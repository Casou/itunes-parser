package com.bparent.itunes;

import com.bparent.itunes.cli.CliParser;
import com.bparent.itunes.configuration.ApplicationProperties;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        Map<String, String> arguments = new HashMap<>();
        System.out.println(ApplicationProperties.get("app.name") + ", version " + ApplicationProperties.get("app.version"));
        arguments.put("itunes.folder", ApplicationProperties.get("itunes.folder"));
        arguments.put("itunes.folder.export", ApplicationProperties.get("itunes.folder.export"));
        arguments.put("itunes.xmlFile", ApplicationProperties.get("itunes.xmlFile"));
        arguments.putAll(parseArguments(args));

        if (!checkMandatoryArguments(arguments)) {
            System.exit(2);
        }

        if (!arguments.containsKey("itunes.folder.export") || arguments.get("itunes.folder.export") == null) {
            arguments.put("itunes.folder.export", arguments.get("itunes.folder"));
        }

        CliParser cliParser;
        try {
            cliParser = new CliParser(arguments);
        } catch (Exception e) {
            System.err.println("Exception while parsing file : " + e.getCause() + " : " + e.getMessage());
            System.exit(3);
            return;
        }
        cliParser.askForCommand();
    }

    private static boolean checkMandatoryArguments(Map<String, String> arguments) {
        if (!arguments.containsKey("itunes.folder") || arguments.get("itunes.folder") == null) {
            System.err.println("Parameter itunes.folder missing either in the command line or in the application.properties file");
            return false;
        }
        String itunesFolderPath = arguments.get("itunes.folder");
        File itunesFolder = new File(itunesFolderPath);
        if (!itunesFolder.exists()) {
            System.err.println("The itunes folder passed does not exists : " + itunesFolder.getAbsolutePath());
            return false;
        }
        if (!itunesFolder.isDirectory()) {
            System.err.println("The itunes folder passed is not a directory : " + itunesFolder.getAbsolutePath());
            return false;
        }
        if (Arrays.stream(itunesFolder.listFiles()).noneMatch(file -> file.isDirectory() && file.getName().equals("iTunes Media"))) {
            System.err.println("The itunes folder passed does not seems to be a iTunes folder (no iTunes Media sub-directory) : " + itunesFolder.getAbsolutePath());
            return false;
        }

        if (!arguments.containsKey("itunes.xmlFile") || arguments.get("itunes.xmlFile") == null) {
            System.err.println("Parameter itunes.xmlFile missing either in the command line or in the application.properties file");
            return false;
        }
        String itunesXmlFilePath = arguments.get("itunes.xmlFile");
        if (!itunesXmlFilePath.endsWith(".xml")) {
            System.err.println("The itunes XML file passed does not seems to be a XML file : " + itunesXmlFilePath);
            return false;
        }

        File itunesXmlFile = new File(itunesXmlFilePath);
        if (!itunesXmlFile.exists()) {
            System.err.println("The itunes XML file passed does not exists : " + itunesXmlFile.getAbsolutePath());
            return false;
        }
        if (!itunesXmlFile.isFile()) {
            System.err.println("The itunes XML file passed is not a directory : " + itunesXmlFile.getAbsolutePath());
            return false;
        }

        return true;
    }

    private static Map<String, String> parseArguments(String[] args) {
        Map<String, String> arguments = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            String key = args[i];
            if (key.equals("--help")) {
                showHelp(args);
            }
            if (key.startsWith("-")) {
                if (i+1 >= args.length) {
                    System.err.println("You declared a key without value : " + key);
                    System.exit(1);
                }
                i++;
                arguments.put(key.substring(1), args[i]);
            }
        }

        return arguments;
    }

    private static void showHelp(String[] args) {
        if (args.length > 1) {
            System.err.println("The --help option should be used alone");
            System.exit(1);
        }
        System.out.println("The itunes parser project can be used to parse a xml itunes files, find dead links and try to repair it.");
        System.out.println("The program have a required parameter : itunes.folder, which can be passed either by command line (-itune.folder /path/to/iTunes Media) " +
                "or in the application.properties files (itunes.folder=/path/to/iTunes Media).");

        System.exit(0);
    }

}
