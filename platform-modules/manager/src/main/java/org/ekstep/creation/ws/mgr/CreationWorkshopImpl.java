package org.ekstep.creation.ws.mgr;

import org.ekstep.common.dto.Response;
import org.ekstep.common.mgr.BaseManager;
import org.ekstep.common.util.HttpRestUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CreationWorkshopImpl extends BaseManager {

    private static String REGISTRY_URL = "http://localhost:8080/read-dev";
    private static String ML_URL = "http://localhost:8080/ml";

    public Response login(String id) throws Exception{
        Response resp = null;
        return resp;
    }

    public Response getProfile(String id, String type){

        Response resp = null;
        Map<String,Object> map = new HashMap<String,Object>();
        Map<String,Object> data = new HashMap<String,Object>();
        Map<String,String> headerParam = new HashMap<String,String>();
        data.put("code",id);
        map.put("request",map);
        try{
            resp = HttpRestUtil.makePostRequest(REGISTRY_URL, map, headerParam);
        }catch(Exception e){
            e.printStackTrace();
        }
        return resp;
    }

    public Response createFramework(Map<String, Object> map, String channelId){
        Response resp = null;
        return resp;
    }

    public Response generate(Map<String, Object> map, String type){
        Response resp = null;
        return resp;
    }

    public Response generateQB(Map<String, Object> map){
        Response resp = null;
        Map<String,String> headerParam = new HashMap<String,String>();
        try{
            resp = HttpRestUtil.makePostRequest(ML_URL, map, headerParam);
        }catch(Exception e){
            e.printStackTrace();
        }
        return resp;
    }

}
