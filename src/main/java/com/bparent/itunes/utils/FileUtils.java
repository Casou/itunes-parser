package com.bparent.itunes.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class FileUtils {

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
    
}
