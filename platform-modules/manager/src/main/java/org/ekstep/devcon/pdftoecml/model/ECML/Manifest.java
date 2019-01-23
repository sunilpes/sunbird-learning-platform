package org.ekstep.devcon.pdftoecml.model.ECML;

import java.util.ArrayList;

public class Manifest {
    ArrayList<Media> media = new ArrayList<Media>();

    public void setMedia(ArrayList<Media> media) {
        this.media = media;
    }

    public void addMedia(Media media) {
        this.media.add(media);
    }
    // Getter Methods
    // Setter Methods
}
