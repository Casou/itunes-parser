package com.bparent.itunes.utils;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    @Test
    void listAllFile_should_return_file_list_if_folder_exists() {
        List<File> files = FileUtils.listAllFile("src/test/resources");

        assertNotNull(files);
        assertEquals(5, files.size());
        assertEquals("void_music_1.mp3", files.get(0).getName());
        assertEquals("void_music_2.mp3", files.get(1).getName());
        assertEquals("itunes_library_current.xml", files.get(2).getName());
        assertEquals("itunes_library_real.xml", files.get(3).getName());
        assertEquals("itunes_library_test.xml", files.get(4).getName());
    }

    @Test
    void listAllFile_should_return_null_if_folder_does_not_exists() {
        assertNull(FileUtils.listAllFile("folder/not/existing"));
    }

}