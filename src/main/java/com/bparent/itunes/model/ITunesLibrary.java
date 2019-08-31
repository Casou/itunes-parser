package com.bparent.itunes.model;

import com.bparent.itunes.exporter.XmlExportable;
import lombok.Data;

@Data
public class ITunesLibrary implements XmlExportable {

    private PList pList;

    @Override
    public String toXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n"
                + pList.toXml();
    }
}
