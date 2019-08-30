package com.bparent.itunes.utils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class FileUtils {
    
    static List<File> listAllFile(String folderName) {
        File folder = new File(folderName);
        if (!folder.exists()) {
            return null;
        }
        return Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                .flatMap(file -> {
                    if (file.isDirectory()) {
                        return listAllFile(folderName + "/" + file.getName()).stream();
                    }
                    return Stream.of(file);
                }).collect(Collectors.toList());
    }
    
}
