package com.bparent.itunes.configuration;

import java.io.IOException;
import java.util.Properties;

public class ApplicationProperties {
    private static final ApplicationProperties instance = new ApplicationProperties();
    private final Properties properties;

    private ApplicationProperties() {
        properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static String get(String property) {
        return instance.properties.getProperty(property);
    }
}
