package com.bparent.itunes.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class FileUtils {

    private static final FilenameFilter audioFilter = (directory, fileName) -> fileName.endsWith(".mp3")
            || fileName.endsWith(".m4a")
            || fileName.endsWith(".wav")
            || fileName.endsWith(".wmv")
            || new File(directory, fileName).isDirectory();

    static List<File> listAllFile(String folderName) {
        File folder = new File(folderName);
        if (!folder.exists()) {
            return null;
        }
        return Arrays.stream(Objects.requireNonNull(folder.listFiles(audioFilter)))
                .flatMap(file -> {
                    if (file.isDirectory()) {
                        return Objects.requireNonNull(listAllFile(folderName + "/" + file.getName())).stream();
                    }
                    return Stream.of(file);
                }).collect(Collectors.toList());
    }
    
}
