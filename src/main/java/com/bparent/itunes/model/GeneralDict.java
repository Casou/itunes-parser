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
public class GeneralDict extends Dict implements XmlExportable {

    @ItunesProperty("Major Version")
    private XmlInteger majorVersion;

    @ItunesProperty("Minor Version")
    private XmlInteger minorVersion;

    @ItunesProperty("Application Version")
    private XmlString applicationVersion;

    @ItunesProperty("Date")
    private XmlDate date;

    @ItunesProperty("Features")
    private XmlInteger features;

    @ItunesProperty("Show Content Ratings")
    private XmlBoolean showContentRatings;

    @ItunesProperty("Library Persistent ID")
    private XmlString persistentId;

    @ItunesProperty("Tracks")
    @ItunesList("dict")
    private List<Track> tracks;

    @ItunesProperty("Playlists")
    @ItunesList("array")
    private List<Playlist> playlists;

    @ItunesProperty("Music Folder")
    private XmlString musicFolder;

    public GeneralDict(Node node) {
        super(node);
        this.tracks = new ArrayList<>();
    }

    public void parse() {
        NodeList dictChildren = this.node.getChildNodes();
        String currentKey = null;
        for (int j = 0; j < dictChildren.getLength(); j++) {
            Node child = dictChildren.item(j);
            if (Node.ELEMENT_NODE == child.getNodeType()) {
                String nodeName = child.getNodeName();
                if (ELEMENT_KEY.equals(nodeName)) {
                    currentKey = getKey(child);
                } else {
                    if (currentKey == null) {
                        continue;
                    }

                    if (ELEMENT_DICT.equals(nodeName)) {
                        if (currentKey.equals(ELEMENT_KEY_TRACKS)) {
                            this.tracks = this.extractTracks(child);
                        }
                    } else if (ELEMENT_ARRAY.equals(nodeName)) {
                        if (currentKey.equals(ELEMENT_KEY_PLAYLISTS)) {
                            this.playlists = this.extractPlaylists(child);
                        }
                    } else {
                        Field field = this.getFieldFromItunes(currentKey);
                        XmlType childValue = this.getChildValue(nodeName, child);
                        if (field == null) {
                            // System.err.println(String.format("Field %s not found for general dict object", currentKey));
                            this.extraProperties.put(currentKey, childValue);
                            continue;
                        }

                        try {
                            this.setField(field, childValue);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private List<Playlist> extractPlaylists(Node arrayNode) {
        NodeList childNodes = arrayNode.getChildNodes();
        List<Playlist> playlists = new ArrayList<>(childNodes.getLength());

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (!ELEMENT_DICT.equals(item.getNodeName())) {
                continue;
            }
            Playlist playlist = new Playlist(item);
            playlist.parse();
            playlists.add(playlist);
        }
        return playlists;
    }

    private String getKey(Node child) {
        if (child.getChildNodes().getLength() == 0) {
            return null;
        }
        return child.getChildNodes().item(0).getNodeValue();
    }

    private List<Track> extractTracks(Node dictTracks) {
        NodeList childNodes = dictTracks.getChildNodes();
        List<Track> tracks = new ArrayList<>(childNodes.getLength());

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (!ELEMENT_DICT.equals(childNode.getNodeName())) {
                continue;
            }

            Track track = new Track(childNode);
            track.parse();
            tracks.add(track);
        }
        return tracks;
    }

    @Override
    public String toXml() {
        String allProperties = propertiesToXml("\t");

        return String.format(
                "<dict>\n"
                    + "%s\n"
                + "</dict>",
                allProperties
        );
    }

}
