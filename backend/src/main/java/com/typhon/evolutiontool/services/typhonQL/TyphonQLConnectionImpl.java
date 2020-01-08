package com.typhon.evolutiontool.services.typhonQL;

import com.typhon.evolutiontool.dummy.WorkingSetDummyImpl;
import com.typhon.evolutiontool.entities.WorkingSet;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import typhonml.ChangeOperator;
import typhonml.Model;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Base64;

public class TyphonQLConnectionImpl implements TyphonQLConnection {

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

    private Path outPath;
    private Logger logger = LoggerFactory.getLogger(TyphonQLConnectionImpl.class);

    TyphonQLConnectionImpl() {
        //TODO remove this temporary method
        outPath = Paths.get("resources/TyphonQL_queries.txt");
    }

    //TODO remove this temporary method
    @Override
    public String executeTyphonQLDDL(String tqlDDL) {
        logger.info("Executing TyphonQL DDL [{}] \n on TyphonML", tqlDDL);
        writeToFile(tqlDDL);
        return tqlDDL;
    }

    @Override
    public void uploadModel(String schemaContent) {
        logger.info("Querying TyphonQL upload ML model web service: {}", schemaContent);
        webTarget = webTarget.path(UPLOAD_ML_MODEL_URL);
        String escapedDoubleQuotesContent = schemaContent.replaceAll("\"", "\\\\\"");
        String json = "{\"name\":\"newTyphonMLModel\",\"contents\":\"" + escapedDoubleQuotesContent + "\"}";
        Response response = webTarget
                .request()
                .header("Authorization", "Basic " + authStringEnc)
                .post(javax.ws.rs.client.Entity.entity(json, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            logger.error("Error during the web service query call: {}", webTarget.getUri());
        }
        logger.info("Upload of ML model successful");
    }

    @Override
    public void evolve(ChangeOperator changeOperator) {
        //TODO use TyphonQL Engine to run the method
        //this.engine.evolve(changeOperator);
        //TODO temporary implementation to log the queries
        logger.info("TyphonQL 'evolve' command working set : [{}] ", changeOperator);
        writeToFile("TyphonQL 'evolve' command working set : [{" + changeOperator + "}] ");
    }

    @Override
    public void insert(WorkingSet workingSet) {
        //TODO use TyphonQL Engine to run the method
        //this.engine.insert(workingSet);
        //TODO temporary implementation to log the queries
        logger.info("TyphonQL 'insert' command working set : [{}] ", workingSet);
        writeToFile("TyphonQL 'insert' command working set : [{" + workingSet + "}] ");
    }

    @Override
    public void delete(WorkingSet workingSet) {
        //TODO use TyphonQL Engine to run the method
        //this.engine.delete(workingSet);
        //TODO temporary implementation to log the queries
        logger.info("TyphonQL 'delete' command working set : [{}] ", workingSet);
        writeToFile("TyphonQL 'delete' command working set : [{" + workingSet + "}] ");
    }

    @Override
    public void update(WorkingSet workingSet) {
        //TODO use TyphonQL Engine to run the method
        //this.engine.update(workingSet);
        //TODO temporary implementation to log the queries
        logger.info("TyphonQL 'update' command working set : [{}] ", workingSet);
        writeToFile("TyphonQL 'update' command working set : [{" + workingSet + "}] ");
    }

    @Override
    public void execute(String statement, Object... params) {
        //TODO use TyphonQL Engine to run the method
        //this.engine.execute(statement, params);
        //TODO temporary implementation to log the queries
        logger.info("TyphonQL 'execute' statement: '{}' with params: [{}] ", statement, params);
        writeToFile("TyphonQL 'execute' statement: '" + statement + "' with params: [" + Arrays.toString(params) + "] ");
    }

    @Override
    public WorkingSet query(String query, Object... params) {
        //TODO use TyphonQL Engine to run the method
        //this.engine.query(query, params);
        //TODO temporary implementation to log the queries
        logger.info(String.format(query, params));
        WorkingSet ws = new WorkingSetDummyImpl();
        writeToFile(String.format(query, params));
        return ws;
    }

    //TODO remove this temporary method
    private void writeToFile(String query) {
        byte[] strToBytes = query.concat("\n").getBytes();
        try {
            if (!Files.exists(outPath)) {
                Files.createFile(outPath);
            }
            Files.write(outPath, strToBytes, StandardOpenOption.APPEND);
        } catch (IOException e) {
            logger.error("IOException while writing the query to the output file.\nException is:\n{}", e.getMessage());
        }
    }
}
