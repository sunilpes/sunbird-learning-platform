package org.ekstep.devcon.pdftoecml.model;

import java.util.ArrayList;

public class TagMeModel {
    private String test;
    private ArrayList<TagMeAnnotations> annotations = new ArrayList<>();
    private float time;
    private String api;
    private String lang;
    private String timestamp;


    // Getter Methods

    public String getTest() {
        return test;
    }

    public float getTime() {
        return time;
    }

    public String getApi() {
        return api;
    }

    public String getLang() {
        return lang;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public ArrayList<TagMeAnnotations> getAnnotations() {
        return annotations;
    }

    // Setter Methods

    public void setTest( String test ) {
        this.test = test;
    }

    public void setTime( float time ) {
        this.time = time;
    }

    public void setApi( String api ) {
        this.api = api;
    }

    public void setLang( String lang ) {
        this.lang = lang;
    }

    public void setTimestamp( String timestamp ) {
        this.timestamp = timestamp;
    }
}