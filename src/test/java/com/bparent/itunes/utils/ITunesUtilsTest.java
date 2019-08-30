package com.bparent.itunes.utils;

import com.bparent.itunes.model.GeneralDict;
import com.bparent.itunes.model.ITunesLibrary;
import com.bparent.itunes.model.PList;
import com.bparent.itunes.model.Track;
import org.junit.jupiter.api.Test;

import javax.imageio.metadata.IIOMetadataNode;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ITunesUtilsTest {

    @Test
    void getMissingFiles_should_return_2_missing_tracks() throws UnsupportedEncodingException {
        // Given
        ITunesLibrary itunesLibrary = new ITunesLibrary();
        PList pList = new PList(new IIOMetadataNode());
        GeneralDict generalDict = new GeneralDict(null);
        generalDict.setTracks(Arrays.asList(
                buildTrack(1000, null, "file://localhost/D:/musiques/itunes/iTunes%20Media/Music/void_music_1.mp3"),
                buildTrack(2000, null, "file://localhost/D:/musiques/itunes/iTunes%20Media/Music/void_music_2.mp3"),
                buildTrack(3000, null, "file://localhost/D:/musiques/itunes/iTunes%20Media/Music/missing_music_1.mp3"),
                buildTrack(4000, null, "file://localhost/D:/musiques/itunes/iTunes%20Media/Music/missing_music_1.mp3")
        ));
        pList.setDict(generalDict);
        itunesLibrary.setPList(pList);

        String itunesFolderPath = new File("src/test/resources").getAbsolutePath();

        // When
        List<Track> missingFiles = ITunesUtils.getMissingFiles(itunesLibrary, itunesFolderPath);

        // Then
        assertEquals(2, missingFiles.size());
        assertEquals(Integer.valueOf(3000), missingFiles.get(0).getItunesId());
        assertEquals(Integer.valueOf(4000), missingFiles.get(1).getItunesId());
    }

    private Track buildTrack(Integer itunesId, String name, String location) {
        Track track = new Track(null);
        track.setItunesId(itunesId);
        track.setName(name);
        track.setLocation(location);
        return track;
    }

    @Test
    void suggestMissingFilesReplacement_should_return_files_with_same_name_as_replacement_suggestion() {
        // Given
        List<Track> missingTracks = Arrays.asList(
                buildTrack(1000, "void_music_1.mp3", null),
                buildTrack(2000, "itunes_library_test.xml", null),
                buildTrack(3000, "unknown_file", null)
        );

        // When
        Map<Track, List<File>> fileReplacements = ITunesUtils.suggestMissingFilesReplacement(missingTracks, "src/test/resources");

        // Then
        assertEquals(3, fileReplacements.size());

        List<Map.Entry<Track, List<File>>> allEntries = fileReplacements.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().getItunesId()))
                .collect(Collectors.toList());

        assertEquals(Integer.valueOf(1000), allEntries.get(0).getKey().getItunesId());
        assertEquals(1,  allEntries.get(0).getValue().size());
        assertTrue(allEntries.get(0).getValue().get(0).getAbsolutePath().endsWith("src/test/resources/iTunes Media/Music/void_music_1.mp3"));

        assertEquals(Integer.valueOf(2000), allEntries.get(1).getKey().getItunesId());
        assertEquals(1,  allEntries.get(1).getValue().size());
        assertTrue(allEntries.get(1).getValue().get(0).getAbsolutePath().endsWith("src/test/resources/xml/itunes_library_test.xml"));

        assertEquals(Integer.valueOf(3000), allEntries.get(2).getKey().getItunesId());
        assertEquals(0,  allEntries.get(2).getValue().size());
    }

}