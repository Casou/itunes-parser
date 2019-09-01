import com.bparent.itunes.model.ITunesLibrary;
import com.bparent.itunes.model.Track;
import com.bparent.itunes.model.TrackWithSuggestions;
import com.bparent.itunes.parser.ItunesParser;
import com.bparent.itunes.utils.ConsoleColors;
import com.bparent.itunes.utils.ITunesUtils;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ItunesParserReplacementsTest {

    @Test
    void test() throws IOException, SAXException, ParserConfigurationException {
        ItunesParser itunesParser = new ItunesParser();
        File f = new File("src/test/resources/xml/itunes_library_real.xml");
        ITunesLibrary currentLibrary = itunesParser.load(f.getAbsolutePath());

        String itunesFolderPath = "/mnt/44D38A27637CE7D3/musiques/itunes";
        List<Track> missingFiles = ITunesUtils.getMissingFiles(currentLibrary, itunesFolderPath);
        System.out.println(ConsoleColors.RED_BOLD_BRIGHT + missingFiles.size() + ConsoleColors.RESET +" missing file(s)");

        List<TrackWithSuggestions> trackListWithSuggestions = ITunesUtils.suggestMissingFilesReplacement(missingFiles, itunesFolderPath);

        int i = 1;
        for (TrackWithSuggestions trackWithSuggestions : trackListWithSuggestions) {
            String artist = trackWithSuggestions.getArtist() == null ? "Unknown Artist" : trackWithSuggestions.getArtist().getValue().trim();
            String album = trackWithSuggestions.getAlbum() == null ? "Unknown Album" : trackWithSuggestions.getAlbum().getValue().trim();
            String decodedLocation = trackWithSuggestions.getDecodedLocation();
            String title = trackWithSuggestions.getName().getValue();
            System.out.println(
                    String.format("%s[%03d]%s %s / %s / %s : %s%d%s replacement(s)\n%s=> %s%s\n%s\n\n",
                            ConsoleColors.CYAN,
                            i,
                            ConsoleColors.RESET,
                            title,
                            artist,
                            album,
                            trackWithSuggestions.getSuggestions().size() == 0 ? ConsoleColors.RED_BOLD_BRIGHT : trackWithSuggestions.getSuggestions().size() == 1 ? ConsoleColors.GREEN_BOLD_BRIGHT : ConsoleColors.YELLOW_BOLD_BRIGHT,
                            trackWithSuggestions.getSuggestions().size(),
                            ConsoleColors.RESET,
                            ConsoleColors.BLACK_BRIGHT,
                            decodedLocation,
                            ConsoleColors.RESET,
                            trackWithSuggestions.getPrintableSuggestions().stream()
                                    .map(suggestion -> "\t" + suggestion)
                                    .collect(Collectors.joining("\n"))
                    ));
            i++;
        }
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