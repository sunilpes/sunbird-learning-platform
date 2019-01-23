package org.ekstep.devcon.pdftoecml.model.ECML;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Theme {
    private String id;
    private String version;
    private String startStage;
    ArrayList<Stage> stage = new ArrayList<Stage>();

    @SerializedName("manifest")
    Manifest manifestObject;

    @SerializedName("plugin-manifest")
    PluginManifest pluginManifestObject;
    private float compatibilityVersion;

    public Theme(String id, String version, String startStage, float compatibilityVersion) {
        this.id = id;
        this.version = version;
        this.startStage = startStage;
        this.compatibilityVersion = compatibilityVersion;
    }
    // Getter Methods

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public String getStartStage() {
        return startStage;
    }

    public Manifest getManifest() {
        return manifestObject;
    }

    public PluginManifest getPluginManifest() {
        return pluginManifestObject;
    }

    public float getCompatibilityVersion() {
        return compatibilityVersion;
    }

    public ArrayList<Stage> getStage() {
        return stage;
    }

    // Setter Methods

    public void setId(String id) {
        this.id = id;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setStartStage(String startStage) {
        this.startStage = startStage;
    }

    public void setManifest(Manifest manifestObject) {
        this.manifestObject = manifestObject;
    }

    public void setPluginManifest(PluginManifest pluginManifestObject) {
        this.pluginManifestObject = pluginManifestObject;
    }

    public void setCompatibilityVersion(float compatibilityVersion) {
        this.compatibilityVersion = compatibilityVersion;
    }

    public void setStage(ArrayList<Stage> stage) {
        this.stage = stage;
    }

    public void addStage(Stage stage) {
        this.stage.add(stage);
    }
}
