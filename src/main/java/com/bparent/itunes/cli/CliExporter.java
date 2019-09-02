package com.bparent.itunes.cli;

import com.bparent.itunes.model.ITunesLibrary;
import com.bparent.itunes.utils.ConsoleColors;
import com.bparent.itunes.utils.FileUtils;

import java.io.File;
import java.io.IOException;

public class CliExporter {

    private final ITunesLibrary library;
    private final File xmlFile;

    public CliExporter(ITunesLibrary library, File xmlFile) {
        this.library = library;
        this.xmlFile = xmlFile;
    }

    public void exportLibrary() {
        System.out.println("Exporting in XML...");
        String xmlContent = this.library.toXml("");
        try {
            String xmlNewFile = computeNexFileName();
            FileUtils.writeInFile(xmlNewFile, xmlContent);
            this.library.setUnsavedModifications(false);
            System.out.println(ConsoleColors.GREEN_BRIGHT + "Library exported in XML in file : " + xmlNewFile + ConsoleColors.RESET);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String computeNexFileName() {
        String originalFileName = this.xmlFile.getName().substring(0, this.xmlFile.getName().lastIndexOf("."));
        int compteur = 1;
        String parentPath = this.xmlFile.getParentFile().getPath() + "/";
        File f = new File(parentPath + originalFileName + " " + compteur + ".xml");
        while (f.exists()) {
            compteur++;
            f = new File(parentPath + originalFileName + " " + compteur + ".xml");
        }

        return parentPath + originalFileName + " " + compteur + ".xml";
    }
}
