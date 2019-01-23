package org.ekstep.devcon.pdftoecml.services;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

@Service
public class PDF2Text {

    public String generateTxtFromPDF(String filename) throws IOException {
        File f = new File(filename);
        String parsedText;
        PDFParser parser = new PDFParser(new RandomAccessFile(f, "r"));
        parser.parse();

        COSDocument cosDoc = parser.getDocument();

        PDFTextStripper pdfStripper = new PDFTextStripper();
        PDDocument pdDoc = new PDDocument(cosDoc);
        pdfStripper.setParagraphStart("/t");
        pdfStripper.setSortByPosition(true);
        parsedText = pdfStripper.getText(pdDoc);

        if (cosDoc != null)
            cosDoc.close();
        if (pdDoc != null)
            pdDoc.close();
        return extractParagraphs(parsedText);
    }

    private String extractParagraphs(String parsedText) {
        String paragraphs[];
        paragraphs = parsedText.split("/t", 0);
        List<String> paragraphList = new java.util.ArrayList<>(Arrays.asList(paragraphs));
        paragraphList.removeIf(paragraph -> (paragraph.split(" ", 0).length <= 15));
        return paragraphList.stream().reduce((p1, p2) -> p1.concat("\n").concat(p2)).get();
    }

    public void printIt(String text, String path) throws IOException {
        PrintWriter pw = new PrintWriter(path);
        pw.print(text);
        pw.close();
    }
}
