package com.bparent.itunes.cli;

import com.bparent.itunes.cli.exception.CommandNotFoundException;
import com.bparent.itunes.model.ITunesLibrary;
import com.bparent.itunes.parser.ItunesParser;
import com.bparent.itunes.utils.ConsoleColors;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class CliParser {

    private File xmlFile;
    private ITunesLibrary library;
    private CliFileRepair cliFileRepair;
    private CliExporter cliExporter;

    public CliParser(Map<String, String> arguments) throws IOException, SAXException, ParserConfigurationException {
        String xmlFilePath = arguments.get("itunes.xmlFile");
        ItunesParser parser = new ItunesParser();
        this.library = parser.load(xmlFilePath);
        this.xmlFile = new File(xmlFilePath);
        this.cliFileRepair = new CliFileRepair(this.library, arguments);
        this.cliExporter = new CliExporter(this.library, this.xmlFile);
        System.out.println("iTunes Music folder : " + arguments.get("itunes.folder"));
        System.out.println("XML parsed : " + xmlFilePath);
        System.out.println("* " + this.library.getPList().getDict().getTracks().size() + " tracks");
        System.out.println("* " + this.library.getPList().getDict().getPlaylists().size() + " playlists");
    }

    public void askForCommand() {
        showMenu();

        Scanner scanner = new Scanner(System.in);
        String command = scanner.nextLine();
        try {
            handleCommand(command);
        } catch (CommandNotFoundException e) {
            System.err.println(e.getMessage());
        }

        askForCommand();
    }

    private void showMenu() {
        System.out.println(ConsoleColors.PURPLE_BRIGHT + "Menu :" + ConsoleColors.RESET);
        if (this.library.getUnsavedModifications()) {
            System.out.println(ConsoleColors.YELLOW + "⚠ Unsaved modifications on library" + ConsoleColors.RESET);
        }
        System.out.println("(1) Repair missing files");
        System.out.println("(9) Export changes in XML");
        System.out.println("(0) Quit");
        System.out.print(ConsoleColors.BLUE + "Choose an item : " + ConsoleColors.RESET);
    }

    private void handleCommand(String command) throws CommandNotFoundException {
        switch(command) {
            case "0" :
                System.out.println("Quit");
                System.exit(0);
                break;
            case "1" :
                this.cliFileRepair.askForCommand();
                break;
            case "9" :
                this.cliExporter.exportLibrary();
                break;
            default:
                throw new CommandNotFoundException("Command " + command + " not found");
        }
    }

}
