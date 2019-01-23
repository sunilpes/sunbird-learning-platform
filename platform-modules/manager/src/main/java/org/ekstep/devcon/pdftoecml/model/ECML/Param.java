package org.ekstep.devcon.pdftoecml.model.ECML;

public class Param {
    private String name;
    private String value;

    public Param(String name, String value) {
        this.name = name;
        this.value = value;
    }
    // Getter Methods

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    // Setter Methods

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
