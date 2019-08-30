package com.bparent.itunes.parser;

import com.bparent.itunes.model.ITunesLibrary;
import com.bparent.itunes.model.PList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class ItunesParser {

    private static final XmlDocumentParser xmlDocumentParser = new XmlDocumentParser();

    public ITunesLibrary load(String filePath) throws ParserConfigurationException, IOException, SAXException {
        ITunesLibrary iTunesLibrary = new ITunesLibrary();

        Document document = xmlDocumentParser.getDocument(filePath);
        NodeList childNodes = document.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node plist = childNodes.item(i);

            if (plist.getChildNodes().getLength() == 0) {
                continue;
            }

            PList pList = new PList(plist);
            pList.parse();

            iTunesLibrary.setPList(pList);
            return iTunesLibrary;
        }

        return iTunesLibrary;
    }

}
