package com.typhon.evolutiontool.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

public class ApplicationProperties {

    private static ApplicationProperties instance = null;
    private Properties properties;

    public static final String ENV_VAR_WEBSERVICE_URL_KEY = "WEBSERVICE_URL";
    public static final String ENV_VAR_WEBSERVICE_USERNAME_KEY = "WEBSERVICE_USERNAME";
    public static final String ENV_VAR_WEBSERVICE_PASSWORD_KEY = "WEBSERVICE_PASSWORD";
    public static final String DEFAULT_PROPERTY_POLYSTORE_API_URL_KEY = "POLYSTORE_API_URL";
    public static final String DEFAULT_PROPERTY_POLYSTORE_API_CREDENTIALS_KEY = "POLYSTORE_API_USER_PASSWORD";
    public static final String DEFAULT_PROPERTY_POLYSTORE_API_RESET_DATABASES_URL_KEY = "API_RESET_DATABASES_URL";
    public static final String DEFAULT_PROPERTY_POLYSTORE_API_GET_USERS_URL_KEY = "API_GET_USERS_URL";
    public static final String DEFAULT_PROPERTY_POLYSTORE_API_QUERY_URL_KEY = "API_QUERY_URL";
    public static final String DEFAULT_PROPERTY_POLYSTORE_API_UPDATE_URL_KEY = "API_UPDATE_URL";
    public static final String DEFAULT_PROPERTY_POLYSTORE_API_GET_ML_MODEL_URL_KEY = "API_GET_ML_MODEL_URL";
    public static final String DEFAULT_PROPERTY_POLYSTORE_API_GET_ML_MODELS_URL_KEY = "API_GET_ML_MODELS_URL";
    public static final String DEFAULT_PROPERTY_POLYSTORE_API_UPLOAD_ML_MODEL_URL_KEY = "API_UPLOAD_ML_MODEL_URL";
    public static final String DEFAULT_PROPERTY_POLYSTORE_API_URL_VALUE = "http://localhost:8080/";
    public static final String DEFAULT_PROPERTY_POLYSTORE_API_CREDENTIALS_VALUE = "admin:admin1@";
    public static final String DEFAULT_PROPERTY_POLYSTORE_API_RESET_DATABASES_URL_VALUE = "api/resetdatabases";
    public static final String DEFAULT_PROPERTY_POLYSTORE_API_GET_USERS_URL_VALUE = "users";
    public static final String DEFAULT_PROPERTY_POLYSTORE_API_QUERY_URL_VALUE = "api/query";
    public static final String DEFAULT_PROPERTY_POLYSTORE_API_UPDATE_URL_VALUE = "api/update";
    public static final String DEFAULT_PROPERTY_POLYSTORE_API_GET_ML_MODEL_URL_VALUE = "api/model/ml/";
    public static final String DEFAULT_PROPERTY_POLYSTORE_API_GET_ML_MODELS_URL_VALUE = "api/models/ml";
    public static final String DEFAULT_PROPERTY_POLYSTORE_API_UPLOAD_ML_MODEL_URL_VALUE = "api/model/ml";

    protected ApplicationProperties() throws Exception {
        //Check if environment variables are defined
        Map<String, String> env = System.getenv();
        if (env.containsKey(ENV_VAR_WEBSERVICE_URL_KEY) && env.containsKey(ENV_VAR_WEBSERVICE_USERNAME_KEY) && env.containsKey(ENV_VAR_WEBSERVICE_PASSWORD_KEY)) {
            System.out.println("Reading application properties from environment variables");
            System.out.println(DEFAULT_PROPERTY_POLYSTORE_API_URL_KEY + ": " + env.get(ENV_VAR_WEBSERVICE_URL_KEY));
            properties = new Properties();
            properties.setProperty(DEFAULT_PROPERTY_POLYSTORE_API_URL_KEY, env.get(ENV_VAR_WEBSERVICE_URL_KEY));
            properties.setProperty(DEFAULT_PROPERTY_POLYSTORE_API_CREDENTIALS_KEY, env.get(ENV_VAR_WEBSERVICE_USERNAME_KEY) + ":" + env.get(ENV_VAR_WEBSERVICE_PASSWORD_KEY));
            properties.put(DEFAULT_PROPERTY_POLYSTORE_API_URL_KEY, DEFAULT_PROPERTY_POLYSTORE_API_URL_VALUE);
            properties.put(DEFAULT_PROPERTY_POLYSTORE_API_CREDENTIALS_KEY, DEFAULT_PROPERTY_POLYSTORE_API_CREDENTIALS_VALUE);
            properties.put(DEFAULT_PROPERTY_POLYSTORE_API_RESET_DATABASES_URL_KEY, DEFAULT_PROPERTY_POLYSTORE_API_RESET_DATABASES_URL_VALUE);
            properties.put(DEFAULT_PROPERTY_POLYSTORE_API_GET_USERS_URL_KEY, DEFAULT_PROPERTY_POLYSTORE_API_GET_USERS_URL_VALUE);
            properties.put(DEFAULT_PROPERTY_POLYSTORE_API_QUERY_URL_KEY, DEFAULT_PROPERTY_POLYSTORE_API_QUERY_URL_VALUE);
            properties.put(DEFAULT_PROPERTY_POLYSTORE_API_UPDATE_URL_KEY, DEFAULT_PROPERTY_POLYSTORE_API_UPDATE_URL_VALUE);
            properties.put(DEFAULT_PROPERTY_POLYSTORE_API_GET_ML_MODEL_URL_KEY, DEFAULT_PROPERTY_POLYSTORE_API_GET_ML_MODEL_URL_VALUE);
            properties.put(DEFAULT_PROPERTY_POLYSTORE_API_GET_ML_MODELS_URL_KEY, DEFAULT_PROPERTY_POLYSTORE_API_GET_ML_MODELS_URL_VALUE);
            properties.put(DEFAULT_PROPERTY_POLYSTORE_API_UPLOAD_ML_MODEL_URL_KEY, DEFAULT_PROPERTY_POLYSTORE_API_UPLOAD_ML_MODEL_URL_VALUE);
        } else {
            //If not, check if the application.properties file exists next to the jar file
            File applicationPropertiesFile = new File("./application.properties");
            if (applicationPropertiesFile.exists()) {
                System.out.println("Reading application properties from application.properties file");
                InputStream file = new FileInputStream(applicationPropertiesFile);
                properties = new Properties();
                properties.load(file);
            } else {
                //Else, take default application.properties file
                ClassLoader classLoader = getClass().getClassLoader();
                URL resource = classLoader.getResource("configuration/application.properties");
                if (resource == null) {
                    throw new Exception("Impossible to find the application properties. Check the user manual.");
                } else {
                    System.out.println("Reading application properties from default values");
                    InputStream file = new FileInputStream(new File(resource.getFile()));
                    properties = new Properties();
                    properties.load(file);
                }
            }
        }
    }

    public static ApplicationProperties getInstance() {
        if (instance == null) {
            try {
                instance = new ApplicationProperties();
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
        return instance;
    }

    public String getValue(String key) {
        return properties.getProperty(key);
    }
}
