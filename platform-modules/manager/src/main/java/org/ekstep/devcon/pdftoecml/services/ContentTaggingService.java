package org.ekstep.devcon.pdftoecml.services;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.ekstep.devcon.pdftoecml.model.TagMeAnnotations;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.ekstep.devcon.pdftoecml.model.TagMeModel;
import sun.net.www.http.HttpClient;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.OK;


@Service
public class ContentTaggingService {

    @Autowired
    ServletContext context;

    public String getTopKeywords(String text) {
        String keywords = "";
        String tagMeURL = String.format("https://tagme.d4science.org/tagme/tag?lang=en&gcube-token=1e1f2881-62ec-4b3e-9036-9efe89347991-843339462&text=%s&epsilon=0.6", text);
        RestTemplate restTemplate = new RestTemplate();
        TagMeModel result = restTemplate.getForObject(tagMeURL, TagMeModel.class);
        if (result != null) {
            ArrayList<TagMeAnnotations> annotations = result.getAnnotations();
            annotations.sort((o1, o2) -> Float.compare(o1.getRho(), o2.getRho()));
            for (TagMeAnnotations annot: annotations.stream().limit(10).collect(Collectors.toList()) ) {
                keywords += annot.getSpot() + " ";
            }
        }
        return keywords;
    }

    public String searchImages(String query) {
        RestTemplate restTemplate = new RestTemplate();
        String googleCustomSearchURL = String.format("https://www.googleapis.com/customsearch/v1?q=%s&searchType=image&cx=013312532056745725691:-vond_hzpda&fileType=png&filter=1&imgColorType=color&imgSize=medium&imgType=photo&num=1&safe=active&searchType=image&key=AIzaSyDdmvR49pdeDZzy9n9Pto3PMHUtQacReqc", query);
        JSONParser parse = new JSONParser();

        ResponseEntity<String> result = restTemplate.getForEntity(googleCustomSearchURL, String.class);
        if (result.getStatusCode() == OK) {
            try {
                String body = result.getBody();
                JSONObject searchResponse = (JSONObject)parse.parse(body);
                JSONArray items = (JSONArray)searchResponse.get("items");
                if (items.size() > 0) {
                    JSONObject imageObj = (JSONObject)items.get(0);
                    return (String) imageObj.get("link");
                }
            } catch(ParseException ex) {
                System.out.println("unable to parse the request from Google custom search API!");
            }
        }
        return null;
    }

    public String createContent() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-channel-id", "in.ekstep");
        String requestBody = "{\"request\":{\"content\":{\"mimeType\":\"image/png\",\"contentType\":\"Asset\",\"name\":\"Devcon Test Image\",\"mediaType\":\"image\",\"code\":\"devcon\"}}}";

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        String createContentURL = "https://dev.ekstep.in/api/content/v3/create";

        ResponseEntity<String> result = restTemplate.postForEntity(createContentURL, entity, String.class);
        if (result.getStatusCode() == OK) {
            try {
                String body = result.getBody();
                JSONParser parse = new JSONParser();
                JSONObject response = (JSONObject)parse.parse(body);
                JSONObject obj = (JSONObject)response.get("result");
                return (String) obj.get("node_id");
            } catch(ParseException ex) {
                System.out.println("unable to parse the request from create content API!");
            }
        }
        return null;
    }


    public String uploadAssetToContent(String contentId, String imageLink) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        Resource resource = new FileSystemResource(imageLink);
        requestBody.add("file", resource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        String uploadContentURL = String.format("https://dev.ekstep.in/api/content/v3/upload/%s", contentId);
        ResponseEntity<String> result = restTemplate.postForEntity(uploadContentURL, requestEntity, String.class);
        if (result.getStatusCode() == OK) {
            try {
                String body = result.getBody();
                JSONParser parse = new JSONParser();
                JSONObject response = (JSONObject)parse.parse(body);
                JSONObject obj = (JSONObject)response.get("result");
                return (String) obj.get("content_url");
            } catch(ParseException ex) {
                System.out.println("unable to parse the request from upload content API!");
            }
        }
        return null;

    }

    public String downloadFileAndSave(String link) {
        try {
            UUID uuid = UUID.randomUUID();

            CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);

            RestTemplate restTemplate = new RestTemplate(requestFactory);
            System.out.println("downloading file.... " + link);
            ResponseEntity<byte[]> imageBytes = restTemplate.getForEntity(link, byte[].class);
            String pathToSave = context.getRealPath("/uploads");
            if (!new File(pathToSave).exists()) {
                new File(pathToSave).mkdir();
            }
            String imagePath = pathToSave + "/" + uuid + ".png";
            File myFile = new File(imagePath);
            try (FileOutputStream fos = new FileOutputStream(myFile)) {
                fos.write(imageBytes.getBody());
            }
            return imagePath;
        } catch (HttpServerErrorException | IOException ex) {
            System.out.println("unable to download the resource from web"+ ex);
        }
        return null;
    }
}
