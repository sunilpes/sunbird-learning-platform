package org.ekstep.creation.ws.mgr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ekstep.common.dto.Response;
import org.ekstep.common.mgr.BaseManager;
import org.ekstep.common.util.HttpRestUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CreationWorkshopImpl extends BaseManager {

    private static String REGISTRY_VIS_URL = "http://104.211.78.0:8080/read-dev";
    private static String REGISTRY_PROF_URL = "http://52.172.187.3:8080/read-dev";
    private static String ML_URL = "http://13.232.140.12:1123/ML/generateQuestion";
    private static String LP_SEARCH = "https://dev.ekstep.in/api/search/v3/search";

    private static ObjectMapper mapper = new ObjectMapper();



    public Response login(Map<String, Object> reqMap) throws Exception {
        Response resp = null;
        try {
            resp = HttpRestUtil.makePostRequest(REGISTRY_VIS_URL, reqMap, new HashMap<String, String>());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resp;
    }

    public Response getProfile(String id, String type) {

        Response resp = null;
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        Map<String, String> headerParam = new HashMap<String, String>();
        data.put("code", id);
        map.put("id","open-saber.registry.read");
        map.put("request", data);
        try {
            if(null!=id && id.toLowerCase().startsWith("vis"))
            resp = HttpRestUtil.makePostRequest(REGISTRY_VIS_URL, map, headerParam);
            else
                resp = HttpRestUtil.makePostRequest(REGISTRY_PROF_URL, map, headerParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resp;
    }

    public Response createFramework(Map<String, Object> map, String channelId) {
        Response resp = null;
        return resp;
    }

    public Response generate(Map<String, Object> map, String type) {
        Response resp = null;
        return resp;
    }

    public Response generateQB(Map<String, Object> map) {
        Response resp = null;
        Map<String, String> headerParam = new HashMap<String, String>();
        try {
            resp = HttpRestUtil.makeDSPostRequest(ML_URL, map, headerParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resp;
    }

    public Response search(Map<String, Object> map) {
        Response contentResponse = null;
        Map<String, String> headerParam = new HashMap<String, String>();
        List<Map<String, Object>> contents = new ArrayList<>();
        //Map<String, Object> params = new HashMap<>();
        try {
            contentResponse = HttpRestUtil.makePostRequest(LP_SEARCH, map, headerParam);
            if ("OK".equalsIgnoreCase(contentResponse.getResponseCode().toString())) {
                contents.addAll((List<Map<String, Object>>) ((Map<String, Object>) contentResponse.getResult()).get("content"));
                // map.put("objectType","AssessmentItem");
                //Response itemResponse = HttpRestUtil.makePostRequest(LP_SEARCH, map, headerParam);
                //contents.addAll((List<Map<String,Object>>)((Map<String,Object>)itemResponse.getResult()).get("items"));
            }
            Map<String, Object> resDataMap = createStructureBasedOnTopic(contents);
            contentResponse.getResult().remove("content");
            contentResponse.getResult().put("topics", resDataMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentResponse;
    }

    public Map<String, Object> createStructureBasedOnTopic(List<Map<String, Object>> contentList) {
        Map<String, Map<String, List<Map<String, Object>>>> outPut = new HashMap<>();
        Map<String, Object> finalMap = new HashMap<>();

        for (int i = 0; i < contentList.size(); i++) {
            Map<String, Object> contentInnerMap = contentList.get(i);
            Object topic = (Object) contentInnerMap.get("topic");
            if (topic != null) {
                List<String> topicList = (List<String>) topic;
                for (String topicKey : topicList) {
                    if (outPut.containsKey(topicKey)) {
                        Map<String, List<Map<String, Object>>> tempMap = outPut.get(topicKey);
                        Object keyWords = (Object) contentInnerMap.get("keywords");
                        if (keyWords != null) {
                            for (String keyword : (List<String>) keyWords) {
                                if (!getPreDefinekeyWordList().contains(keyword))
                                    continue;
                                if (tempMap.containsKey(keyword)) {
                                    List<Map<String, Object>> keyWordList = tempMap.get(keyword);
                                    keyWordList.add(contentInnerMap);
                                } else {
                                    List<Map<String, Object>> keyWordList = new ArrayList<>();
                                    keyWordList.add(contentInnerMap);
                                    tempMap.put(keyword, keyWordList);
                                }
                            }
                        }

                    } else {
                        Map<String, List<Map<String, Object>>> tempMap = new HashMap<>();
                        List<Map<String, Object>> list = new ArrayList<>();
                        Object keyWords = (Object) contentInnerMap.get("keywords");
                        if (keyWords != null) {
                            List<String> keywordList = (List) keyWords;
                            for (String keyword : keywordList) {
                                if (!getPreDefinekeyWordList().contains(keyword))
                                    continue;
                                list.add(contentInnerMap);
                                tempMap.put(keyword, list);
                            }
                        }
                        outPut.put(topicKey, tempMap);
                    }
                }
            }
        }
        String value = null;
        try {
            value = mapper.writeValueAsString(outPut);
            finalMap = mapper.readValue(value, Map.class);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return finalMap;
    }

    public static List<String> getPreDefinekeyWordList() {
        List<String> keyWordsStaticList = new ArrayList<>();
        keyWordsStaticList.add("dc_primary");
        keyWordsStaticList.add("dc_secondary");
        keyWordsStaticList.add("dc_preparatory");
        keyWordsStaticList.add("dc_practice");
        keyWordsStaticList.add("dc_assessment");
        return keyWordsStaticList;
    }

}
