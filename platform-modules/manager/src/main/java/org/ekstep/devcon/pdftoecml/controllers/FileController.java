package org.ekstep.devcon.pdftoecml.controllers;

import org.ekstep.devcon.pdftoecml.model.ECML.ECML;
import org.ekstep.devcon.pdftoecml.model.MultiMediaContent;
import org.ekstep.devcon.pdftoecml.responses.UploadFileResponse;
import org.ekstep.devcon.pdftoecml.services.ContentTaggingService;
import org.ekstep.devcon.pdftoecml.services.ECMLConverterService;
import org.ekstep.devcon.pdftoecml.services.PDF2Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private PDF2Text pdf2Text;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ContentTaggingService taggingService;

    @Autowired
    private ECMLConverterService ecmlConverterService;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @RequestMapping(value="/content/v3/pdf2ecml/uploadFile", method = RequestMethod.POST)
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        ArrayList<MultiMediaContent> content = new ArrayList<>();
        String JsonECML = "";

        try {
            String text = "";

            String realPathToUpload = request.getServletContext().getRealPath("/uploads");
            if (!new File(realPathToUpload).exists()) {
                new File(realPathToUpload).mkdir();
            }

            String orgName = file.getOriginalFilename();
            String filePath = realPathToUpload + orgName;
            File dest = new File(filePath);
            file.transferTo(dest);
            text = pdf2Text.generateTxtFromPDF(dest.getAbsolutePath());

            ArrayList<String> textList = groupWords(text);

            for(String str: textList) {
                String keywords = taggingService.getTopKeywords(str);
                String imageLink = taggingService.searchImages(keywords);
                String imagePath = taggingService.downloadFileAndSave(imageLink);
                String contentId = taggingService.createContent();
                String assetLink = taggingService.uploadAssetToContent(contentId, imagePath);
                content.add(new MultiMediaContent(str, assetLink, contentId));
            }

            if(content.size() > 0) {
                ECML ecml = ecmlConverterService.generateECML(content);
                JsonECML = ecmlConverterService.toJSON(ecml);
            }

            return new UploadFileResponse(null, null,
                    file.getContentType(), file.getSize(), JsonECML);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Something went wrong when converting pdf to text!");
            return new UploadFileResponse(null, null,
                    file.getContentType(), file.getSize(), "Something went wrong when converting pdf to text!");
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @RequestMapping(value="/content/v3/pdf2text/uploadFile", method = RequestMethod.POST)
    public UploadFileResponse pdf2text(@RequestParam("file") MultipartFile file) {
        ArrayList<MultiMediaContent> content = new ArrayList<>();

        try {
            String text = "";

            String realPathToUpload = request.getServletContext().getRealPath("/uploads");
            if (!new File(realPathToUpload).exists()) {
                new File(realPathToUpload).mkdir();
            }

            String orgName = file.getOriginalFilename();
            String filePath = realPathToUpload + orgName;
            File dest = new File(filePath);
            file.transferTo(dest);
            text = pdf2Text.generateTxtFromPDF(dest.getAbsolutePath());

            return new UploadFileResponse(null, null,
                    file.getContentType(), file.getSize(), text);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Something went wrong when converting pdf to text!");
            return new UploadFileResponse(null, null,
                    file.getContentType(), file.getSize(), "Something went wrong when converting pdf to text!");
        }
    }

    private ArrayList<String> groupWords(String text) {
        String[] lines = text.split("\r\n|\r|\n");
        Integer counter = 0;
        String tmp = "";
        ArrayList<String> acc = new ArrayList<String>();
        for (String a : lines) {
             tmp += a;
             counter++;
             if(counter.equals(6)) {
                 acc.add(tmp);
                 tmp = "";
                 counter = 0;
             }
        }
        return acc;
    }

    @RequestMapping(value="/content/v3/pdf2ecml/uploadMultipleFiles", method = RequestMethod.POST)
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
    }
}