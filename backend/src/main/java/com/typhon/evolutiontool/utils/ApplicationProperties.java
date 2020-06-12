package com.typhon.evolutiontool.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class ApplicationProperties {

    private static ApplicationProperties instance = null;
    private Properties properties;

    protected ApplicationProperties() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource("configuration/application.properties");
        if (resource == null) {
            throw new IllegalArgumentException("Application configuration file not found");
        } else {
            InputStream file = new FileInputStream(new File(resource.getFile())) ;
            properties = new Properties();
            properties.load(file);
        }
    }

    public static ApplicationProperties getInstance() {
        if (instance == null) {
            try {
                instance = new ApplicationProperties();
            } catch (IOException ioException) {
                System.out.println("Failed to load application properties file. Verify if 'application.properties' file exists");
                System.out.println(ioException.getMessage());
            }
        }
        return instance;
    }

    public String getValue(String key) {
        return properties.getProperty(key);
    }
}