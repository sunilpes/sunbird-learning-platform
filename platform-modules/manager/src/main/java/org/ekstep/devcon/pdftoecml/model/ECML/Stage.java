package org.ekstep.devcon.pdftoecml.model.ECML;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Stage {
    private float x;
    private float y;
    private float w;
    private float h;
    private String id;
    private String rotate = null;

    @SerializedName("config")
    Config ConfigObject;

    ArrayList<Param> param = new ArrayList<>();

    @SerializedName("org.ekstep.text")
    ArrayList<TextPlugin> textPlugin = new ArrayList <TextPlugin> ();

    @SerializedName("image")
    ArrayList<ImagePlugin> imagePlugins = new ArrayList<>();

    @SerializedName("manifest")
    StageManifest stageManifest = new StageManifest();

    public Stage(float x, float y, float w, float h, String id) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.id = id;
    }

    // Getter Methods

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getW() {
        return w;
    }

    public float getH() {
        return h;
    }

    public String getId() {
        return id;
    }

    public String getRotate() {
        return rotate;
    }

    public Config getConfig() {
        return ConfigObject;
    }

    // Setter Methods

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setW(float w) {
        this.w = w;
    }

    public void setH(float h) {
        this.h = h;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRotate(String rotate) {
        this.rotate = rotate;
    }

    public void setConfig(Config configObject) {
        this.ConfigObject = configObject;
    }

    public void addTextPlugin(TextPlugin plugin) {
        this.textPlugin.add(plugin);
    }

    public void addImagePlugin(ImagePlugin plugin) {
        this.imagePlugins.add(plugin);
    }

    public void setParam(Param param) {
        this.param.add(param);
    }

    public void addStageMedia(StageMedia stageMedia) {
        this.stageManifest.setMedia(stageMedia);
    }

    public StageManifest getStageManifest() {
        return this.stageManifest;
    }
}
