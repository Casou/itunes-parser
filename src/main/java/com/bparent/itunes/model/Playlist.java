package com.bparent.itunes.model;

import com.bparent.itunes.annotations.ItunesProperty;
import lombok.Data;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Data
public class Playlist extends ITunesNode {

    @ItunesProperty("Master")
    private boolean master;

    @ItunesProperty("Playlist ID")
    private Integer playlistId;

    @ItunesProperty("Playlist Persistent ID")
    private String playlistPersistentId;

    @ItunesProperty("Parent Persistent ID")
    private String parentPersistentId;

    @ItunesProperty("Distinguished Kind")
    private Integer distinguishedKind;

    @ItunesProperty("All Items")
    private Boolean allItems;

    @ItunesProperty("Visible")
    private Boolean visible;

    @ItunesProperty("Movies")
    private Boolean movies;

    @ItunesProperty("Music")
    private Boolean music;

    @ItunesProperty("TV Shows")
    private Boolean tvShows;

    @ItunesProperty("Podcasts")
    private Boolean podcasts;

    @ItunesProperty("Audiobooks")
    private Boolean audiobooks;

    @ItunesProperty("Folder")
    private Boolean folder;

    @ItunesProperty("Name")
    private String name;

    @ItunesProperty("Smart Info")
    private String smartInfo;

    @ItunesProperty("Smart Criteria")
    private String smartCriteria;

    private List<PlaylistItem> items;

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
            Object childValue = this.getChildValue(nodeValue.getNodeName(), nodeValue);

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
