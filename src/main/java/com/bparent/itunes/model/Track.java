package com.bparent.itunes.model;

import com.bparent.itunes.annotations.ItunesProperty;
import com.bparent.itunes.exporter.XmlExportable;
import com.bparent.itunes.type.*;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;

@Getter
@Setter
public class Track extends ITunesNode implements XmlExportable {

    @ItunesProperty("Track ID")
    private XmlInteger itunesId;

    @ItunesProperty("Size")
    private XmlInteger size;

    @ItunesProperty("Total Time")
    private XmlInteger totalTime;

    @ItunesProperty("Start Time")
    private XmlInteger startTime;

    @ItunesProperty("Stop Time")
    private XmlInteger stopTime;

    @ItunesProperty("Disc Number")
    private XmlInteger discNumber;

    @ItunesProperty("Disc Count")
    private XmlInteger discCount;

    @ItunesProperty("Track Number")
    private XmlInteger trackNumber;

    @ItunesProperty("Track Count")
    private XmlInteger trackCount;

    @ItunesProperty("Year")
    private XmlInteger year;

    @ItunesProperty("BPM")
    private XmlInteger bpm;

    @ItunesProperty("Date Modified")
    private XmlDate dateModified;

    @ItunesProperty("Date Added")
    private XmlDate dateAdded;

    @ItunesProperty("Bit Rate")
    private XmlInteger bitRate;

    @ItunesProperty("Sample Rate")
    private XmlInteger sampleRate;

    @ItunesProperty("Part Of Gapless Album")
    private XmlBoolean partOfGaplessAlbum;

    @ItunesProperty("Volume Adjustment")
    private XmlInteger volumeAdjustment;

    @ItunesProperty("Play Count")
    private XmlInteger playCount;

    @ItunesProperty("Play Date")
    private XmlInteger playDate;

    @ItunesProperty("Play Date UTC")
    private XmlDate playDateUTC;

    @ItunesProperty("Skip Count")
    private XmlInteger skipCount;

    @ItunesProperty("Skip Date")
    private XmlDate skipDate;

    @ItunesProperty("Release Date")
    private XmlDate releaseDate;

    @ItunesProperty("Rating")
    private XmlInteger rating;

    @ItunesProperty("Rating Computed")
    private XmlBoolean ratingComputed;

    @ItunesProperty("Album Rating")
    private XmlInteger albumRating;

    @ItunesProperty("Album Rating Computed")
    private XmlBoolean albumRatingComputed;

    @ItunesProperty("Compilation")
    private XmlBoolean compilation;

    @ItunesProperty("Artwork Count")
    private XmlInteger artworkCount;

    @ItunesProperty("Persistent ID")
    private XmlString persistentId;

    @ItunesProperty("Disabled")
    private XmlBoolean disabled;

    @ItunesProperty("Track Type")
    private XmlString trackType;

    @ItunesProperty("Purchased")
    private XmlBoolean purchased;

    @ItunesProperty("File Folder Count")
    private XmlInteger fileFolderCount;

    @ItunesProperty("Library Folder Count")
    private XmlInteger libraryFolderCount;

    @ItunesProperty("Name")
    private XmlString name;

    @ItunesProperty("Artist")
    private XmlString artist;

    @ItunesProperty("Album Artist")
    private XmlString albumArtist;

    @ItunesProperty("Composer")
    private XmlString composer;

    @ItunesProperty("Album")
    private XmlString album;

    @ItunesProperty("Grouping")
    private XmlString grouping;

    @ItunesProperty("Genre")
    private XmlString genre;

    @ItunesProperty("Kind")
    private XmlString kind;

    @ItunesProperty("Comments")
    private XmlString comments;

    @ItunesProperty("Sort Name")
    private XmlString sortName;

    @ItunesProperty("Sort Album")
    private XmlString sortAlbum;

    @ItunesProperty("Sort Artist")
    private XmlString sortArtist;

    @ItunesProperty("Sort Album Artist")
    private XmlString sortAlbumArtist;

    @ItunesProperty("Sort Composer")
    private XmlString sortComposer;

    @ItunesProperty("Work")
    private XmlString work;

    @ItunesProperty("Location")
    private XmlString location;

    public Track(Node node) {
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

    public String getDecodedLocation() {
        if (this.location == null) {
            return null;
        }
        try {
            return URLDecoder.decode(this.location.getValue(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toXml(String paddingLeft) {
        String allProperties = propertiesToXml(paddingLeft + "\t");

        return String.format(
                "%s<key>%d</key>\n"
                        + "%s<dict>\n"
                        + "%s\n"
                        + "%s</dict>",
                paddingLeft,
                this.itunesId.getValue(),
                paddingLeft,
                allProperties,
                paddingLeft
        );
    }

}
