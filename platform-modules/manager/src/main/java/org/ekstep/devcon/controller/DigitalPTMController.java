package org.ekstep.devcon.controller;

import org.ekstep.common.controller.BaseController;
import org.ekstep.common.dto.Request;
import org.ekstep.common.dto.Response;
import org.ekstep.devcon.mgr.DigitalPTMMgr;
import org.ekstep.telemetry.logger.TelemetryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/ptm/v3")
public class DigitalPTMController extends BaseController {


    @Autowired
    DigitalPTMMgr ptmMgr;


    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Response> create(@RequestBody Map<String, Object> requestMap) {
        String apiId = "ekstep.learning.ptm.create";
        Request request = getRequest(requestMap);
        try {
            Map<String, Object> map = (Map<String, Object>) request.get("ptm");
            Response response = ptmMgr.upsert(map);
            return getResponseEntity(response, apiId, null);
        } catch (Exception e) {
            TelemetryManager.error("Exception: " + e.getMessage(), e);
            return getExceptionResponseEntity(e, apiId, null);
        }
    }


    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/update/{id:.+}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Response> update(@PathVariable(value = "id") String periodId,@RequestBody Map<String,
            Object> requestMap) {
        String apiId = "ekstep.learning.ptm.update";
        Request request = getRequest(requestMap);
        try {
            Map<String, Object> map = (Map<String, Object>) request.get("ptm");
            map.put("identifier", periodId);
            Response response = ptmMgr.upsert(map);
            return getResponseEntity(response, apiId, null);
        } catch (Exception e) {
            TelemetryManager.error("Exception: " + e.getMessage(), e);
            return getExceptionResponseEntity(e, apiId, null);
        }
    }
}
