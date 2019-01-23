package org.ekstep.devcon.pdftoecml.model.ECML;

import java.util.ArrayList;

public class StageManifest {

    private ArrayList<StageMedia> media = new ArrayList<>();

    public ArrayList<StageMedia> getMedia() {
        return this.media;
    }

    public void setMedia(StageMedia media) {
        this.media.add(media);
    }
}
