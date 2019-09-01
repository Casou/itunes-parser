package com.bparent.itunes.utils;

import com.bparent.itunes.model.GeneralDict;
import com.bparent.itunes.model.ITunesLibrary;
import com.bparent.itunes.model.PList;
import com.bparent.itunes.model.Track;
import com.bparent.itunes.type.XmlInteger;
import com.bparent.itunes.type.XmlString;
import org.junit.jupiter.api.Test;

import javax.imageio.metadata.IIOMetadataNode;
import java.io.File;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ITunesUtilsTest {

    @Test
    void getMissingFiles_should_return_2_missing_tracks() {
        // Given
        ITunesLibrary itunesLibrary = new ITunesLibrary();
        PList pList = new PList(new IIOMetadataNode());
        GeneralDict generalDict = new GeneralDict(null);
        generalDict.setTracks(Arrays.asList(
                buildTrack(1000, "Void music 1", "file://localhost/D:/musiques/itunes/iTunes%20Media/Music/void_music_1.mp3"),
                buildTrack(2000, "Void music 2", "file://localhost/D:/musiques/itunes/iTunes%20Media/Music/void_music_2.mp3"),
                buildTrack(3000, "Missing music 1", "file://localhost/D:/musiques/itunes/iTunes%20Media/Music/missing_music_1.mp3"),
                buildTrack(4000, "Missing music 2", "file://localhost/D:/musiques/itunes/iTunes%20Media/Music/missing_music_1.mp3")
        ));
        pList.setDict(generalDict);
        itunesLibrary.setPList(pList);

        String itunesFolderPath = new File("src/test/resources").getAbsolutePath();

        // When
        List<Track> missingFiles = ITunesUtils.getMissingFiles(itunesLibrary, itunesFolderPath);

        // Then
        assertEquals(2, missingFiles.size());
        assertEquals(BigInteger.valueOf(3000), missingFiles.get(0).getItunesId().getValue());
        assertEquals(BigInteger.valueOf(4000), missingFiles.get(1).getItunesId().getValue());
    }

    private Track buildTrack(Integer itunesId, String name, String location) {
        Track track = new Track(null);
        track.setItunesId(new XmlInteger(itunesId + ""));
        track.setName(new XmlString(name));
        track.setLocation(new XmlString(location));
        return track;
    }

    @Test
    void suggestMissingFilesReplacement_should_return_replacement_suggestion_only_if_audio_file_found_with_same_name() {
        // Given
        List<Track> missingTracks = Arrays.asList(
                buildTrack(1000, "Void music 1", "folder/to/void_music_1.mp3"),
                buildTrack(2000, "iTunes XML file", "folder/to/itunes_library_test.xml"),
                buildTrack(3000, "Unknown file", "folder/to/unknown_file.mp3")
        );

        // When
        Map<Track, List<File>> fileReplacements = ITunesUtils.suggestMissingFilesReplacement(missingTracks, "src/test/resources");

        // Then
        assertEquals(3, fileReplacements.size());

        List<Map.Entry<Track, List<File>>> allEntries = fileReplacements.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().getItunesId().getValue()))
                .collect(Collectors.toList());

        Map.Entry<Track, List<File>> voidMusicEntry = allEntries.get(0);
        assertEquals(BigInteger.valueOf(1000), voidMusicEntry.getKey().getItunesId().getValue());
        assertEquals(1, voidMusicEntry.getValue().size());
        assertTrue(voidMusicEntry.getValue().get(0).getAbsolutePath().endsWith("src/test/resources/iTunes Media/Music/void_music_1.mp3"));

        Map.Entry<Track, List<File>> iTunesLibraryEntry = allEntries.get(1);
        assertEquals(BigInteger.valueOf(2000), iTunesLibraryEntry.getKey().getItunesId().getValue());
        assertEquals(0, iTunesLibraryEntry.getValue().size());

        Map.Entry<Track, List<File>> unknownFileEntry = allEntries.get(2);
        assertEquals(BigInteger.valueOf(3000), unknownFileEntry.getKey().getItunesId().getValue());
        assertEquals(0, unknownFileEntry.getValue().size());
    }

}