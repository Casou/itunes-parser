package com.bparent.itunes.model;

import com.bparent.itunes.utils.ConsoleColors;
import lombok.Data;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class TrackWithSuggestions extends Track {

    private List<File> suggestions;

    public TrackWithSuggestions() {
        super(null);
    }

    public void setSuggestions(List<File> suggestions) {
        for (File suggestion : suggestions) {
            if (!suggestion.exists()) {
                throw new RuntimeException("Suggestion file not found for track " + this.getName().getValue() + " : "
                        + suggestion.getAbsolutePath());
            }
        }
        this.suggestions = suggestions;
    }

    public List<String> getPrintableSuggestions() {
        return suggestions.stream()
                .map(file -> {
                    String artist = this.getArtist() == null ? "Unknown Artist" : this.getArtist().getValue().trim();
                    String album = this.getAlbum() == null ? "Unknown Album" : this.getAlbum().getValue().trim();
                    String decodedLocation = this.getDecodedLocation();
                    String fileName = new File(decodedLocation).getName();
                    String title = this.getName().getValue();
                    return highlightMatching(file.getAbsolutePath(), artist, album, fileName, title);
                })
                .collect(Collectors.toList());
    }

    private String highlightMatching(String path, String artist, String album, String fileName, String title) {
        int artistIndex = path.toLowerCase().indexOf("/" + artist.toLowerCase() + "/");
        if (artistIndex > -1) {
            path = highlightString(path, artist, artistIndex + 1, ConsoleColors.PURPLE_BRIGHT);
        }
        int albumIndex = path.toLowerCase().indexOf("/" + album.toLowerCase() + "/");
        if (albumIndex > -1) {
            path = highlightString(path, album, albumIndex + 1, ConsoleColors.CYAN_BRIGHT);
        }
        int indexFileName = path.toLowerCase().indexOf(fileName.toLowerCase());
        if (indexFileName > -1) {
            path = highlightString(path, fileName, indexFileName, ConsoleColors.GREEN_BRIGHT);
        } else {
            int indexTitle = path.toLowerCase().indexOf(title.toLowerCase());
            if (indexTitle > -1) {
                path = highlightString(path, title, indexTitle, ConsoleColors.GREEN);
            }
        }

        return path;
    }

    private String highlightString(String path, String s, int index, String blue) {
        return path.substring(0, index)
                + blue + path.substring(index, index + s.length()) + ConsoleColors.RESET
                + path.substring(index + s.length());
    }

}
