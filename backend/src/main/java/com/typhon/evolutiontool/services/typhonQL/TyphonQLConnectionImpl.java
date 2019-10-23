package com.typhon.evolutiontool.services.typhonQL;

import com.typhon.evolutiontool.dummy.WorkingSetDummyImpl;
import com.typhon.evolutiontool.entities.WorkingSet;
import typhonml.Model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import static java.util.stream.Collectors.joining;


public class TyphonQLConnectionImpl implements TyphonQLConnection {

    Logger logger = LoggerFactory.getLogger(TyphonQLConnectionImpl.class);
    private Model schema;
    private Path outPath;

    public TyphonQLConnectionImpl(Model schema) {
        this.schema = schema;
        //TODO Remove this
        outPath = Paths.get("resources/TyphonQL_queries.txt");
    }

    @Override
    public String executeTyphonQLDDL(String tqlDDL) {
        logger.info("Executing TyphonQL DDL [{}] \n on TyphonML", tqlDDL);
        //TODO remove this
        writeToFile(tqlDDL);
        return tqlDDL;
    }


    @Override
    public WorkingSet query(String queryString, String... params) {
        WorkingSet ws = new WorkingSetDummyImpl();

//        logger.info(queryString + Arrays.stream(params).collect(joining(",")));
        logger.info(String.format(queryString, params));
        //TODO remove this
        writeToFile(String.format(queryString, params));
        return ws;
    }

    @Override
    public WorkingSet query(String queryString, Map<String, Object> params) {
        return null;
    }

    @Override
    public WorkingSet update(WorkingSet ws) {
        return null;
    }

    @Override
    public WorkingSet insert(WorkingSet ws) {
        logger.info("TyphonQL 'insert' command working set : [{}] ", ws);
        //TODO remove this
        writeToFile("TyphonQL 'insert' command working set : [{" + ws + "}] ");
        return null;
    }

    @Override
    public WorkingSet delete(WorkingSet ws) {
        logger.info("TyphonQL 'delete' command working set : [{}] ", ws);
        //TODO remove this
        writeToFile("TyphonQL 'delete' command working set : [{" + ws + "}] ");
        return null;
    }

    //TODO remove this
    private void writeToFile(String query) {
        byte[] strToBytes = query.concat("\n").getBytes();
        try {
            Files.write(outPath, strToBytes, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Model getSchema() {
        return schema;
    }

    public void setSchema(Model schema) {
        this.schema = schema;
    }
}
