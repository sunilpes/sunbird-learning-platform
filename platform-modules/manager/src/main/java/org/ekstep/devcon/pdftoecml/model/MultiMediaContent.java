package org.ekstep.devcon.pdftoecml.model;

import java.net.MalformedURLException;
import java.net.URL;

public class MultiMediaContent {

    private String text;
    private String imageLink;
    private String assetId;

    public MultiMediaContent(String text, String imageLink) {
        this.text = text;
        this.imageLink = imageLink;
    }

    public MultiMediaContent(String text, String imageLink, String assetId) {
        this.text = text;
        this.imageLink = imageLink;
        this.assetId = assetId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getResolvedURL(String link) {
        try {
            URL actualURL = new URL(link);
            return "/assets/public".concat(actualURL.getPath());
        } catch (MalformedURLException ex) {
            System.out.println("unable to resolve the URL!" + ex);
        }
        return null;
    }

}
