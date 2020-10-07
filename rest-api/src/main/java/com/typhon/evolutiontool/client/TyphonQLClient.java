package com.typhon.evolutiontool.client;

import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Base64;

public class TyphonQLClient {

    private static final String LOCALHOST_URL = "http://localhost:8080/";
    private static final String H2020_URL = "http://h2020.info.fundp.ac.be:8080/";
    private static final String RESET_DATABASES_URL = "api/resetdatabases";
    private static final String GET_USERS_URL = "users";
    private static final String QUERY_URL = "api/query";
    private static final String UPDATE_URL = "api/update";
    private static final String GET_ML_MODEL_URL = "api/model/ml/";
    private static final String GET_ML_MODELS_URL = "api/models/ml";
    private static final String UPLOAD_ML_MODEL_URL = "api/model/ml";

    private static final String authStringEnc = Base64.getEncoder().encodeToString(("admin:admin1@").getBytes());
    private static final JerseyClient restClient = JerseyClientBuilder.createClient();
    private static WebTarget webTarget = restClient.target(LOCALHOST_URL);

    public static void main(String[] args) {
        System.out.println("Base64 encoded auth string: " + authStringEnc);
        //Reset databases in the polystore
//        resetDatabases();
        //Get polystore users
//        getUsers();
        //Get all instances of "User" entity
        select();
        //Update instances of "User" entity
//        update();
        //Insert an instance of "User" entity
//        insert();
        //Create a new entity "TestRelational" into the Inventory relational database
//        create();
        //Create a new attribute "id" for "Word" entity
//        createAttribute();
        //Drop an attribute "id" for "Word" entity
//        dropAttribute();
        //Create a new relation "relationalToDocument" in "TestRelational" entity to "TestDocument" entity
//        createRelation();
        //Drop "TestRelational" entity from the relational database
//        delete();
        //Get first uploaded TyphonML model
//        getModel(1);
        //Get all uploaded TyphonML models
//        getModels();
        //Upload a TyphonML model
        //
    }

    private static void resetDatabases() {
        webTarget = webTarget.path(RESET_DATABASES_URL);
        String result = webTarget
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + authStringEnc)
                .get(String.class);
        System.out.println("Result: " + result);
    }

    private static void getUsers() {
        webTarget = webTarget.path(GET_USERS_URL);
        String result = webTarget
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + authStringEnc)
                .get(String.class);
        System.out.println("Result: " + result);
    }

    private static void select() {
//        String query = "from User user select user.@id, user.address where user.@id == #b4628b35-1a8f-483d-83ef-74d02969f9ca";
        String query = "from Word w select w.@id, w.name";
        webTarget = webTarget.path(QUERY_URL);
        Response response = webTarget
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + authStringEnc)
                .post(Entity.entity(query, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            System.err.println("Error during the web service query call: " + webTarget.getUri());
        }
        String result = response.readEntity(String.class);
        System.out.println("Result: " + result);
    }

    private static void update() {
//        String query = "update Word w where w.name == \"updated word 2\" set {name: \"updated word\"}";
        String query = "update Word w where w.@id == #4bb512eb-8f90-46cc-9c06-1c900b35de04 set {name: \"updated word 2\"}";
//        String query = "update Word w where w.name == \"updated word\" set {name: \"updated word 2\"}";
        webTarget = webTarget.path(UPDATE_URL);
        Response response = webTarget
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + authStringEnc)
                .post(Entity.entity(query, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            System.err.println("Error during the web service query call: " + webTarget.getUri());
        }
        String result = response.readEntity(String.class);
        System.out.println("Result: " + result);
    }

    private static void insert() {
//        String query = "insert User {id: 1, name: \"Loup\"}";
//        String query = "insert User {id: 2, name: \"Pol\" }";
//        String query = "insert Address {zip: \"10\", city: \"adr1\", streetNumber: \"100\", streetName: \"street1\", country: \"country1\"}";
//        String query = "insert Address {zip: \"20\", city: \"adr2\", streetNumber: \"200\", streetName: \"stree2\", country: \"country2\"}";
        String query = "insert Word {name: \"new word\"}";

        webTarget = webTarget.path(UPDATE_URL);
        Response response = webTarget
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + authStringEnc)
                .post(Entity.entity(query, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            System.err.println("Error during the web service query call: " + webTarget.getUri());
        }
        String result = response.readEntity(String.class);
        System.out.println("Result: " + result);
    }

    private static void create() {
        String query = "create TestRelational at Inventory";
        webTarget = webTarget.path(UPDATE_URL);
        Response response = webTarget
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + authStringEnc)
                .post(Entity.entity(query, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            System.err.println("Error during the web service query call: " + webTarget.getUri());
        }
        String result = response.readEntity(String.class);
        System.out.println("Result: " + result);
    }

    private static void createAttribute() {
        String query = "create Word.id : string(32)";
        webTarget = webTarget.path(UPDATE_URL);
        Response response = webTarget
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + authStringEnc)
                .post(Entity.entity(query, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            System.err.println("Error during the web service query call: " + webTarget.getUri());
        }
        String result = response.readEntity(String.class);
        System.out.println("Result: " + result);
    }

    private static void dropAttribute() {
        String query = "drop attribute Word.id";
        webTarget = webTarget.path(UPDATE_URL);
        Response response = webTarget
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + authStringEnc)
                .post(Entity.entity(query, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            System.err.println("Error during the web service query call: " + webTarget.getUri());
        }
        String result = response.readEntity(String.class);
        System.out.println("Result: " + result);
    }

    private static void createRelation() {
        String query = "create TestRelational.relationalToDocument -> TestDocument[1..1]";
//        String query = "create TestDocument.documentToRelational -> TestRelational[1..1]";
        webTarget = webTarget.path(UPDATE_URL);
        Response response = webTarget
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + authStringEnc)
                .post(Entity.entity(query, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            System.err.println("Error during the web service query call: " + webTarget.getUri());
        }
        String result = response.readEntity(String.class);
        System.out.println("Result: " + result);
    }

    private static void delete() {
        String query = "drop TestRelational";
        webTarget = webTarget.path(UPDATE_URL);
        Response response = webTarget
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + authStringEnc)
                .post(Entity.entity(query, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            System.err.println("Error during the web service query call: " + webTarget.getUri());
        }
        String result = response.readEntity(String.class);
        System.out.println("Result: " + result);
    }

    private static void getModel(int typhonMLModelVersion) {
        webTarget = webTarget.path(GET_ML_MODEL_URL + typhonMLModelVersion);
        String result = webTarget
                .request(MediaType.APPLICATION_OCTET_STREAM)
                .header("Authorization", "Basic " + authStringEnc)
                .get(String.class);
        System.out.println("Result: " + result);
        try (PrintWriter out = new PrintWriter("xmi.xmi")) {
            out.println(result);
        } catch (FileNotFoundException e) {
            System.err.println("Unable to write the content of the XMI file");
        }
    }

    private static void getModels() {
        webTarget = webTarget.path(GET_ML_MODELS_URL);
        String result = webTarget
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + authStringEnc)
                .get(String.class);
        System.out.println("Result: " + result);
        try (PrintWriter out = new PrintWriter("xmis.xmi")) {
            out.println(result);
        } catch (FileNotFoundException e) {
            System.err.println("Unable to write the content of the XMI file");
        }
    }

    private static void uploadModel() {
        String stringXMI = "<?xml version=\"1.0\" encoding=\"ASCII\"?><typhonml:Model xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:typhonml=\"http://org.typhon.dsls.typhonml.sirius\">  <databases xsi:type=\"typhonml:RelationalDB\" name=\"RelationalDatabase\">    <tables name=\"UserDB\" entity=\"//@dataTypes.2\">      <indexSpec name=\"userIndex\" attributes=\"//@dataTypes.2/@attributes.1\"/>      <idSpec attributes=\"//@dataTypes.2/@attributes.1\"/>    </tables>    <tables name=\"CreditCardDB\" entity=\"//@dataTypes.3\">      <indexSpec name=\"creditCardIndex\" attributes=\"//@dataTypes.3/@attributes.1\"/>      <idSpec attributes=\"//@dataTypes.3/@attributes.1\"/>    </tables>  </databases>  <databases xsi:type=\"typhonml:DocumentDB\" name=\"DocumentDatabase\">    <collections name=\"CommentDB\" entity=\"//@dataTypes.4\"/>  </databases>  <dataTypes xsi:type=\"typhonml:PrimitiveDataType\" name=\"Date\"/>  <dataTypes xsi:type=\"typhonml:PrimitiveDataType\" name=\"String\"/>  <dataTypes xsi:type=\"typhonml:Entity\" name=\"User\">    <attributes name=\"id\" type=\"//@dataTypes.1\"/>    <attributes name=\"name\" type=\"//@dataTypes.1\"/>    <relations name=\"paymentsDetails\" type=\"//@dataTypes.3\" cardinality=\"zero_many\" isContainment=\"true\"/>  </dataTypes>  <dataTypes xsi:type=\"typhonml:Entity\" name=\"CreditCard\">    <attributes name=\"id\" type=\"//@dataTypes.1\"/>    <attributes name=\"number\" type=\"//@dataTypes.1\"/>    <attributes name=\"expiryDate\" type=\"//@dataTypes.0\"/>  </dataTypes>  <dataTypes xsi:type=\"typhonml:Entity\" name=\"Comment_migrated\">    <attributes name=\"name\" type=\"//@dataTypes.1\"/>    <attributes name=\"id\" type=\"//@dataTypes.1\"/>    <relations name=\"responses\" type=\"//@dataTypes.4\" cardinality=\"zero_many\" isContainment=\"false\"/>  </dataTypes></typhonml:Model>";
        String escapedDoubleQuotesXMI = stringXMI.replaceAll("\"", "\\\\\"");
        String jsonXMI = "{\"name\":\"newTyphonMLModel\",\"contents\":\"" + escapedDoubleQuotesXMI + "\"}";
        webTarget = webTarget.path(UPLOAD_ML_MODEL_URL);
        Response response = webTarget
                .request()
                .header("Authorization", "Basic " + authStringEnc)
                .post(Entity.entity(jsonXMI, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            System.err.println("Error during the web service query call: " + webTarget.getUri());
        }
        System.out.println("Result: " + response);
    }
}
