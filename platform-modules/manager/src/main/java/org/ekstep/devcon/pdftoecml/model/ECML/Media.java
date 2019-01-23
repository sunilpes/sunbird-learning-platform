package org.ekstep.devcon.pdftoecml.model.ECML;

public class Media {
    private String id;
    private String plugin;
    private String ver;
    private String src;
    private String type;

    public Media(String id, String plugin, String ver, String src, String type) {
        this.id = id;
        this.plugin = plugin;
        this.ver = ver;
        this.src = src;
        this.type = type;
    }
    // Getter Methods

    public String getId() {
        return id;
    }

    public String getPlugin() {
        return plugin;
    }

    public String getVer() {
        return ver;
    }

    public String getSrc() {
        return src;
    }

    public String getType() {
        return type;
    }

    // Setter Methods

    public void setId(String id) {
        this.id = id;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public void setType(String type) {
        this.type = type;
    }
}