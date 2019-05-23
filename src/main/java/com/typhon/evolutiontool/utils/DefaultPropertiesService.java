package com.typhon.evolutiontool.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Admin on 21-08-17.
 */
@Component
public class DefaultPropertiesService implements PropertiesService {


    private static Logger logger = LoggerFactory.getLogger(DefaultPropertiesService.class);
    //TODO Changer ce chemin absolu
    public static final String CONFIGURATION_PATH_SYSTEM_PROPERTY_KEY = "C:\\Users\\Admin\\Documents\\IdeaProjects\\typhon\\src\\main\\resources\\static\\evolutiontool.properties";

    private static Properties loadFromPath(final String configurationPath)
            throws IOException, FileNotFoundException {
        final Properties properties = new Properties();

        try (final FileInputStream configurationStream = new FileInputStream(configurationPath)) {
            properties.load(new FileInputStream(configurationPath));
            logger.info("Property file loaded");
        }

        return properties;
    }

    private static String readConfigurationPath(final String configurationPathSystemPropertyKey) {
        final String configurationPath = System.getProperty(configurationPathSystemPropertyKey);
        if (configurationPath == null) {
            throw new IllegalArgumentException(
                    String.format("System property %s not provided!", configurationPathSystemPropertyKey));
        }
        return configurationPath;
    }

    private final Properties properties;

    public DefaultPropertiesService() throws FileNotFoundException, IOException {
        this(CONFIGURATION_PATH_SYSTEM_PROPERTY_KEY);
    }

    public DefaultPropertiesService(final Properties properties) {
        this.properties = properties;
    }

    public DefaultPropertiesService(final String configurationPathSystemPropertyKey)
            throws FileNotFoundException, IOException {
//        this(loadFromPath(readConfigurationPath(configurationPathSystemPropertyKey)));
        this(loadFromPath(configurationPathSystemPropertyKey));
    }

    @Override
    public Properties get() {
        return this.properties;
    }
}
