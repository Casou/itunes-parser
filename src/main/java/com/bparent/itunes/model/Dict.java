package com.bparent.itunes.model;

import com.bparent.itunes.exporter.XmlExportable;
import org.w3c.dom.Node;

public abstract class Dict extends ITunesNode implements XmlExportable {

    public Dict(Node node) {
        super(node);
    }

    @Override
    public String toXml(String paddingLeft) {
        String allProperties = propertiesToXml(paddingLeft + "\t");

        return String.format(
                "%s<dict>\n"
                        + "%s\n"
                        + "%s</dict>",
                paddingLeft,
                allProperties,
                paddingLeft
        );
    }

}
