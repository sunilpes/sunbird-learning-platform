package org.ekstep.creation.ws.controller;

import org.ekstep.common.controller.BaseController;
import org.ekstep.common.dto.Request;
import org.ekstep.common.dto.Response;
import org.ekstep.creation.ws.mgr.CreationWorkshopImpl;
import org.ekstep.telemetry.logger.TelemetryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Controller
@RequestMapping("/devcon/v3")
public class CreationWorkshopController extends BaseController {

    @Autowired
    private CreationWorkshopImpl creationWorkshopImpl;

    @RequestMapping(value = "/login/{id:.+}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Response> login(@PathVariable(value = "id") String visitorId)
    {
        String apiId = "ekstep.devcon.login";
        Response response;
        try {
            TelemetryManager.log(
                    "Calling the Manager for fetching visitor details : [" + visitorId + "]" );
            response = creationWorkshopImpl.login(visitorId);
            return getResponseEntity(response, apiId, null);
        } catch (Exception e) {
            TelemetryManager.error("Exception: " + e.getMessage(), e);
            return getExceptionResponseEntity(e, apiId, null);
        }
    }

    @RequestMapping(value = "/profile/read/{id:.+}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Response> profile(@PathVariable(value = "id") String id,
                                          @RequestParam(value = "type", required = false) String type)
    {
        String apiId = "ekstep.devcon.profile.read";
        Response response;
        try {
            TelemetryManager.log(
                    "Calling the Manager for fetching profile details : [" + id + "]" );
            response = creationWorkshopImpl.getProfile(id,type);
            return getResponseEntity(response, apiId, null);
        } catch (Exception e) {
            TelemetryManager.error("Exception: " + e.getMessage(), e);
            return getExceptionResponseEntity(e, apiId, null);
        }
    }

    @RequestMapping(value = "/framework/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Response> createFramework(@RequestBody Map<String, Object> requestMap,
                                                    @RequestHeader(value = "X-Channel-Id") String channelId) {
        String apiId = "ekstep.devcon.framework.create";
        Request request = getRequest(requestMap);
        try {
            Map<String, Object> map = (Map<String, Object>) request.get("framework");
            Response response = creationWorkshopImpl.createFramework(map, channelId);
            return getResponseEntity(response, apiId, null);
        } catch (Exception e) {
            TelemetryManager
                    .error("Exception Occured while creating framework (Create Framework API): " + e.getMessage(), e);
            return getExceptionResponseEntity(e, apiId, null);
        }
    }

    @RequestMapping(value = "/generate/multimedia", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Response> generate(@RequestBody Map<String, Object> requestMap,
                                             @RequestParam(value = "type", required = true) String type,
                                                    @RequestHeader(value = "X-Channel-Id") String channelId) {
        String apiId = "ekstep.devcon.generate.multimedia."+type;
        Request request = getRequest(requestMap);
        try {
            Map<String, Object> map = (Map<String, Object>) request.get("framework");
            Response response = creationWorkshopImpl.generate(map, type);
            return getResponseEntity(response, apiId, null);
        } catch (Exception e) {
            TelemetryManager
                    .error("Exception Occured while creating framework (Create Framework API): " + e.getMessage(), e);
            return getExceptionResponseEntity(e, apiId, null);
        }
    }


    protected String getAPIVersion() {
        return API_VERSION_3;
    }
}
