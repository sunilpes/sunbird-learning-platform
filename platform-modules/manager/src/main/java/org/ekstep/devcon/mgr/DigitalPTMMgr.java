package org.ekstep.devcon.mgr;

import org.ekstep.common.dto.Response;
import org.ekstep.common.mgr.BaseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DigitalPTMMgr extends BaseManager {

    @Autowired
    DigitalPtmStore dbStore;


    public Response upsert(Map<String, Object> map) throws Exception {
        dbStore.upsert(map);
        Response respone = getSuccessResponse();
        respone.put("identifier", map.get("idenitifer"));
        return respone;
    }
}
