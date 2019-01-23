package org.ekstep.devcon.pdftoecml.model.ECML;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TextPlugin {
    private float x;
    private float y;
    private float minWidth = 20;
    private float w;
    private float maxWidth = 500;
    private String fill = "#000000";
    private String fontStyle = "normal";
    private String fontWeight = "normal";
    private String stroke = "rgba(255, 255, 255, 0)";
    private float strokeWidth = 1;
    private float opacity = 1;
    private boolean editable = false;
    private String version = "V2";
    private float offsetY;
    private float h;
    private float rotate = 0;
    private String textType = "text";
    private float lineHeight = 1;
    private float zIndex = 0;
    private String font = "NotoSans";
    private float fontsize = 48;
    private String weight = "";
    private String id;

    @SerializedName("config")
    private Config ConfigObject;

    public TextPlugin(float x, float y, float w, float h, String id) {
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

    public float getMinWidth() {
        return minWidth;
    }

    public float getW() {
        return w;
    }

    public float getMaxWidth() {
        return maxWidth;
    }

    public String getFill() {
        return fill;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public String getFontWeight() {
        return fontWeight;
    }

    public String getStroke() {
        return stroke;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public float getOpacity() {
        return opacity;
    }

    public boolean getEditable() {
        return editable;
    }

    public String getVersion() {
        return version;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public float getH() {
        return h;
    }

    public float getRotate() {
        return rotate;
    }

    public String getTextType() {
        return textType;
    }

    public float getLineHeight() {
        return lineHeight;
    }

    public float getZIndex() {
        return zIndex;
    }

    public String getFont() {
        return font;
    }

    public float getFontsize() {
        return fontsize;
    }

    public String getWeight() {
        return weight;
    }

    public String getId() {
        return id;
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

    public void setMinWidth(float minWidth) {
        this.minWidth = minWidth;
    }

    public void setW(float w) {
        this.w = w;
    }

    public void setMaxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
    }

    public void setFill(String fill) {
        this.fill = fill;
    }

    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    public void setFontWeight(String fontWeight) {
        this.fontWeight = fontWeight;
    }

    public void setStroke(String stroke) {
        this.stroke = stroke;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public void setH(float h) {
        this.h = h;
    }

    public void setRotate(float rotate) {
        this.rotate = rotate;
    }

    public void setTextType(String textType) {
        this.textType = textType;
    }

    public void setLineHeight(float lineHeight) {
        this.lineHeight = lineHeight;
    }

    public void setZIndex(float zIndex) {
        this.zIndex = zIndex;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public void setFontsize(float fontsize) {
        this.fontsize = fontsize;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setConfig(Config configObject) {
        this.ConfigObject = configObject;
    }

    public static ArrayList<Media> getPluginMedia() {
        ArrayList<Media> mediaList = new ArrayList<Media>();
        mediaList.add(new Media(
                "org.ekstep.text",
                "org.ekstep.text",
                "1.2",
                "/content-plugins/org.ekstep.text-1.2/renderer/supertextplugin.js",
                "plugin"));
        mediaList.add(new Media(
                "org.ekstep.text_manifest",
                "org.ekstep.text",
                "1.2",
                "/content-plugins/org.ekstep.text-1.2/manifest.json",
                "json"
        ));
        return mediaList;
    }
}
