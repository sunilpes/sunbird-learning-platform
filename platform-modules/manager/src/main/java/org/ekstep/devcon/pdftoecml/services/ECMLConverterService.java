package org.ekstep.devcon.pdftoecml.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.ekstep.devcon.pdftoecml.model.ECML.*;
import org.ekstep.devcon.pdftoecml.model.MultiMediaContent;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class ECMLConverterService {

    public ECML generateECML(ArrayList<MultiMediaContent> contents) {
        ECML ecml = new ECML();
        Theme theme = new Theme("theme","1.0", "", 2);
        String configData = "{\"opacity\":100,\"strokeWidth\":1,\"stroke\":\"rgba(255, 255, 255, 0)\",\"autoplay\":false,\"visible\":true,\"color\":\"#FFFFFF\",\"genieControls\":false,\"instructions\":\"\"}";
        Config StageConfig = new Config(configData);
        Stage firstStage = null;
        Stage prevStage = null;
        PluginManifest pluginManifest = new PluginManifest();
        TextPlugin textPlugin = null;
        ImagePlugin imagePlugin = null;
        ArrayList<Media> media = new ArrayList<>();

        for (MultiMediaContent content: contents) {
            Stage stage = createStage();
            stage.setConfig(StageConfig);
            if(content.getText() != null && !content.getText().isEmpty()) {
                textPlugin = createTextPlugin(content.getText());
                stage.addTextPlugin(textPlugin);
            }

            if(content.getAssetId() != null && !content.getAssetId().isEmpty()) {
                imagePlugin = createImagePlugin(content.getAssetId());
                Media imageMedia = new Media(content.getAssetId(), null, null,content.getResolvedURL(content.getImageLink()), "image");
                media.add(imageMedia);
                stage.addImagePlugin(imagePlugin);
                stage.addStageMedia(new StageMedia(content.getAssetId()));
            }

            if (firstStage == null) {
                theme.setStartStage(stage.getId());
            }
            if (prevStage != null) {
                stage.setParam(new Param("previous", prevStage.getId()));
                prevStage.setParam(new Param("next", stage.getId()));
            }
            this.addStage(stage, theme);
            prevStage = stage;
            firstStage = stage;
        }
        pluginManifest.addPlugin(new Plugin("org.ekstep.text", "1.2", "plugin", ""));
        pluginManifest.addPlugin(new Plugin("org.ekstep.navigation", "1.0", "plugin", ""));
        theme.setPluginManifest(pluginManifest);

        media.addAll(this.getNavPluginMedia());
        media.addAll(TextPlugin.getPluginMedia());

        Manifest manifest = new Manifest();
        manifest.setMedia(media);
        theme.setManifest(manifest);

        ecml.setTheme(theme);
        return ecml;
    }

    public String toJSON(ECML ecml) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(ecml);
    }


    private ArrayList<Media> getNavPluginMedia() {
        ArrayList<Media> mediaList = new ArrayList<Media>();
        mediaList.add(new Media(
                "bec245b8-d5ae-4e33-a295-37b8be0e5e0b",
                "org.ekstep.navigation",
                "1.0",
                "/content-plugins/org.ekstep.navigation-1.0/renderer/controller/navigation_ctrl.js",
                "js"));
        mediaList.add(new Media(
                "06203ce1-0e74-4e2b-905e-31383b131d94",
                "org.ekstep.navigation",
                "1.0",
                "/content-plugins/org.ekstep.navigation-1.0/renderer/templates/navigation.html",
                "js"
        ));
        mediaList.add(new Media(
                "org.ekstep.navigation",
                "org.ekstep.navigation",
                "1.0",
                "/content-plugins/org.ekstep.navigation-1.0/renderer/plugin.js",
                "plugin"
        ));
        mediaList.add(new Media(
                "org.ekstep.navigation_manifest",
                "org.ekstep.navigation",
                "1.0",
                "/content-plugins/org.ekstep.navigation-1.0/manifest.json",
                "json"
        ));
        return mediaList;
    }

    private TextPlugin createTextPlugin(String text) {
        UUID uuid = UUID.randomUUID();
        String cdata = String.format("{\"opacity\":100,\"strokeWidth\":1,\"stroke\":\"rgba(255, 255, 255, 0)\",\"autoplay\":false,\"visible\":true,\"text\":\"%s\",\"color\":\"#000000\",\"fontfamily\":\"NotoSans\",\"fontsize\":18,\"fontweight\":false,\"fontstyle\":false,\"align\":\"left\"}", text);
        Config textConfig = new Config(cdata);
        TextPlugin plugin = new TextPlugin(3, 5, 50, 90, uuid.toString());
        plugin.setConfig(textConfig);
        return plugin;
    }

    private ImagePlugin createImagePlugin(String assetId) {
        UUID uuid = UUID.randomUUID();
        String cdata = "{\"opacity\":100,\"strokeWidth\":1,\"stroke\":\"rgba(255, 255, 255, 0)\",\"autoplay\":false,\"visible\":true}";
        Config imageConfig = new Config(cdata);
        ImagePlugin plugin = new ImagePlugin(60, 5, 35, 60, uuid.toString(), assetId);
        plugin.setConfig(imageConfig);
        return plugin;
    }

    private void addStage(Stage stage, Theme theme) {
        theme.addStage(stage);
    }

    private Stage createStage() {
        UUID uuid = UUID.randomUUID();
        Stage stage = new Stage(0,0,100,100, uuid.toString());
        return stage;
    }
}
