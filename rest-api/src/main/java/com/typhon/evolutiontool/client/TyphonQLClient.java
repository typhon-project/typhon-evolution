package com.typhon.evolutiontool.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import typhonml.Model;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Base64;

public class TyphonQLClient {

    private static final String authStringEnc = Base64.getEncoder().encodeToString(("admin:admin1@").getBytes());
    private static final Client restClient = Client.create();

    private static final String LOCALHOST_URL = "http://localhost:8080/";
    private static final String H2020_URL = "http://h2020.info.fundp.ac.be:8080/";
    private static final String RESET_DATABASES_URL = "api/resetdatabases";
    private static final String GET_USERS_URL = "users";
    private static final String QUERY_URL = "api/query";
    private static final String UPDATE_URL = "api/update";
    private static final String GET_ML_MODEL_URL = "api/model/ml/";
    private static final String GET_ML_MODELS_URL = "api/models/ml";

    private static final String RESET_DATABASES_LOCALHOST_URL = LOCALHOST_URL + RESET_DATABASES_URL;
    private static final String GET_USERS_LOCALHOST_URL = LOCALHOST_URL + GET_USERS_URL;
    private static final String QUERY_LOCALHOST_URL = LOCALHOST_URL + QUERY_URL;
    private static final String UPDATE_DATABASES_LOCALHOST_URL = LOCALHOST_URL + UPDATE_URL;
    private static final String GET_ML_MODEL_LOCALHOST_URL = LOCALHOST_URL + GET_ML_MODEL_URL;
    private static final String GET_ML_MODELS_LOCALHOST_URL = LOCALHOST_URL + GET_ML_MODELS_URL;
    private static final String RESET_DATABASES_H2020_URL = H2020_URL + RESET_DATABASES_URL;
    private static final String GET_USERS_H2020_URL = H2020_URL + GET_USERS_URL;
    private static final String QUERY_H2020_URL = H2020_URL + QUERY_URL;
    private static final String UPDATE_DATABASES_H2020_URL = H2020_URL + UPDATE_URL;
    private static final String GET_ML_MODEL_H2020_URL = H2020_URL + GET_ML_MODEL_URL;
    private static final String GET_ML_MODELS_H2020_URL = H2020_URL + GET_ML_MODELS_URL;

    public static void main(String[] args) {
        System.out.println("Base64 encoded auth string: " + authStringEnc);
        //Reset databases in the polystore
//        resetDatabases(RESET_DATABASES_LOCALHOST_URL);
//        resetDatabases(RESET_DATABASES_H2020_URL);
        //Get polystore users
//        getUsers(GET_USERS_LOCALHOST_URL);
//        getUsers(GET_USERS_H2020_URL);
        //Get all instances of "User" entity
//        select(QUERY_LOCALHOST_URL);
//        select(GET_USERS_H2020_URL);
        //Insert an instance of "User" entity
//        insert(UPDATE_DATABASES_LOCALHOST_URL);
//        insert(UPDATE_DATABASES_H2020_URL);
        //Create a new entity "TestRelational" into the relational database
//        create(UPDATE_DATABASES_LOCALHOST_URL);
//        create(UPDATE_DATABASES_H2020_URL);
        //Drop "TestRelational" entity from the relational database
//        delete(UPDATE_DATABASES_LOCALHOST_URL);
//        delete(UPDATE_DATABASES_H2020_URL);
//        getModel(GET_ML_MODEL_LOCALHOST_URL, 1);
//        getModel(GET_ML_MODEL_H2020_URL, 1);
        getModels(GET_ML_MODELS_LOCALHOST_URL);
//        getModel(GET_ML_MODEL_H2020_URL, 1);
    }

    private static WebResource createWebResource(String url) {
        return restClient.resource(url);
    }

    private static void resetDatabases(String url) {
        WebResource webResource = createWebResource(url);
        ClientResponse resp = webResource.accept("application/json").header("Authorization", "Basic " + authStringEnc)
                .get(ClientResponse.class);
        if (resp.getStatus() != 200) {
            System.err.println("Unable to connect to the server");
        }
        String output = resp.getEntity(String.class);
        System.out.println("response: " + output);
    }

    private static void getUsers(String url) {
        WebResource webResource = createWebResource(url);
        ClientResponse resp = webResource.accept("application/json").header("Authorization", "Basic " + authStringEnc)
                .get(ClientResponse.class);
        if (resp.getStatus() != 200) {
            System.err.println("Unable to connect to the server");
        }
        String output = resp.getEntity(String.class);
        System.out.println("response: " + output);
    }

    private static void select(String url) {
        try {
            WebResource webResource = createWebResource(url);
            String input = "from User u select u";
            ClientResponse resp = webResource.accept("application/json").header("Authorization", "Basic " + authStringEnc)
                    .post(ClientResponse.class, input);
            if (resp.getStatus() != 200) {
                System.err.println("Unable to connect to the server");
            }
            System.out.println("Output from Server .... \n");
            String output = resp.getEntity(String.class);
            System.out.println(output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void insert(String url) {
        try {
            WebResource webResource = createWebResource(url);
            String input = "insert User {id: 2, name: \"test2\", surname: \"test2\" }";
            ClientResponse resp = webResource.accept("application/json").header("Authorization", "Basic " + authStringEnc)
                    .post(ClientResponse.class, input);
            if (resp.getStatus() != 200) {
                System.err.println("Unable to connect to the server");
            }
            System.out.println("Output from Server .... \n");
            String output = resp.getEntity(String.class);
            System.out.println(output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void create(String url) {
        try {
            WebResource webResource = createWebResource(url);
            String input = "create TestRelational at RelationalDatabase";
            ClientResponse resp = webResource.accept("application/json").header("Authorization", "Basic " + authStringEnc)
                    .post(ClientResponse.class, input);
            if (resp.getStatus() != 200) {
                System.err.println("Unable to connect to the server");
            }
            System.out.println("Output from Server .... \n");
            String output = resp.getEntity(String.class);
            System.out.println(output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void delete(String url) {
        try {
            WebResource webResource = createWebResource(url);
            String input = "drop TestRelational";
            ClientResponse resp = webResource.accept("application/json").header("Authorization", "Basic " + authStringEnc)
                    .post(ClientResponse.class, input);
            if (resp.getStatus() != 200) {
                System.err.println("Unable to connect to the server");
            }
            System.out.println("Output from Server .... \n");
            String output = resp.getEntity(String.class);
            System.out.println(output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getModel(String url, int typhonMLModelVersion) {
        WebResource webResource = createWebResource(url + typhonMLModelVersion);
        ClientResponse resp = webResource.accept("application/octet-stream").header("Authorization", "Basic " + authStringEnc)
                .get(ClientResponse.class);
        if (resp.getStatus() != 200) {
            System.err.println("Unable to connect to the server");
        }
        String xmiString = resp.getEntity(String.class);
        System.out.println("response: " + xmiString);
        try (PrintWriter out = new PrintWriter("resources/xmi.xmi")) {
            out.println(xmiString);
        } catch (FileNotFoundException e) {
            System.err.println("Unable to write the content of the XMI file");
        }
    }

    private static void getModels(String url) {
        WebResource webResource = createWebResource(url);
        ClientResponse resp = webResource.accept("application/json").header("Authorization", "Basic " + authStringEnc)
                .get(ClientResponse.class);
        if (resp.getStatus() != 200) {
            System.err.println("Unable to connect to the server");
        }
        String xmiString = resp.getEntity(String.class);
        System.out.println("response: " + xmiString);
        try (PrintWriter out = new PrintWriter("xmis.xmi")) {
            out.println(xmiString);
        } catch (FileNotFoundException e) {
            System.err.println("Unable to write the content of the XMI file");
        }
    }
}