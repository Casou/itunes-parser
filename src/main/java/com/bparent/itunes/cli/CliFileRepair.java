package com.bparent.itunes.cli;

import com.bparent.itunes.cli.exception.CommandNotFoundException;
import com.bparent.itunes.cli.exception.ExitCommandException;
import com.bparent.itunes.model.ITunesLibrary;
import com.bparent.itunes.model.Track;
import com.bparent.itunes.model.TrackWithSuggestions;
import com.bparent.itunes.type.XmlString;
import com.bparent.itunes.utils.ConsoleColors;
import com.bparent.itunes.utils.ITunesUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CliFileRepair {

    private String itunesFolderPath;
    private String itunesFolderExportPath;
    private ITunesLibrary library;
    private List<TrackWithSuggestions> missingFiles;

    public CliFileRepair(ITunesLibrary library, Map<String, String> arguments) {
        this.library = library;
        this.itunesFolderPath = arguments.get("itunes.folder");
        this.itunesFolderExportPath = arguments.get("itunes.folder.export");
        this.missingFiles = null;
    }

    private void searchForMissingFile() {
        List<Track> missingFiles = ITunesUtils.getMissingFiles(library, itunesFolderPath);
        this.missingFiles = ITunesUtils.suggestMissingFilesReplacement(missingFiles, itunesFolderPath);
    }

    public void askForCommand() {
        showMenu();

        Scanner scanner = new Scanner(System.in);
        String command = scanner.nextLine();
        try {
            handleCommand(command);
        } catch (CommandNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (ExitCommandException e) {
            return;
        }

        askForCommand();
    }

    private void handleCommand(String command) throws CommandNotFoundException, ExitCommandException {
        switch (command) {
            case "1":
                refreshMissingFiles();
                break;
            case "2":
                autoRepairOneSuggestion();
                break;
            case "3":
                repairNoSuggestion();
                break;
            case "4":
                repairMultipleSuggestions();
                break;
            case "5":
                autoRepairMultipleSuggestions();
                break;
            case "0":
                throw new ExitCommandException();
            default:
                throw new CommandNotFoundException("Command " + command + " not found");
        }
    }

    private void showMenu() {
        System.out.println(ConsoleColors.PURPLE_BRIGHT + "Repair missing files : " + ConsoleColors.RESET);
        if (this.library.getUnsavedModifications()) {
            System.out.println(ConsoleColors.YELLOW + "⚠ Unsaved modifications on library" + ConsoleColors.RESET);
        }
        if (this.missingFiles == null) {
            System.out.println("Searching for missing files and replacements... (might take a while)");
            this.searchForMissingFile();
        }

        System.out.println(getColor(this.missingFiles.size()) + this.missingFiles.size() + ConsoleColors.RESET + " missing file(s) found");

        long zeroSuggestionFileCount = this.missingFiles.stream().filter(track -> track.getSuggestions().size() == 0).count();
        long oneSuggestionFileCount = this.missingFiles.stream().filter(track -> track.getSuggestions().size() == 1).count();
        long multipleSuggestionsFileCount = this.missingFiles.stream().filter(track -> track.getSuggestions().size() > 1).count();
        System.out.println(String.format(
                "%s files with no suggestion, %s files with 1 suggestion (auto-repairables), %s files with multiple suggestions.",
                getColor(zeroSuggestionFileCount) + zeroSuggestionFileCount + ConsoleColors.RESET,
                getColor(oneSuggestionFileCount) + oneSuggestionFileCount + ConsoleColors.RESET,
                getColor(multipleSuggestionsFileCount) + multipleSuggestionsFileCount + ConsoleColors.RESET
        ));

        System.out.println("(1) Refresh missing files");
        System.out.println("(2) Auto-repair files with 1 suggestion");
        System.out.println("(3) Manually repair files with no suggestion");
        System.out.println("(4) Manually repair files with multiple suggestions");
        System.out.println("(5) Auto-repair files with multiple suggestions with the best one");
        System.out.println("(0) Return to main menu");
        System.out.print(ConsoleColors.BLUE + "Choose an item : " + ConsoleColors.RESET);
    }

    private String getColor(long size) {
        return size == 0 ? ConsoleColors.GREEN_BOLD_BRIGHT : ConsoleColors.YELLOW_BOLD_BRIGHT;
    }

    private String getColor(int size) {
        return size == 0 ? ConsoleColors.GREEN_BOLD_BRIGHT : ConsoleColors.YELLOW_BOLD_BRIGHT;
    }

    private void refreshMissingFiles() {
        this.searchForMissingFile();
    }

    private void autoRepairOneSuggestion() {
        List<TrackWithSuggestions> autoRepairableMissingFiles = this.missingFiles.stream()
                .filter(track -> track.getSuggestions().size() == 1)
                .collect(Collectors.toList());
        autoRepair(autoRepairableMissingFiles);
    }

    private void autoRepairMultipleSuggestions() {
        List<TrackWithSuggestions> autoRepairableMissingFiles = this.missingFiles.stream()
                .filter(track -> track.getSuggestions().size() > 1)
                .collect(Collectors.toList());
        autoRepair(autoRepairableMissingFiles);
    }

    private void autoRepair(List<TrackWithSuggestions> autoRepairableMissingFiles) {
        if (autoRepairableMissingFiles.size() == 0) {
            System.out.println(ConsoleColors.GREEN_BRIGHT + "No auto-repairable file found" + ConsoleColors.RESET);
            return;
        }

        System.out.println(ConsoleColors.GREEN_BRIGHT + "Repairing " + autoRepairableMissingFiles.size() + " file(s)." + ConsoleColors.RESET);
        autoRepairableMissingFiles.forEach(track -> {
            String newLocation = getNewLocation(track.getSuggestions().get(0).getAbsolutePath());
            repairFile(track, newLocation);
        });
    }

    public String getNewLocation(String suggestion) {
        return itunesFolderExportPath + suggestion.substring(suggestion.indexOf("iTunes Media") + "iTunes Media".length());
    }

    private void repairNoSuggestion() {
        List<TrackWithSuggestions> noSuggestionMissingFiles = this.missingFiles.stream()
                .filter(track -> track.getSuggestions().size() == 0)
                .collect(Collectors.toList());
        if (noSuggestionMissingFiles.size() == 0) {
            System.out.println(ConsoleColors.GREEN_BRIGHT + "No missing file with no suggestion found" + ConsoleColors.RESET);
            return;
        }

        System.out.println(ConsoleColors.GREEN_BRIGHT + "Repairing " + noSuggestionMissingFiles.size() + " file(s)." + ConsoleColors.RESET);
        noSuggestionMissingFiles.forEach(track -> {

            System.out.println(String.format("Title : %s / Artist : %s / Album : %s\n=> %s",
                    track.getName(),
                    track.getArtist(),
                    track.getAlbum(),
                    track.getDecodedLocation()
                    ));
            Scanner scanner = new Scanner(System.in);

            File f = askForFile(scanner);
            if (f == null) return;

            String newLocation = getNewLocation(f.getAbsolutePath());
            repairFile(track, newLocation);
        });
    }

    private void repairFile(TrackWithSuggestions track, String newLocation) {
        System.out.println(ConsoleColors.WHITE + track.getName() + " : " + track.getLocation() + " -> " + newLocation + ConsoleColors.RESET);
        Track libraryTrack = this.library.getPList().getDict().getTracks().stream()
                .filter(t -> t.getItunesId().getValue().equals(track.getItunesId().getValue()))
                .findAny()
                .orElse(null);
        if (libraryTrack == null) {
            System.err.println("Track " + track.getItunesId() + " not found in library");
            return;
        }
        libraryTrack.setLocation(new XmlString(newLocation));
        libraryTrack.setComments(new XmlString("FIXED " + track.getComments()));
        this.library.setUnsavedModifications(true);

        this.missingFiles.remove(track);
    }


    private void repairMultipleSuggestions() {
        List<TrackWithSuggestions> mulitpleSuggestionsMissingFiles = this.missingFiles.stream()
                .filter(track -> track.getSuggestions().size() > 1)
                .collect(Collectors.toList());
        if (mulitpleSuggestionsMissingFiles.size() == 0) {
            System.out.println(ConsoleColors.GREEN_BRIGHT + "No missing file with multiple suggestions found" + ConsoleColors.RESET);
            return;
        }

        System.out.println(ConsoleColors.GREEN_BRIGHT + "Repairing " + mulitpleSuggestionsMissingFiles.size() + " file(s)." + ConsoleColors.RESET);
        for (TrackWithSuggestions track : mulitpleSuggestionsMissingFiles) {
            System.out.println(String.format("%sArtist%s : %s / %sAlbum%s : %s / %sTitle%s : %s\n%s=> %s%s",
                    ConsoleColors.PURPLE_BRIGHT, ConsoleColors.RESET, track.getArtist(),
                    ConsoleColors.CYAN_BRIGHT, ConsoleColors.RESET, track.getAlbum(),
                    ConsoleColors.GREEN_BRIGHT, ConsoleColors.RESET, track.getName(),
                    ConsoleColors.BLACK_BRIGHT, track.getDecodedLocation(), ConsoleColors.RESET
            ));
            for (int i = 0; i < track.getPrintableSuggestions().size(); i++) {
                System.out.println(String.format("\t%s%s(%d)%s %s",
                        i == 0 ? ConsoleColors.CYAN_BOLD : ConsoleColors.CYAN,
                        i == 0 ? "*" : " ",
                        i + 1,
                        ConsoleColors.RESET,
                        track.getPrintableSuggestions().get(i)
                ));
            }
            System.out.println("\t " + ConsoleColors.CYAN + "(0)" + ConsoleColors.RESET + " Choose manually");
            System.out.println("\t " + ConsoleColors.CYAN + "(quit)" + ConsoleColors.RESET + " To return to the menu");

            Scanner scanner = new Scanner(System.in);
            String location = null;
            while (location == null) {
                System.out.print(ConsoleColors.BLUE + "Choose one song (empty to choose the default one) : " + ConsoleColors.RESET);
                String command = scanner.nextLine().trim();
                if (command.equals("quit")) {
                    return;
                }
                if (command.equals("0")) {
                    File f = askForFile(scanner);
                    if (f == null) {
                        continue;
                    }
                    location = f.getAbsolutePath();
                } else {
                    if (StringUtils.isEmpty(command)) {
                        command = "1";
                    }
                    int commandIndex;
                    try {
                        commandIndex = Integer.parseInt(command);
                    } catch(NumberFormatException e) {
                        System.err.println("Choice " + command + " is incorrect");
                        continue;
                    }

                    commandIndex--;
                    if (commandIndex >= track.getSuggestions().size() || commandIndex < 0) {
                        System.err.println("Choice " + command + " is incorrect");
                        continue;
                    }
                    location = track.getSuggestions().get(commandIndex).getAbsolutePath();
                }
            }

            String newLocation = getNewLocation(location);
            repairFile(track, newLocation);
        }
    }

    private File askForFile(Scanner scanner) {
        String command;
        File f = null;

        while (f == null || !f.exists()) {
            if (f != null) {
                System.out.println(ConsoleColors.YELLOW + "File " + f.getAbsolutePath() + " not found" + ConsoleColors.RESET);
            }
            System.out.print(ConsoleColors.BLUE + "Type the full path of the song (empty to skip) : " + ConsoleColors.RESET);
            command = scanner.nextLine();
            if (StringUtils.isEmpty(command)) {
                return null;
            }
            f = new File(command);
        }
        return f;
    }

}
