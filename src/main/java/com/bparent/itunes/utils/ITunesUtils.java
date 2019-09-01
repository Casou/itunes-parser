package com.bparent.itunes.utils;

import com.bparent.itunes.model.ITunesLibrary;
import com.bparent.itunes.model.Track;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ITunesUtils {

    private static final FilenameFilter audioFilter = (directory, fileName) -> fileName.endsWith(".mp3")
            || fileName.endsWith(".m4a")
            || fileName.endsWith(".wav")
            || fileName.endsWith(".wmv")
            || new File(directory, fileName).isDirectory();

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

    public static Map<Track, List<File>> suggestMissingFilesReplacement(List<Track> missingTracks, String itunesFolderPath) {
        List<File> files = FileUtils.listAllFiles(itunesFolderPath, audioFilter);

        return missingTracks.stream()
                .collect(Collectors.toMap(track -> track, track -> findReplacements(track, files)));
    }

    private static List<File> findReplacements(Track track, List<File> allFiles) {
        return allFiles.stream()
                .filter(file -> file.getName().contains(new File(track.getName().getValue()).getName())
                        || file.getName().contains(new File(track.getLocation().getValue()).getName())
                        || file.getName().contains(track.getName().getValue()))
                .sorted((file1, file2) -> {
                    if (file1.getName().contains(new File(track.getName().getValue()).getName())) {
                        return -1;
                    }
                    if (file2.getName().contains(new File(track.getName().getValue()).getName())) {
                        return 1;
                    }
                    return file1.getAbsolutePath().compareTo(file2.getAbsolutePath());
                })
                .collect(Collectors.toList());
    }

}
