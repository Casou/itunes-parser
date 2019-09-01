package com.bparent.itunes.exporter;

import com.bparent.itunes.model.ITunesLibrary;
import com.bparent.itunes.parser.ItunesParser;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XmlExportableTest {

    @Test
    void toXml_should_produce_xml_file_identical_from_itunes() throws IOException, SAXException, ParserConfigurationException {
        ItunesParser parser = new ItunesParser();
        String xmlFilePath = "src/test/resources/xml/itunes_library_real.xml";
        String originalFileContent = String.join("\n", Files.readAllLines(Paths.get(xmlFilePath), Charset.defaultCharset()));

        ITunesLibrary iTunesLibrary = parser.load(xmlFilePath);
        String generatedXml = iTunesLibrary.toXml("");

        assertEquals(originalFileContent, generatedXml);
    }

}