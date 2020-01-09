package com.typhon.evolutiontool.client;

import com.typhon.evolutiontool.dummy.WorkingSetDummyImpl;
import com.typhon.evolutiontool.entities.WorkingSet;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import typhonml.Model;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Base64;

public class TyphonQLWebServiceClientImpl implements TyphonQLWebServiceClient {

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

    private Logger logger = LoggerFactory.getLogger(TyphonQLWebServiceClientImpl.class);

    @Override
    public void uploadModel(String schemaContent) {
        logger.info("Querying TyphonQL web service to upload a new version of the TyphonML model : {}", schemaContent);
        WebTarget webTarget = restClient.target(LOCALHOST_URL).path(UPLOAD_ML_MODEL_URL);
        String escapedDoubleQuotesContent = schemaContent.replaceAll("\"", "\\\\\"");
        String json = "{\"name\":\"newTyphonMLModel\",\"contents\":\"" + escapedDoubleQuotesContent + "\"}";
        Response response = webTarget
                .request()
                .header("Authorization", "Basic " + authStringEnc)
                .post(Entity.entity(json, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            logger.error("Error during the web service query call: {}", webTarget.getUri());
        }
        logger.info("Upload of ML model successful");
    }

    @Override
    public void resetDatabases() {
        logger.info("Querying TyphonQL web service to reset the polystore databases");
        WebTarget webTarget = restClient.target(LOCALHOST_URL).path(RESET_DATABASES_URL);
        String result = webTarget
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + authStringEnc)
                .get(String.class);
        System.out.println("Result: " + result);
        logger.info("Reset of polystore databases successful");
    }

    @Override
    public void getUsers() {
        logger.info("Querying TyphonQL web service to get the polystore users");
        WebTarget webTarget = restClient.target(LOCALHOST_URL).path(GET_USERS_URL);
        String result = webTarget
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + authStringEnc)
                .get(String.class);
        System.out.println("Result: " + result);
        logger.info("Get polytstore users successful");
    }

    /**
     * Select data from the polystore
     */
    @Override
    public WorkingSet query(String query) {
        logger.info("Querying TyphonQL web service to execute a DML query");
        WebTarget webTarget = restClient.target(LOCALHOST_URL).path(QUERY_URL);
//        String query = "from User u select u";
        Response response = webTarget
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + authStringEnc)
                .post(Entity.entity(query, MediaType.TEXT_PLAIN));
        if (response.getStatus() != 200) {
            System.err.println("Error during the web service query call: " + webTarget.getUri());
        }
        String result = response.readEntity(String.class);
        System.out.println("Result: " + result);
        logger.info("DML query for the polystore successful");
        return new WorkingSetDummyImpl();
    }

    /**
     * Create, rename, delete entity, relation or attribute
     */
    @Override
    public void update(String query) {
        logger.info("Querying TyphonQL web service to execute a DDL query");
        WebTarget webTarget = restClient.target(LOCALHOST_URL).path(UPDATE_URL);
//        String query = "insert User {id: 2, name: \"test2\", surname: \"test2\" }";
        Response response = webTarget
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + authStringEnc)
                .post(Entity.entity(query, MediaType.TEXT_PLAIN));
        if (response.getStatus() != 200) {
            System.err.println("Error during the web service query call: " + webTarget.getUri());
        }
        String result = response.readEntity(String.class);
        System.out.println("Result: " + result);
        logger.info("DDL query for the polystore successful");
    }

    /**
     * Retrieve a given version of the TyphonML model
     *
     * @param typhonMLModelVersion the version of the TyphonML model to retrieve; -1 for the latest version
     */
    @Override
    public Model getModel(Integer typhonMLModelVersion) {
        logger.info("Querying TyphonQL web service to get a given version of a TyphonML model");
        WebTarget webTarget = restClient.target(LOCALHOST_URL).path(GET_ML_MODEL_URL + (typhonMLModelVersion != null ? typhonMLModelVersion : -1));
        String result = webTarget
                .request(MediaType.APPLICATION_OCTET_STREAM)
                .header("Authorization", "Basic " + authStringEnc)
                .get(String.class);
        System.out.println("Result: " + result);
        try (PrintWriter out = new PrintWriter("xmi.xmi")) {
            out.println(result);
        } catch (FileNotFoundException e) {
            logger.error("Unable to write the content of the retrieved XMI file");
        }
        logger.info("Get TyphonML model successful");
        return TyphonMLUtils.loadModelTyphonML("xmi.xmi");
    }

    /**
     * Retrieve all versions of the TyphonML models
     */
    @Override
    public void getModels() {
        logger.info("Querying TyphonQL web service to get all the versions of the TyphonML models");
        WebTarget webTarget = restClient.target(LOCALHOST_URL).path(GET_ML_MODELS_URL);
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
        logger.info("Get TyphonML models successful");
    }
}
