package com.bparent.itunes.model;

import com.bparent.itunes.annotations.ItunesProperty;
import com.bparent.itunes.exporter.XmlExportable;
import lombok.Data;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.time.LocalDateTime;

@Data
public class Track extends ITunesNode implements XmlExportable {

    @ItunesProperty("Track ID")
    protected Integer itunesId;

    @ItunesProperty("Size")
    protected Integer size;

    @ItunesProperty("Total Time")
    protected Integer totalTime;

    @ItunesProperty("Start Time")
    protected Integer startTime;

    @ItunesProperty("Stop Time")
    protected Integer stopTime;

    @ItunesProperty("Disc Number")
    protected Integer discNumber;

    @ItunesProperty("Disc Count")
    protected Integer discCount;

    @ItunesProperty("Track Number")
    protected Integer trackNumber;

    @ItunesProperty("Track Count")
    protected Integer trackCount;

    @ItunesProperty("Year")
    protected Integer year;

    @ItunesProperty("BPM")
    protected Integer bpm;

    @ItunesProperty("Date Modified")
    protected LocalDateTime dateModified;

    @ItunesProperty("Date Added")
    protected LocalDateTime dateAdded;

    @ItunesProperty("Bit Rate")
    protected Integer bitRate;

    @ItunesProperty("Sample Rate")
    protected Integer sampleRate;

    @ItunesProperty("Part Of Gapless Album")
    protected Boolean partOfGaplessAlbum;

    @ItunesProperty("Volume Adjustment")
    protected Integer volumeAdjustment;

    @ItunesProperty("Play Count")
    protected Integer playCount;

    @ItunesProperty("Play Date")
    protected BigInteger playDate;

    @ItunesProperty("Play Date UTC")
    protected LocalDateTime playDateUTC;

    @ItunesProperty("Skip Count")
    protected Integer skipCount;

    @ItunesProperty("Skip Date")
    protected LocalDateTime skipDate;

    @ItunesProperty("Release Date")
    protected LocalDateTime releaseDate;

    @ItunesProperty("Rating")
    protected Integer rating;

    @ItunesProperty("Rating Computed")
    protected Boolean ratingComputed;

    @ItunesProperty("Album Rating")
    protected Integer albumRating;

    @ItunesProperty("Album Rating Computed")
    protected Boolean albumRatingComputed;

    @ItunesProperty("Compilation")
    protected Boolean compilation;

    @ItunesProperty("Artwork Count")
    protected Integer artworkCount;

    @ItunesProperty("Persistent ID")
    protected String persistentId;

    @ItunesProperty("Disabled")
    protected Boolean disabled;

    @ItunesProperty("Track Type")
    protected String trackType;

    @ItunesProperty("Purchased")
    protected Boolean purchased;

    @ItunesProperty("File Folder Count")
    protected Integer fileFolderCount;

    @ItunesProperty("Library Folder Count")
    protected Integer libraryFolderCount;

    @ItunesProperty("Name")
    protected String name;

    @ItunesProperty("Artist")
    protected String artist;

    @ItunesProperty("Album Artist")
    protected String albumArtist;

    @ItunesProperty("Composer")
    protected String composer;

    @ItunesProperty("Album")
    protected String album;

    @ItunesProperty("Grouping")
    protected String grouping;

    @ItunesProperty("Genre")
    protected String genre;

    @ItunesProperty("Kind")
    protected String kind;

    @ItunesProperty("Comments")
    protected String comments;

    @ItunesProperty("Sort Name")
    protected String sortName;

    @ItunesProperty("Sort Album")
    protected String sortAlbum;

    @ItunesProperty("Sort Artist")
    protected String sortArtist;

    @ItunesProperty("Sort Album Artist")
    protected String sortAlbumArtist;

    @ItunesProperty("Sort Composer")
    protected String sortComposer;

    @ItunesProperty("Work")
    protected String work;

    @ItunesProperty("Location")
    protected String location;

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

    public String getDecodedLocation() {
        if (this.location == null) {
            return null;
        }
        try {
            return URLDecoder.decode(this.location, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toXml() {
        String allProperties = propertiesToXml("\t\t\t");

        return String.format(
                "\t\t<key>%d</key>\n"
                        + "\t\t<dict>\n"
                        + "%s"
                        + "\t\t</dict>",
                this.itunesId,
                allProperties
        );
    }

}
