import com.bparent.itunes.model.GeneralDict;
import com.bparent.itunes.model.ITunesLibrary;
import com.bparent.itunes.model.Track;
import com.bparent.itunes.parser.ItunesParser;
import com.bparent.itunes.utils.ITunesUtils;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ItunesParserTest {

    @Test
    void load_should_return_library_with_version() throws IOException, SAXException, ParserConfigurationException {
        // Given
        ItunesParser itunesParser = new ItunesParser();

        // When
        File f = new File("src/test/resources/xml/itunes_library_test.xml");
        ITunesLibrary library = itunesParser.load(f.getAbsolutePath());

        // Then
        assertNotNull(library.getPList());
        assertEquals("1.0", library.getPList().getVersion().getValue());
    }

    @Test
    void load_should_return_library_with_general_dict_with_attributes() throws IOException, SAXException, ParserConfigurationException {
        // Given
        ItunesParser itunesParser = new ItunesParser();

        // When
        File f = new File("src/test/resources/xml/itunes_library_test.xml");
        ITunesLibrary library = itunesParser.load(f.getAbsolutePath());

        // Then
        assertNotNull(library.getPList());
        assertNotNull(library.getPList().getDict());

        GeneralDict generalDict = library.getPList().getDict();
        assertEquals(BigInteger.valueOf(1), generalDict.getMajorVersion().getValue());
        assertEquals(BigInteger.valueOf(1), generalDict.getMinorVersion().getValue());
        assertEquals(LocalDateTime.of(2013, 9, 21, 9, 44, 21), generalDict.getDate().getValue());
        assertEquals("11.0.2", generalDict.getApplicationVersion().getValue());
        assertTrue(generalDict.getShowContentRatings().getValue());
        assertEquals("file://localhost/C:/Documents%20and%20Settings/Basile/Mes%20documents/Ma%20musique/iTunes/iTunes%20Media/", generalDict.getMusicFolder().getValue());
        assertEquals("049ADDA3FB4FA110", generalDict.getPersistentId().getValue());
    }

    @Test
    void load_should_return_library_with_general_dict_with_no_extra_field() throws IOException, SAXException, ParserConfigurationException {
        // Given
        ItunesParser itunesParser = new ItunesParser();

        // When
        File f = new File("src/test/resources/xml/itunes_library_test.xml");
        ITunesLibrary library = itunesParser.load(f.getAbsolutePath());

        // Then
        assertNotNull(library.getPList());
        assertNotNull(library.getPList().getDict());

        GeneralDict generalDict = library.getPList().getDict();
        assertEquals(0, generalDict.getExtraProperties().size());
    }

    @Test
    void load_should_return_library_with_list_of_tracks() throws IOException, SAXException, ParserConfigurationException {
        // Given
        ItunesParser itunesParser = new ItunesParser();

        // When
        File f = new File("src/test/resources/xml/itunes_library_test.xml");
        ITunesLibrary library = itunesParser.load(f.getAbsolutePath());

        // Then
        assertNotNull(library.getPList());
        assertNotNull(library.getPList().getDict());

        GeneralDict generalDict = library.getPList().getDict();
        assertEquals(4, generalDict.getTracks().size());
    }

    @Test
    void load_should_return_library_with_parsed_tracks() throws IOException, SAXException, ParserConfigurationException {
        // Given
        ItunesParser itunesParser = new ItunesParser();

        // When
        File f = new File("src/test/resources/xml/itunes_library_test.xml");
        ITunesLibrary library = itunesParser.load(f.getAbsolutePath());

        // Then
        assertNotNull(library.getPList());
        assertNotNull(library.getPList().getDict());

        GeneralDict generalDict = library.getPList().getDict();
        Track track = generalDict.getTracks().get(0);

        assertEquals(BigInteger.valueOf(1), track.getItunesId().getValue());
        assertEquals("Shiny Stockings", track.getName().getValue());
        assertEquals("Ella Fitzgerald & Someone Else", track.getArtist().getValue());
        assertEquals(BigInteger.valueOf(131552), track.getTotalTime().getValue());
        assertEquals(BigInteger.valueOf(55), track.getStartTime().getValue());
        assertEquals(BigInteger.valueOf(131000), track.getStopTime().getValue());
        assertEquals(BigInteger.valueOf(120), track.getBpm().getValue());
        assertEquals(BigInteger.valueOf(80), track.getRating().getValue());
        assertEquals(BigInteger.valueOf(1990), track.getYear().getValue());
        assertEquals("30 - Some comment", track.getComments().getValue());
        assertEquals("file://localhost/D:/musiques/itunes/iTunes%20Media/Music/void_music_1.mp3", track.getLocation().getValue());
        assertEquals(LocalDateTime.of(2012, 12, 24, 13, 54, 56), track.getDateModified().getValue());
    }

    @Test
    void load_should_return_library_with_tracks_with_no_extra_fields() throws IOException, SAXException, ParserConfigurationException {
        // Given
        ItunesParser itunesParser = new ItunesParser();

        // When
        File f = new File("src/test/resources/xml/itunes_library_test.xml");
        ITunesLibrary library = itunesParser.load(f.getAbsolutePath());

        // Then
        assertNotNull(library.getPList());
        assertNotNull(library.getPList().getDict());

        GeneralDict generalDict = library.getPList().getDict();

        generalDict.getTracks().forEach(track ->
                assertEquals(0, track.getExtraProperties().size(),
                        String.format("Erreur sur la track %d : %s", track.getItunesId().getValue(), track.getExtraProperties().toString())));
    }

    @Test
    void load_should_return_library_with_playlists() throws IOException, SAXException, ParserConfigurationException {
        // Given
        ItunesParser itunesParser = new ItunesParser();

        // When
        File f = new File("src/test/resources/xml/itunes_library_test.xml");
        ITunesLibrary library = itunesParser.load(f.getAbsolutePath());

        // Then
        assertNotNull(library.getPList());
        assertNotNull(library.getPList().getDict());

        GeneralDict generalDict = library.getPList().getDict();
        assertEquals(2, generalDict.getPlaylists().size());

        assertFalse(generalDict.getPlaylists().get(0).getMaster().getValue());
        assertEquals("Test Folder", generalDict.getPlaylists().get(0).getName().getValue());
        assertEquals(BigInteger.valueOf(14525), generalDict.getPlaylists().get(0).getPlaylistId().getValue());
        assertEquals("42A45B8FD23FA86A", generalDict.getPlaylists().get(0).getPlaylistPersistentId().getValue());
        assertNull(generalDict.getPlaylists().get(0).getParentPersistentId());
        assertTrue(generalDict.getPlaylists().get(0).getAllItems().getValue());
        assertFalse(generalDict.getPlaylists().get(0).getVisible().getValue());

        assertTrue(generalDict.getPlaylists().get(1).getMaster().getValue());
        assertEquals("Test Playlist", generalDict.getPlaylists().get(1).getName().getValue());
        assertEquals(BigInteger.valueOf(12345), generalDict.getPlaylists().get(1).getPlaylistId().getValue());
        assertEquals("3E6916F95560A2DD", generalDict.getPlaylists().get(1).getPlaylistPersistentId().getValue());
        assertEquals("42A45B8FD23FA86A", generalDict.getPlaylists().get(1).getParentPersistentId().getValue());
        assertTrue(generalDict.getPlaylists().get(1).getAllItems().getValue());
        assertTrue(generalDict.getPlaylists().get(1).getVisible().getValue());
    }

    @Test
    void load_should_return_library_with_playlists_with_no_extra_fields() throws IOException, SAXException, ParserConfigurationException {
        // Given
        ItunesParser itunesParser = new ItunesParser();

        // When
        File f = new File("src/test/resources/xml/itunes_library_test.xml");
        ITunesLibrary library = itunesParser.load(f.getAbsolutePath());

        // Then
        assertNotNull(library.getPList());
        assertNotNull(library.getPList().getDict());

        GeneralDict generalDict = library.getPList().getDict();

        generalDict.getPlaylists().forEach(playlist ->
                assertEquals(0, playlist.getExtraProperties().size(),
                        String.format("Erreur sur la playlist %d : %s", playlist.getPlaylistId().getValue(), playlist.getExtraProperties().toString())));
    }

    @Test
    void load_should_return_library_with_playlists_with_items() throws IOException, SAXException, ParserConfigurationException {
        // Given
        ItunesParser itunesParser = new ItunesParser();

        // When
        File f = new File("src/test/resources/xml/itunes_library_test.xml");
        ITunesLibrary library = itunesParser.load(f.getAbsolutePath());

        // Then
        assertNotNull(library.getPList());
        assertNotNull(library.getPList().getDict());

        GeneralDict generalDict = library.getPList().getDict();
        assertEquals(2, generalDict.getPlaylists().size());

        assertEquals(2, generalDict.getPlaylists().get(0).getItems().size());
        assertEquals(BigInteger.valueOf(1), generalDict.getPlaylists().get(0).getItems().get(0).getTrackId().getValue());
        assertEquals(BigInteger.valueOf(2), generalDict.getPlaylists().get(0).getItems().get(1).getTrackId().getValue());

        assertEquals(3, generalDict.getPlaylists().get(1).getItems().size());
        assertEquals(BigInteger.valueOf(1), generalDict.getPlaylists().get(1).getItems().get(0).getTrackId().getValue());
        assertEquals(BigInteger.valueOf(3), generalDict.getPlaylists().get(1).getItems().get(1).getTrackId().getValue());
        assertEquals(BigInteger.valueOf(4), generalDict.getPlaylists().get(1).getItems().get(2).getTrackId().getValue());
    }

    @Test
    void load_should_return_library_with_playlists_with_items_with_no_extra_fields() throws IOException, SAXException, ParserConfigurationException {
        // Given
        ItunesParser itunesParser = new ItunesParser();

        // When
        File f = new File("src/test/resources/xml/itunes_library_test.xml");
        ITunesLibrary library = itunesParser.load(f.getAbsolutePath());

        // Then
        assertNotNull(library.getPList());
        assertNotNull(library.getPList().getDict());

        GeneralDict generalDict = library.getPList().getDict();

        generalDict.getPlaylists().forEach(playlist ->
                Optional.ofNullable(playlist.getItems()).ifPresent(items -> items.forEach(item ->
                        assertEquals(0, item.getExtraProperties().size(),
                                String.format("Erreur sur la playlist %d et l'item %d : %s",
                                        playlist.getPlaylistId().getValue(),
                                        item.getTrackId().getValue(),
                                        item.getExtraProperties().toString())))));
    }

    @Test
    void test() throws IOException, SAXException, ParserConfigurationException {
        ItunesParser itunesParser = new ItunesParser();
        File f = new File("src/test/resources/xml/itunes_library_test.xml");
        ITunesLibrary currentLibrary = itunesParser.load(f.getAbsolutePath());

        String itunesFolderPath = "/mnt/44D38A27637CE7D3/musiques/itunes";
        List<Track> missingFiles = ITunesUtils.getMissingFiles(currentLibrary, itunesFolderPath);
        System.out.println(missingFiles + " missing file(s)");

        Map<Track, List<File>> trackListMap = ITunesUtils.suggestMissingFilesReplacement(missingFiles, itunesFolderPath);

        trackListMap.forEach((track, files) ->
                System.out.println(
                        String.format("%s => %s : %d replacement(s)\n", track.getName().getValue(), track.getDecodedLocation(), files.size())
                        + files.stream().map(file -> "\t" + file.getAbsolutePath()).collect(Collectors.joining("\n"))
                        + "\n"));
    }

//    @Test
//    void test() throws IOException, SAXException, ParserConfigurationException {
//        // Given
//        ItunesParser itunesParser = new ItunesParser();
//
//        // When
//        File f = new File("src/test/resources/xml/itunes_library_test.xml");
//        ITunesLibrary currentLibrary = itunesParser.load(f.getAbsolutePath());
//
//        f = new File("src/test/resources/xml/itunes_library_test.xml");
//        ITunesLibrary realLibrary = itunesParser.load(f.getAbsolutePath());
//
//        // Then
//        List<Track> diffTracks = realLibrary.getPList().getDict().getTracks().stream()
//                .filter(track -> currentLibrary.getPList().getDict().getTracks().stream()
//                        .noneMatch(t -> t.getName().equals(track.getName())
//                                && (t.getArtist() != null && t.getArtist().equals(track.getArtist()))
//                                && (t.getBpm() != null && t.getBpm().equals(track.getBpm()))
//                                && (t.getTotalTime() != null && t.getTotalTime().equals(track.getTotalTime()))
//                        ))
//                .collect(Collectors.toList());
//
////        GeneralDict generalDict = currentLibrary.getPList().getDict();
////        assertEquals(1704, generalDict.getTracks().size());
////        System.out.println(currentIds.size() + " current songs; " + realIds.size() + " real songs");
//        System.err.println(diffTracks.size() + " differences");
////        System.out.println(currentIds);
////        System.out.println(realIds);
////        System.err.println(diffTracksId);
//        diffTracks.stream().map(Track::getLocation).forEach(System.out::println);
//
//        assertEquals(1701, realLibrary.getPList().getDict().getTracks().size());
//        assertEquals(1503, currentLibrary.getPList().getDict().getTracks().size());
//        assertEquals(1701 - 1504, diffTracks.size());
//    }

}