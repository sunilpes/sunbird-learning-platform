package org.ekstep.devcon.pdftoecml.model;

public class TagMeAnnotations {
    private String spot;
    private float start;
    private float link_probability;
    private float rho;
    private float end;
    private float id;
    private String title;


    // Getter Methods

    public String getSpot() {
        return spot;
    }

    public float getStart() {
        return start;
    }

    public float getLink_probability() {
        return link_probability;
    }

    public float getRho() {
        return rho;
    }

    public float getEnd() {
        return end;
    }

    public float getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    // Setter Methods

    public void setSpot(String spot) {
        this.spot = spot;
    }

    public void setStart(float start) {
        this.start = start;
    }

    public void setLink_probability(float link_probability) {
        this.link_probability = link_probability;
    }

    public void setRho(float rho) {
        this.rho = rho;
    }

    public void setEnd(float end) {
        this.end = end;
    }

    public void setId(float id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
