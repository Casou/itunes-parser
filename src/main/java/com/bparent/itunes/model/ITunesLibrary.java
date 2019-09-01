package com.bparent.itunes.model;

import com.bparent.itunes.exporter.XmlExportable;
import lombok.Data;

@Data
public class ITunesLibrary implements XmlExportable {

    private PList pList;

    @Override
    public String toXml(String paddingLeft) {
        return String.format("%s<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "%s<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n",
                paddingLeft,
                paddingLeft
        )
                + pList.toXml(paddingLeft);
    }
}
