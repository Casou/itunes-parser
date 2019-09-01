package com.bparent.itunes.model;

import com.bparent.itunes.annotations.ItunesList;
import com.bparent.itunes.annotations.ItunesProperty;
import com.bparent.itunes.exporter.XmlExportable;
import com.bparent.itunes.type.*;
import lombok.Data;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Data
public class Playlist extends Dict implements XmlExportable {

    @ItunesProperty("Master")
    private XmlBoolean master;

    @ItunesProperty("Playlist ID")
    private XmlInteger playlistId;

    @ItunesProperty("Parent Persistent ID")
    private XmlString parentPersistentId;

    @ItunesProperty("Playlist Persistent ID")
    private XmlString playlistPersistentId;

    @ItunesProperty("Distinguished Kind")
    private XmlInteger distinguishedKind;

    @ItunesProperty("Movies")
    private XmlBoolean movies;

    @ItunesProperty("Music")
    private XmlBoolean music;

    @ItunesProperty("TV Shows")
    private XmlBoolean tvShows;

    @ItunesProperty("Podcasts")
    private XmlBoolean podcasts;

    @ItunesProperty("Audiobooks")
    private XmlBoolean audiobooks;

    @ItunesProperty("Books")
    private XmlBoolean books;

    @ItunesProperty("iTunesU")
    private XmlBoolean itunesU;

    @ItunesProperty("All Items")
    private XmlBoolean allItems;

    @ItunesProperty("Visible")
    private XmlBoolean visible;

    @ItunesProperty("Folder")
    private XmlBoolean folder;

    @ItunesProperty("Name")
    private XmlString name;

    @ItunesProperty("Smart Info")
    private XmlData smartInfo;

    @ItunesProperty("Smart Criteria")
    private XmlData smartCriteria;

    @ItunesProperty("Playlist Items")
    @ItunesList("array")
    private List<PlaylistItem> items = new ArrayList<>();

    public Playlist(Node node) {
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
            while (Node.ELEMENT_NODE != nodeValue.getNodeType() && j < dictChildren.getLength()) {
                j++;
                nodeValue = dictChildren.item(j);
            }

            String key = nodeKey.getChildNodes().item(0).getNodeValue();
            if (key.equals("Playlist Items")) {
                this.items = parseItems(nodeValue);
                continue;
            }

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

    private List<PlaylistItem> parseItems(Node arrayNode) {
        NodeList childNodes = arrayNode.getChildNodes();
        List<PlaylistItem> items = new ArrayList<>(childNodes.getLength());
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!ELEMENT_DICT.equals(item.getNodeName())) {
                continue;
            }

            PlaylistItem playlistItem = new PlaylistItem(item);
            playlistItem.parse();
            items.add(playlistItem);
        }
        return items;
    }

}
