package org.ekstep.learning.util;

import org.ekstep.telemetry.logger.TelemetryManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pradyumna
 */
public class FrameworkCache {

    private static Map<String, Object> frameworkCache = new HashMap<>();

    public static void save(String frameworkId, Object hierarchy) {
        frameworkCache.put(frameworkId, hierarchy);
        TelemetryManager.log("Saved framework hierarchy for : " + frameworkId);
    }

    public static Object get(String frameworkId) {
        TelemetryManager.log("Fetching framework from cache for : " + frameworkId);
        return frameworkCache.get(frameworkId);
    }
}
