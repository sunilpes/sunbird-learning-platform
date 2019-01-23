package org.ekstep.devcon.pdftoecml.model.ECML;

public class Plugin {
    private String id;
    private String ver;
    private String type;
    private String depends;

    public Plugin(String id, String ver, String type, String depends) {
        this.id = id;
        this.ver = ver;
        this.type = type;
        this.depends = depends;
    }

    // Getter Methods

    public String getId() {
        return id;
    }

    public String getVer() {
        return ver;
    }

    public String getType() {
        return type;
    }

    public String getDepends() {
        return depends;
    }

    // Setter Methods

    public void setId(String id) {
        this.id = id;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDepends(String depends) {
        this.depends = depends;
    }
}