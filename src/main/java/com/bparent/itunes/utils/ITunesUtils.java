package com.bparent.itunes.utils;

import com.bparent.itunes.model.ITunesLibrary;
import com.bparent.itunes.model.Track;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ITunesUtils {

    private ITunesUtils() {
    }

    public static List<Track> getMissingFiles(ITunesLibrary iTunesLibrary, String itunesFolderPath) throws UnsupportedEncodingException {
        List<Track> tracks = new ArrayList<>();

        for (Track track : iTunesLibrary.getPList().getDict().getTracks()) {
            String filePath = itunesFolderPath + "/" + URLDecoder.decode(track.getLocation().substring(track.getLocation().indexOf("iTunes%20Media")), "UTF-8");
            File f = new File(filePath);
            if (!f.exists()) {
                tracks.add(track);
            }
        }

        return tracks;
    }

    public static Map<Track, List<File>> suggestMissingFilesReplacement(List<Track> missingTracks, String itunesFolderPath) {
        List<File> files = FileUtils.listAllFile(itunesFolderPath);

        return missingTracks.stream()
                .collect(Collectors.toMap(track -> track, track -> findReplacements(track, files)));
    }

    private static List<File> findReplacements(Track track, List<File> allFiles) {
        return allFiles.stream()
                .filter(file -> file.getName().equalsIgnoreCase(track.getName()))
                .collect(Collectors.toList());
    }

}
