package org.ekstep.devcon.pdftoecml.model.ECML;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ImagePlugin {
    private String asset;
    private float x;
    private float y;
    private float w;
    private float h;
    private float rotate;
    private transient ArrayList<Media> media = new ArrayList<>();

    @SerializedName("z-index")
    private float zIndex;
    private String id;

    @SerializedName("config")
    Config ConfigObject;

    public ImagePlugin(float x, float y, float w, float h, String id, String asset) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.id = id;
        this.asset = asset;
    }

    // Getter Methods

    public String getAsset() {
        return asset;
    }

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

    public float getRotate() {
        return rotate;
    }

    public float getZIndex() {
        return zIndex;
    }

    public String getId() {
        return id;
    }

    public Config getConfig() {
        return ConfigObject;
    }

    // Setter Methods

    public void setAsset(String asset) {
        this.asset = asset;
    }

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

    public void setRotate(float rotate) {
        this.rotate = rotate;
    }

    public void setZIndex(float zIndex) {
        this.zIndex = zIndex;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setConfig(Config configObject) {
        this.ConfigObject = configObject;
    }

    public void addPluginMedia(Media media) {
        this.media.add(media);
    }

    public ArrayList<Media> getPluginMedia() {
        return this.media;
    }
}