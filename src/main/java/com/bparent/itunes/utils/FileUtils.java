package com.bparent.itunes.utils;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {

    private static final String ITUNES_MUSIC_FOLDER_IDENTIFIER = "iTunes Media/Music";

    static List<File> listAllFiles(String folderName, FilenameFilter filenameFilter) {
        File folder = new File(folderName);
        if (!folder.exists()) {
            return null;
        }
        return Arrays.stream(Objects.requireNonNull(folder.listFiles(filenameFilter)))
                .flatMap(file -> {
                    if (file.isDirectory()) {
                        return Objects.requireNonNull(listAllFiles(folderName + "/" + file.getName(), filenameFilter)).stream();
                    }
                    return Stream.of(file);
                }).collect(Collectors.toList());
    }

    static String getArtist(String filePath) {
        String artist = filePath.substring(filePath.indexOf(ITUNES_MUSIC_FOLDER_IDENTIFIER) + ITUNES_MUSIC_FOLDER_IDENTIFIER.length() + 1);
        return artist.substring(0, artist.indexOf("/"));
    }

    static String getAlbum(String filePath) {
        String album = filePath.substring(filePath.indexOf(ITUNES_MUSIC_FOLDER_IDENTIFIER) + ITUNES_MUSIC_FOLDER_IDENTIFIER.length() + 1);
        album = album.substring(album.indexOf("/") + 1);
        return album.substring(0, album.indexOf("/"));
    }

    public static void writeInFile(String filePath, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(content);

        writer.close();
    }
}
