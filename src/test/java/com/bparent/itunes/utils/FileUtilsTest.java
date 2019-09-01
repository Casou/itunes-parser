package com.bparent.itunes.utils;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    @Test
    void listAllFile_should_return_a_file_list_with_all_files_if_folder_exists_and_no_filter() {
        List<File> files = FileUtils.listAllFiles("src/test/resources", null);

        assertNotNull(files);
        assertEquals(5, files.size());
        assertEquals("void_music_1.mp3", files.get(0).getName());
        assertEquals("void_music_2.mp3", files.get(1).getName());
        assertEquals("itunes_library_current.xml", files.get(2).getName());
        assertEquals("itunes_library_real.xml", files.get(3).getName());
        assertEquals("itunes_library_test.xml", files.get(4).getName());
    }

    @Test
    void listAllFile_should_return_a_file_list_with_only_audio_files_if_folder_exists_and_audio_filter() {
        // Given
        FilenameFilter audioFilter = (directory, fileName) -> fileName.endsWith(".mp3")
                || fileName.endsWith(".m4a")
                || fileName.endsWith(".wav")
                || fileName.endsWith(".wmv")
                || new File(directory, fileName).isDirectory();

        // When
        List<File> files = FileUtils.listAllFiles("src/test/resources", audioFilter);

        // Then
        assertNotNull(files);
        assertEquals(2, files.size());
        assertEquals("void_music_1.mp3", files.get(0).getName());
        assertEquals("void_music_2.mp3", files.get(1).getName());
    }

    @Test
    void listAllFile_should_return_null_if_folder_does_not_exists() {
        assertNull(FileUtils.listAllFiles("folder/not/existing", null));
    }
    
    @Test
    void getArtist_should_return_first_music_folder_as_artist() {
        assertEquals("Michael Jackson", FileUtils.getArtist("file://localhost/D:/musiques/itunes/iTunes Media/Music/Michael Jackson/Blood On The Dance Floor/27 - Ghost.mp3"));
        assertEquals("Unknown Artist", FileUtils.getArtist("file://localhost/D:/musiques/itunes/iTunes Media/Music/Unknown Artist/Unknown Album/03 25 - Piste 3___.m4a"));
    }

    @Test
    void getAlbum_should_return_second_music_folder_as_album() {
        assertEquals("Blood On The Dance Floor", FileUtils.getAlbum("file://localhost/D:/musiques/itunes/iTunes Media/Music/Michael Jackson/Blood On The Dance Floor/27 - Ghost.mp3"));
        assertEquals("Unknown Album", FileUtils.getAlbum("file://localhost/D:/musiques/itunes/iTunes Media/Music/Unknown Artist/Unknown Album/03 25 - Piste 3___.m4a"));
    }

}