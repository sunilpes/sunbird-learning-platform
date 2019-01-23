package org.ekstep.common.util;

import java.io.File;

public class Test {

    public static void main(String[] args) {
        String url = "https://dev.ekstep.in/assets/public/content/do_1126830426936360961221/artifact/solar-system_1548222985527.png";
        File file = HttpDownloadUtility.downloadFile(url, "/data/tempFile");
        System.out.println("Absolute path : "+file.getAbsolutePath());
    }
}