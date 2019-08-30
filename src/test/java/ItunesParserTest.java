import com.bparent.itunes.model.GeneralDict;
import com.bparent.itunes.model.ITunesLibrary;
import com.bparent.itunes.model.Track;
import com.bparent.itunes.parser.ItunesParser;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ItunesParserTest {

    @Test
    void load_should_return_library_with_version() throws IOException, SAXException, ParserConfigurationException {
        // Given
        ItunesParser itunesParser = new ItunesParser();

        // When
        File f = new File("src/test/resources/xml/itunes_library_real.xml");
        ITunesLibrary library = itunesParser.load(f.getAbsolutePath());

        // Then
        assertNotNull(library.getPList());
        assertEquals("1.0", library.getPList().getVersion());
    }

    @Test
    void load_should_return_library_with_general_dict_with_attributes() throws IOException, SAXException, ParserConfigurationException {
        // Given
        ItunesParser itunesParser = new ItunesParser();

        // When
        File f = new File("src/test/resources/xml/itunes_library_real.xml");
        ITunesLibrary library = itunesParser.load(f.getAbsolutePath());

        // Then
        assertNotNull(library.getPList());
        assertNotNull(library.getPList().getDict());

        GeneralDict generalDict = library.getPList().getDict();
        assertEquals(Integer.valueOf(1), generalDict.getMajorVersion());
        assertEquals(Integer.valueOf(1), generalDict.getMinorVersion());
        assertEquals(LocalDateTime.of(2013, 9, 21, 9, 44, 21), generalDict.getDate());
        assertEquals("11.0.2", generalDict.getApplicationVersion());
        assertTrue(generalDict.isShowContentRatings());
        assertEquals("file://localhost/C:/Documents%20and%20Settings/Basile/Mes%20documents/Ma%20musique/iTunes/iTunes%20Media/", generalDict.getMusicFolder());
        assertEquals("049ADDA3FB4FA110", generalDict.getPersistentId());
    }

    @Test
    void load_should_return_library_with_general_dict_with_no_extra_field() throws IOException, SAXException, ParserConfigurationException {
        // Given
        ItunesParser itunesParser = new ItunesParser();

        // When
        File f = new File("src/test/resources/xml/itunes_library_real.xml");
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
        File f = new File("src/test/resources/xml/itunes_library_real.xml");
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
        File f = new File("src/test/resources/xml/itunes_library_real.xml");
        ITunesLibrary library = itunesParser.load(f.getAbsolutePath());

        // Then
        assertNotNull(library.getPList());
        assertNotNull(library.getPList().getDict());

        GeneralDict generalDict = library.getPList().getDict();
        Track track = generalDict.getTracks().get(0);

        assertEquals(Integer.valueOf(1), track.getItunesId());
        assertEquals("Shiny Stockings", track.getName());
        assertEquals("Ella Fitzgerald", track.getArtist());
        assertEquals(Integer.valueOf(131552), track.getTotalTime());
        assertEquals(Integer.valueOf(55), track.getStartTime());
        assertEquals(Integer.valueOf(131000), track.getStopTime());
        assertEquals(Integer.valueOf(120), track.getBpm());
        assertEquals(Integer.valueOf(80), track.getRating());
        assertEquals(Integer.valueOf(1990), track.getYear());
        assertEquals("30 - Some comment", track.getComments());
        assertEquals("file://localhost/mnt/44D38A27637CE7D3/musiques/itunes/iTunes%20Media/Music/Ella%20Fitzgerald_Count%20Basie/Ella%20&%20Basie!/09%20Shiny%20Stockings.mp3", track.getLocation());
        assertEquals(LocalDateTime.of(2012, 12, 24, 13, 54, 56), track.getDateModified());
    }

    @Test
    void load_should_return_library_with_tracks_with_no_extra_fields() throws IOException, SAXException, ParserConfigurationException {
        // Given
        ItunesParser itunesParser = new ItunesParser();

        // When
        File f = new File("src/test/resources/xml/itunes_library_real.xml");
        ITunesLibrary library = itunesParser.load(f.getAbsolutePath());

        // Then
        assertNotNull(library.getPList());
        assertNotNull(library.getPList().getDict());

        GeneralDict generalDict = library.getPList().getDict();

        generalDict.getTracks().forEach(track ->
                assertEquals(0, track.getExtraProperties().size(),
                        String.format("Erreur sur la track %d : %s", track.getItunesId(), track.getExtraProperties().toString())));
    }

    @Test
    void load_should_return_library_with_playlists() throws IOException, SAXException, ParserConfigurationException {
        // Given
        ItunesParser itunesParser = new ItunesParser();

        // When
        File f = new File("src/test/resources/xml/itunes_library_real.xml");
        ITunesLibrary library = itunesParser.load(f.getAbsolutePath());

        // Then
        assertNotNull(library.getPList());
        assertNotNull(library.getPList().getDict());

        GeneralDict generalDict = library.getPList().getDict();
        assertEquals(2, generalDict.getPlaylists().size());

        assertFalse(generalDict.getPlaylists().get(0).isMaster());
        assertEquals("Test Folder", generalDict.getPlaylists().get(0).getName());
        assertEquals(Integer.valueOf(14525), generalDict.getPlaylists().get(0).getPlaylistId());
        assertEquals("42A45B8FD23FA86A", generalDict.getPlaylists().get(0).getPlaylistPersistentId());
        assertNull(generalDict.getPlaylists().get(0).getParentPersistentId());
        assertTrue(generalDict.getPlaylists().get(0).getAllItems());
        assertFalse(generalDict.getPlaylists().get(0).getVisible());

        assertTrue(generalDict.getPlaylists().get(1).isMaster());
        assertEquals("Test Playlist", generalDict.getPlaylists().get(1).getName());
        assertEquals(Integer.valueOf(12345), generalDict.getPlaylists().get(1).getPlaylistId());
        assertEquals("3E6916F95560A2DD", generalDict.getPlaylists().get(1).getPlaylistPersistentId());
        assertEquals("42A45B8FD23FA86A", generalDict.getPlaylists().get(1).getParentPersistentId());
        assertTrue(generalDict.getPlaylists().get(1).getAllItems());
        assertTrue(generalDict.getPlaylists().get(1).getVisible());
    }

    @Test
    void load_should_return_library_with_playlists_with_no_extra_fields() throws IOException, SAXException, ParserConfigurationException {
        // Given
        ItunesParser itunesParser = new ItunesParser();

        // When
        File f = new File("src/test/resources/xml/itunes_library_real.xml");
        ITunesLibrary library = itunesParser.load(f.getAbsolutePath());

        // Then
        assertNotNull(library.getPList());
        assertNotNull(library.getPList().getDict());

        GeneralDict generalDict = library.getPList().getDict();

        generalDict.getPlaylists().forEach(playlist ->
                assertEquals(0, playlist.getExtraProperties().size(),
                        String.format("Erreur sur la playlist %d : %s", playlist.getPlaylistId(), playlist.getExtraProperties().toString())));
    }

    @Test
    void load_should_return_library_with_playlists_with_items() throws IOException, SAXException, ParserConfigurationException {
        // Given
        ItunesParser itunesParser = new ItunesParser();

        // When
        File f = new File("src/test/resources/xml/itunes_library_real.xml");
        ITunesLibrary library = itunesParser.load(f.getAbsolutePath());

        // Then
        assertNotNull(library.getPList());
        assertNotNull(library.getPList().getDict());

        GeneralDict generalDict = library.getPList().getDict();
        assertEquals(2, generalDict.getPlaylists().size());

        assertEquals(2, generalDict.getPlaylists().get(0).getItems().size());
        assertEquals(Integer.valueOf(1), generalDict.getPlaylists().get(0).getItems().get(0).getTrackId());
        assertEquals(Integer.valueOf(2), generalDict.getPlaylists().get(0).getItems().get(1).getTrackId());

        assertEquals(3, generalDict.getPlaylists().get(1).getItems().size());
        assertEquals(Integer.valueOf(1), generalDict.getPlaylists().get(1).getItems().get(0).getTrackId());
        assertEquals(Integer.valueOf(3), generalDict.getPlaylists().get(1).getItems().get(1).getTrackId());
        assertEquals(Integer.valueOf(4), generalDict.getPlaylists().get(1).getItems().get(2).getTrackId());
    }

    @Test
    void load_should_return_library_with_playlists_with_items_with_no_extra_fields() throws IOException, SAXException, ParserConfigurationException {
        // Given
        ItunesParser itunesParser = new ItunesParser();

        // When
        File f = new File("src/test/resources/xml/itunes_library_real.xml");
        ITunesLibrary library = itunesParser.load(f.getAbsolutePath());

        // Then
        assertNotNull(library.getPList());
        assertNotNull(library.getPList().getDict());

        GeneralDict generalDict = library.getPList().getDict();

        generalDict.getPlaylists().forEach(playlist ->
                Optional.ofNullable(playlist.getItems()).ifPresent(items -> items.forEach(item ->
                        assertEquals(0, item.getExtraProperties().size(),
                                String.format("Erreur sur la playlist %d et l'item %d : %s", playlist.getPlaylistId(),
                                        item.getTrackId(), item.getExtraProperties().toString())))));
    }

//    @Test
//    void test() throws IOException, SAXException, ParserConfigurationException {
//        // Given
//        ItunesParser itunesParser = new ItunesParser();
//
//        // When
//        File f = new File("src/test/resources/xml/itunes_library_real.xml");
//        ITunesLibrary currentLibrary = itunesParser.load(f.getAbsolutePath());
//
//        f = new File("src/test/resources/xml/itunes_library_real.xml");
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