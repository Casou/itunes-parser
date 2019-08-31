package com.bparent.itunes.model;

import com.bparent.itunes.exporter.XmlExportable;
import lombok.Data;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Optional;

@Data
public class PList extends ITunesNode implements XmlExportable {

    private String version;
    private GeneralDict dict;

    public PList(Node node) {
        super(node);

        this.getDictNode().ifPresent(dictNode -> this.dict = new GeneralDict(dictNode));
        this.fillAttributes();
    }

    private Optional<Node> getDictNode() {
        NodeList plistChildren = this.node.getChildNodes();
        for (int j = 0; j < plistChildren.getLength(); j++) {
            Node child = plistChildren.item(j);
            if (Node.ELEMENT_NODE == child.getNodeType()) {
                return Optional.of(child);
            }
        }
        return Optional.empty();
    }

    public void parse() {
        if (this.dict == null) {
            return;
        }
        this.dict.parse();
    }

    @Override
    public String toXml() {
        return String.format("<plist version=\"%s\">\n" +
                "%s\n" +
                "</plist>", this.version, this.dict.toXml());
    }
}
