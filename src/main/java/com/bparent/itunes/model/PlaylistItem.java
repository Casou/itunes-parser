package com.bparent.itunes.model;

import com.bparent.itunes.annotations.ItunesProperty;
import com.bparent.itunes.exporter.XmlExportable;
import com.bparent.itunes.type.XmlInteger;
import com.bparent.itunes.type.XmlType;
import lombok.Data;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Field;

@Data
public class PlaylistItem extends ITunesNode implements XmlExportable {

    @ItunesProperty("Track ID")
    private XmlInteger trackId;

    public PlaylistItem(Node node) {
        super(node);
    }

    public void parse() {
        NodeList dictChildren = this.node.getChildNodes();
        for (int j = 0; j < dictChildren.getLength(); j++) {
            Node nodeKey = dictChildren.item(j);
            if (Node.ELEMENT_NODE != nodeKey.getNodeType()) {
                continue;
            }
            j++;
            Node nodeValue = dictChildren.item(j);

            String key = nodeKey.getChildNodes().item(0).getNodeValue();
            Field field = this.getFieldFromItunes(key);
            XmlType childValue = this.getChildValue(nodeValue.getNodeName(), nodeValue);

            if (field == null) {
                this.extraProperties.put(key, childValue);
                continue;
            }
            try {
                this.setField(field, childValue);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toXml() {
        String allProperties = propertiesToXml("\t\t\t\t\t");

        return String.format(
                "\t\t\t\t<dict>\n"
                        + "%s\n"
                        + "\t\t\t\t</dict>",
                allProperties);
    }
}
