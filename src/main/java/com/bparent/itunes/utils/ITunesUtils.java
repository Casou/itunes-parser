package com.bparent.itunes.utils;

import com.bparent.itunes.model.ITunesLibrary;
import com.bparent.itunes.model.Track;
import com.bparent.itunes.model.TrackWithSuggestions;
import org.modelmapper.ModelMapper;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ITunesUtils {

    private static final FilenameFilter audioFilter = (directory, fileName) -> fileName.endsWith(".mp3")
            || fileName.endsWith(".m4a")
            || fileName.endsWith(".wav")
            || fileName.endsWith(".wmv")
            || new File(directory, fileName).isDirectory();

    private static final String UNKNOWN_ARTIST = "Unknown Artist";

    private ITunesUtils() {
    }

    public static List<Track> getMissingFiles(ITunesLibrary iTunesLibrary, String itunesFolderPath) {
        List<Track> tracks = new ArrayList<>();

        for (Track track : iTunesLibrary.getPList().getDict().getTracks()) {
            String filePath = itunesFolderPath + "/"
                    + track.getDecodedLocation().substring(track.getLocation().getValue().indexOf("iTunes%20Media"));
            File f = new File(filePath);
            if (!f.exists()) {
                tracks.add(track);
            }
        }

        return tracks;
    }

    public static List<TrackWithSuggestions> suggestMissingFilesReplacement(List<Track> missingTracks, String itunesFolderPath) {
        List<File> files = FileUtils.listAllFiles(itunesFolderPath, audioFilter);
        ModelMapper modelMapper = new ModelMapper();

        return missingTracks.stream()
                .map(track -> {
                    TrackWithSuggestions trackWithSuggestions = modelMapper.map(track, TrackWithSuggestions.class);
                    trackWithSuggestions.setSuggestions(findReplacements(track, files));
                    return trackWithSuggestions;
                })
                .collect(Collectors.toList());
    }

    private static List<File> findReplacements(Track track, List<File> allFiles) {
        return allFiles.stream()
                .filter(file -> {
                    String fileName = file.getName().toLowerCase();
                    String itunesSafeName = getItunesSafeName(track.getName().getValue()).toLowerCase();
                    return fileName.contains(itunesSafeName)
                            || fileName.equals(new File(track.getDecodedLocation()).getName().toLowerCase())
                            || fileName.contains(track.getName().getValue().toLowerCase());
                })
                .sorted(sortReplacements(track))
                .collect(Collectors.toList());
    }

    private static String getItunesSafeName(String name) {
        return name
                .replaceAll("[\\/\\?]", "_");
    }

    private static Comparator<File> sortReplacements(Track track) {
        return (file1, file2) -> {
            String file1Name = file1.getName().toLowerCase();
            String file2Name = file2.getName().toLowerCase();
            String artist = track.getArtist() == null ? "" : track.getArtist().getValue().toLowerCase();
            String file1Artist = FileUtils.getArtist(file1.getPath());
            String file2Artist = FileUtils.getArtist(file2.getPath());
            String trackName = new File(track.getDecodedLocation()).getName();

            Integer file1LevenshteinDistance = StringUtils.getLevenshteinDistance(file1Name, trackName);
            Integer file2LevenshteinDistance = StringUtils.getLevenshteinDistance(file2Name, trackName);

            if (file1LevenshteinDistance < 5 && file2LevenshteinDistance >= 8) {
                return -1;
            }
            if (file2LevenshteinDistance < 5 && file1LevenshteinDistance >= 8) {
                return 1;
            }

            Integer file1ArtistLevenshteinDistance = StringUtils.getLevenshteinDistance(file1Artist, artist);
            Integer file2ArtistLevenshteinDistance = StringUtils.getLevenshteinDistance(file2Artist, artist);
            if (file1Artist.equals(UNKNOWN_ARTIST) && file2ArtistLevenshteinDistance > 8) {
                if (file1LevenshteinDistance < 5) {
                    return -1;
                }
                return file1LevenshteinDistance - file2LevenshteinDistance;
            }
            if (file2Artist.equals(UNKNOWN_ARTIST) && file1ArtistLevenshteinDistance > 8) {
                if (file2LevenshteinDistance < 5) {
                    return 1;
                }
                return file1LevenshteinDistance - file2LevenshteinDistance;
            }

            if (file1ArtistLevenshteinDistance - file2ArtistLevenshteinDistance == 0) {
                String album = track.getAlbum() == null ? "Unknown Album" : track.getAlbum().getValue();
                file1ArtistLevenshteinDistance = StringUtils.getLevenshteinDistance(FileUtils.getAlbum(file1.getPath()), album);
                file2ArtistLevenshteinDistance = StringUtils.getLevenshteinDistance(FileUtils.getAlbum(file2.getPath()), album);
            }

            return (file1LevenshteinDistance + file1ArtistLevenshteinDistance) - (file2LevenshteinDistance + file2ArtistLevenshteinDistance);

//            // If file name (with extension) is almost the same for file 1
//            if (StringUtils.areAlmostIdentical(file1Name, trackName)) {
//                // Check if file name is the same for file 2 too
//                if (StringUtils.areAlmostIdentical(file2Name, trackName)) {
//                    // Then check if artists are the same
//                    if (StringUtils.areAlmostIdentical(file1Artist, artist)) {
//                        return -1;
//                    } else if (StringUtils.areAlmostIdentical(file2Artist, artist)) {
//                        return 1;
//                    }
//                }
//                return -1;
//            }
//            // If file name (with extension) is almost the same for file 2
//            if (StringUtils.areAlmostIdentical(file2Name, trackName.toLowerCase())) {
//                return 1;
//            }
//
//            // Sort by levenshtein distance
//            if (file1LevenshteinDistance < file2LevenshteinDistance) {
//                return -1;
//            } else if (file1LevenshteinDistance > file2LevenshteinDistance) {
//                return 1;
//            }
//            return file1.getAbsolutePath().compareTo(file2.getAbsolutePath());
        };
    }

}
