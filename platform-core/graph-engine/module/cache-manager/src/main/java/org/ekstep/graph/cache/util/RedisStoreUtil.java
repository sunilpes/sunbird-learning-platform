package org.ekstep.graph.cache.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.ekstep.common.exception.ServerException;
import org.ekstep.graph.cache.exception.GraphCacheErrorCodes;
import org.ekstep.graph.dac.enums.GraphDACParams;
import org.ekstep.telemetry.logger.TelemetryManager;
import redis.clients.jedis.Jedis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.ekstep.graph.cache.factory.JedisFactory.getRedisConncetion;
import static org.ekstep.graph.cache.factory.JedisFactory.returnConnection;

public class RedisStoreUtil {

	private static ObjectMapper mapper = new ObjectMapper();

	public static void saveNodeProperty(String graphId, String objectId, String nodeProperty, String propValue) {

		Jedis jedis = getRedisConncetion();
		try {
			String redisKey = CacheKeyGenerator.getNodePropertyKey(graphId, objectId, nodeProperty);
			jedis.set(redisKey, propValue);
		} catch (Exception e) {
			throw new ServerException(GraphCacheErrorCodes.ERR_CACHE_SAVE_PROPERTY_ERROR.name(), e.getMessage());
		} finally {
			returnConnection(jedis);
		}
	}


	public static void save(String key, String value, int ttl) {

		Jedis jedis = getRedisConncetion();
		try {
			jedis.set(key, value);
			if(ttl > 0)
				jedis.expire(key, ttl);
		} catch (Exception e) {
			throw new ServerException(GraphCacheErrorCodes.ERR_CACHE_SAVE_PROPERTY_ERROR.name(), e.getMessage());
		} finally {
			returnConnection(jedis);
		}
	}

	public static String get(String key) {
		Jedis jedis = getRedisConncetion();
		try {
			return jedis.get(key);
		} catch (Exception e) {
			throw new ServerException(GraphCacheErrorCodes.ERR_CACHE_GET_PROPERTY_ERROR.name(), e.getMessage());
		} finally {
			returnConnection(jedis);
		}
	}

	public static String getNodeProperty(String graphId, String objectId, String nodeProperty) {

		Jedis jedis = getRedisConncetion();
		try {
			String redisKey = CacheKeyGenerator.getNodePropertyKey(graphId, objectId, nodeProperty);
			String value = jedis.get(redisKey);
			return value;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			throw new ServerException(GraphCacheErrorCodes.ERR_CACHE_GET_PROPERTY_ERROR.name(), e.getMessage());
		} finally {
			returnConnection(jedis);
		}
	}

	public static void saveNodeProperties(String graphId, String objectId, Map<String, Object> metadata) {
		Jedis jedis = getRedisConncetion();
		try {
			for (Entry<String, Object> entry : metadata.entrySet()) {
				String propertyName = entry.getKey();
				String propertyValue = entry.getValue().toString();

				String redisKey = CacheKeyGenerator.getNodePropertyKey(graphId, objectId, propertyName);
				jedis.set(redisKey, propertyValue);
			}

		} catch (Exception e) {
			throw new ServerException(GraphCacheErrorCodes.ERR_CACHE_SAVE_PROPERTY_ERROR.name(), e.getMessage());
		} finally {
			returnConnection(jedis);
		}
	}

	public static void deleteNodeProperties(String graphId, String objectId) {
		Jedis jedis = getRedisConncetion();
		try {

			String versionKey = CacheKeyGenerator.getNodePropertyKey(graphId, objectId,
					GraphDACParams.versionKey.name());
			String consumerId = CacheKeyGenerator.getNodePropertyKey(graphId, objectId,
					GraphDACParams.consumerId.name());
			jedis.del(versionKey, consumerId);

		} catch (Exception e) {
			throw new ServerException(GraphCacheErrorCodes.ERR_CACHE_SAVE_PROPERTY_ERROR.name(), e.getMessage());
		} finally {
			returnConnection(jedis);
		}
	}

	public static void deleteAllNodeProperty(String graphId, String propertyName) {
		Jedis jedis = getRedisConncetion();
		try {

			String delKeysPattern = CacheKeyGenerator.getAllNodePropertyKeysPattern(graphId, propertyName);
			Set<String> keys = jedis.keys(delKeysPattern);
			if (keys != null && keys.size() > 0) {
				List<String> keyList = new ArrayList<>(keys);
				jedis.del(keyList.toArray(new String[keyList.size()]));
			}

		} catch (Exception e) {
			throw new ServerException(GraphCacheErrorCodes.ERR_CACHE_SAVE_PROPERTY_ERROR.name(), e.getMessage());
		} finally {
			returnConnection(jedis);
		}
	}

	public static Double getNodePropertyIncVal(String graphId, String objectId, String nodeProperty) {

		Jedis jedis = getRedisConncetion();
		try {
			String redisKey = CacheKeyGenerator.getNodePropertyKey(graphId, objectId, nodeProperty);
			double inc = 1.0;
			double value = jedis.incrByFloat(redisKey, inc);
			return value;
		} catch (Exception e) {
			throw new ServerException(GraphCacheErrorCodes.ERR_CACHE_GET_PROPERTY_ERROR.name(), e.getMessage());
		} finally {
			returnConnection(jedis);
		}
	}

	
	// TODO: always considering object as string. need to change this.
	public static void saveList(String key, List<Object> values) {
		Jedis jedis = getRedisConncetion();
		try {
			jedis.del(key);
			for (Object val : values) {
				jedis.sadd(key, (String) val);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			returnConnection(jedis);
		}
	}
	
	public static List<Object> getList(String key) {
		Jedis jedis = getRedisConncetion();
		try {
			 Set<String> set = jedis.smembers(key);
			 List<Object> list = new ArrayList<Object>(set);
			return list;
		} catch (Exception e) {
			throw new ServerException(GraphCacheErrorCodes.ERR_CACHE_GET_PROPERTY_ERROR.name(), e.getMessage());
		} finally {
			returnConnection(jedis);
		}
	}

	/**
	 * This Method Save Data to Redis Cache With ttl.
	 *
	 * @param identifier
	 * @param data
	 * @param ttl
	 */
	public static void saveData(String identifier, Map<String, Object> data, int ttl) {
		try {
			save(identifier, mapper.writeValueAsString(data), ttl);
		} catch (Exception e) {
			TelemetryManager.error("Error while saving hierarchy to Redis for Identifier : " + identifier + " | Error is : ", e);
		}
	}

	public static void saveCompressedData(String identifier, Map<String, Object> data, int ttl) {
		try {
			save(identifier, compressString(mapper.writeValueAsString(data)), ttl);
		} catch (Exception e) {
			TelemetryManager.error("Error while saving hierarchy to Redis for Identifier : " + identifier + " | Error is : ", e);
		}
	}

	public static String getUncompressed(String key) {
		Jedis jedis = getRedisConncetion();
		try {
			long start = System.currentTimeMillis();
			String value = jedis.get(key);
			System.out.println("Time taken fetch data from redis for : " + key + " : " + (System.currentTimeMillis() - start));
			start = System.currentTimeMillis();
			String uncompressedValue = uncompressString(value);
			System.out.println("Time taken uncompress data for : " + key + " : " + (System.currentTimeMillis() - start));
			return uncompressedValue;
		} catch (Exception e) {
			throw new ServerException(GraphCacheErrorCodes.ERR_CACHE_GET_PROPERTY_ERROR.name(), e.getMessage());
		} finally {
			returnConnection(jedis);
		}
	}

	public static String compressString(String srcTxt) throws IOException {
		ByteArrayOutputStream baos = null;
		GZIPOutputStream gzos = null;
		try {
			byte[] bytes = srcTxt.getBytes();
			baos = new ByteArrayOutputStream();
			gzos = new GZIPOutputStream(baos);
			gzos.write(bytes, 0, bytes.length);
			gzos.finish();
			gzos.close();
			byte[] compressedByteArray = baos.toByteArray();
			baos.close();
			return Base64.encodeBase64String(compressedByteArray);
		} finally {
			IOUtils.closeQuietly(gzos);
			IOUtils.closeQuietly(baos);
		}
	}

	private static boolean isCompressed(byte[] bytes)
	{
		if ((bytes == null) || (bytes.length < 2)) {
			return false;
		} else {
			return ((bytes[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (bytes[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8)));
		}
	}

	public static String uncompressString(String zippedBase64Str)
			throws IOException {
		String result = null;
		byte[] bytes;
		if(Base64.isBase64(zippedBase64Str)) {
			bytes = Base64.decodeBase64(zippedBase64Str);
		}else {
			bytes = zippedBase64Str.getBytes();
		}

		if(!isCompressed(bytes))
			return zippedBase64Str;
		ByteArrayInputStream bais = null;
		GZIPInputStream gzis = null;
		ByteArrayOutputStream baos = null;
		try {
			bais = new ByteArrayInputStream(bytes);
			gzis = new GZIPInputStream(bais);
			baos = new ByteArrayOutputStream();

			IOUtils.copy(gzis, baos);
			gzis.close();
			bais.close();
			byte[] resBytes = baos.toByteArray();
			result = new String(resBytes, StandardCharsets.UTF_8);
			//result = IOUtils.toString(zi, "UTF-8");

		} finally {
			IOUtils.closeQuietly(gzis);
			IOUtils.closeQuietly(bais);
			IOUtils.closeQuietly(baos);
		}
		return result;
	}
}
