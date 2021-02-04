package com.typhon.evolutiontool.client;

import com.typhon.evolutiontool.utils.ApplicationProperties;
import it.univaq.disim.typhon.acceleo.services.Services;
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

    private static final String POLYSTORE_API_URL = "POLYSTORE_API_URL";
    private static final String POLYSTORE_API_USER_PASSWORD = "POLYSTORE_API_USER_PASSWORD";
    private static final String API_RESET_DATABASES_URL = "API_RESET_DATABASES_URL";
    private static final String API_GET_USERS_URL = "API_GET_USERS_URL";
    private static final String API_QUERY_URL = "API_QUERY_URL";
    private static final String API_UPDATE_URL = "API_UPDATE_URL";
    private static final String API_GET_ML_MODEL_URL = "API_GET_ML_MODEL_URL";
    private static final String API_GET_ML_MODELS_URL = "API_GET_ML_MODELS_URL";
    private static final String API_UPLOAD_ML_MODEL_URL = "API_UPLOAD_ML_MODEL_URL";

    private static final JerseyClient restClient = JerseyClientBuilder.createClient();

    private ApplicationProperties applicationProperties;
    private Logger logger = LoggerFactory.getLogger(TyphonQLWebServiceClientImpl.class);

    public TyphonQLWebServiceClientImpl() {
        this.applicationProperties = ApplicationProperties.getInstance();
    }

    @Override
    public void uploadModel(String schemaContent) {
        logger.info("Querying TyphonQL web service to upload a new version of the TyphonML model");
        logger.debug("New TyphonML model : {}", schemaContent);
        WebTarget webTarget = restClient.target(applicationProperties.getValue(POLYSTORE_API_URL)).path(applicationProperties.getValue(API_UPLOAD_ML_MODEL_URL));
        String escapedDoubleQuotesContent = schemaContent.replaceAll("\"", "\\\\\"");
        String json = "{\"name\":\"newTyphonMLModel\",\"contents\":\"" + escapedDoubleQuotesContent + "\"}";
        Response response = webTarget
                .request()
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((applicationProperties.getValue(POLYSTORE_API_USER_PASSWORD)).getBytes()))
                .post(Entity.entity(json, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            logger.error("Error during the web service query call: {}", webTarget.getUri());
        }
        logger.info("Upload of ML model successful");
    }

    @Override
    public void resetDatabases() {
        logger.info("Querying TyphonQL web service to reset the polystore databases");
        WebTarget webTarget = restClient.target(applicationProperties.getValue(POLYSTORE_API_URL)).path(applicationProperties.getValue(API_RESET_DATABASES_URL));
        Boolean result = webTarget
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((applicationProperties.getValue(POLYSTORE_API_USER_PASSWORD)).getBytes()))
                .get(Boolean.class);
        logger.info(applicationProperties.getValue(API_RESET_DATABASES_URL) + " result: " + result);
        logger.info("Reset of polystore databases successful");
    }

    @Override
    public void getUsers() {
        logger.info("Querying TyphonQL web service to get the polystore users");
        WebTarget webTarget = restClient.target(applicationProperties.getValue(POLYSTORE_API_URL)).path(applicationProperties.getValue(API_GET_USERS_URL));
        String result = webTarget
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((applicationProperties.getValue(POLYSTORE_API_USER_PASSWORD)).getBytes()))
                .get(String.class);
        logger.info(applicationProperties.getValue(API_GET_USERS_URL) + " result: " + result);
        logger.info("Get polytstore users successful");
    }

    /**
     * Select data from the polystore
     */
    @Override
    public String query(String query) {
        logger.info("Querying TyphonQL web service to execute the query: {}", query);
        WebTarget webTarget = restClient.target(applicationProperties.getValue(POLYSTORE_API_URL)).path(applicationProperties.getValue(API_QUERY_URL));
//        String query = "from User u select u";
        Response response = webTarget
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((applicationProperties.getValue(POLYSTORE_API_USER_PASSWORD)).getBytes()))
                .post(Entity.entity(query, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            logger.error("Error during the web service query call: {}", webTarget.getUri());
        }
        String result = response.readEntity(String.class);
        logger.debug(applicationProperties.getValue(API_QUERY_URL) + " result: " + result);
        logger.info("Query executed successfully");
        return result;
    }

    /**
     * Create, rename, delete entity, relation or attribute
     */
    @Override
    public void update(String query) {
        logger.info("Querying TyphonQL web service to execute the update query: {}", query != null ? query.length() <= 150 ? query : query.substring(0, 149) + " ..." : "");
        WebTarget webTarget = restClient.target(applicationProperties.getValue(POLYSTORE_API_URL)).path(applicationProperties.getValue(API_UPDATE_URL));
        Response response = webTarget
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((applicationProperties.getValue(POLYSTORE_API_USER_PASSWORD)).getBytes()))
                .post(Entity.entity(query, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            logger.error("Error during the web service query call: {}", webTarget.getUri());
        }
        String result = response.readEntity(String.class);
        logger.debug(applicationProperties.getValue(API_UPDATE_URL) + " result: " + result);
        logger.info("Update query successfully executed");
    }

    /**
     * Retrieve a given version of the TyphonML model
     *
     * @param typhonMLModelVersion the version of the TyphonML model to retrieve; -1 for the latest version
     */
    @Override
    public Model getModel(Integer typhonMLModelVersion) {
        logger.info("Querying TyphonQL web service to get version '{}' of the TyphonML model", typhonMLModelVersion);
        WebTarget webTarget = restClient.target(applicationProperties.getValue(POLYSTORE_API_URL)).path(applicationProperties.getValue(API_GET_ML_MODEL_URL) + (typhonMLModelVersion != null ? typhonMLModelVersion : -1));
        String result = webTarget
                .request(MediaType.APPLICATION_OCTET_STREAM)
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((applicationProperties.getValue(POLYSTORE_API_USER_PASSWORD)).getBytes()))
                .get(String.class);
        logger.info(applicationProperties.getValue(API_GET_ML_MODEL_URL) + " result: " + result);
        try (PrintWriter out = new PrintWriter("xmi.xmi")) {
            out.println(result);
        } catch (FileNotFoundException e) {
            logger.error("Unable to write the content of the retrieved XMI file");
        }
        logger.info("Get TyphonML model successful");
        return Services.loadXtextModel("xmi.xmi");
    }

    /**
     * Retrieve all versions of the TyphonML models
     */
    @Override
    public void getModels() {
        logger.info("Querying TyphonQL web service to get all the versions of the TyphonML models");
        WebTarget webTarget = restClient.target(applicationProperties.getValue(POLYSTORE_API_URL)).path(applicationProperties.getValue(API_GET_ML_MODELS_URL));
        String result = webTarget
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((applicationProperties.getValue(POLYSTORE_API_USER_PASSWORD)).getBytes()))
                .get(String.class);
        logger.info(applicationProperties.getValue(API_GET_ML_MODELS_URL) + " result: " + result);
        try (PrintWriter out = new PrintWriter("xmis.xmi")) {
            out.println(result);
        } catch (FileNotFoundException e) {
            logger.error("Unable to write the content of the XMI file");
        }
        logger.info("Get TyphonML models successful");
    }
}
