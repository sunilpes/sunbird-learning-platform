package org.ekstep.devcon.pdftoecml.model.ECML;

import java.util.ArrayList;

public class PluginManifest {
    ArrayList <Plugin> plugin = new ArrayList<Plugin>();

    public void addPlugin(Plugin plugin) {
        this.plugin.add(plugin);
    }
    // Getter Methods
    // Setter Methods
}
