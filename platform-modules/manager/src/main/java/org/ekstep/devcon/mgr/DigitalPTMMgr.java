package org.ekstep.devcon.mgr;

import org.apache.commons.lang3.StringUtils;
import org.ekstep.common.dto.Response;
import org.ekstep.common.exception.ResponseCode;
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
        respone.put("identifier", map.get("visitor"));
        return respone;
    }

    public Response read(String id, String visitorId) throws Exception {
        if (StringUtils.isBlank(id))
            return ERROR("ERR_INVALID_REQUEST",
                    "Invalid Request.", ResponseCode.CLIENT_ERROR);
        Map<String, Object> record = dbStore.readData(id, visitorId);
        Response resp = getSuccessResponse();
        resp.put("ptm", record);
        return resp;
    }
}
