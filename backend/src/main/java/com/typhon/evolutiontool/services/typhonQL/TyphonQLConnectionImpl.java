package com.typhon.evolutiontool.services.typhonQL;

import com.typhon.evolutiontool.dummy.WorkingSetDummyImpl;
import com.typhon.evolutiontool.entities.WorkingSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import typhonml.ChangeOperator;
import typhonml.Model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
//import nl.cwi.swat.typhonql.Engine
//import nl.cwi.swat.typhonql.WorkingSet


//TODO WorkingSet class should be the class from TyphonQL
public class TyphonQLConnectionImpl implements TyphonQLConnection {

    private Path outPath;
    //    private Engine engine;
    private Model schema;
    private Logger logger = LoggerFactory.getLogger(TyphonQLConnectionImpl.class);

    public TyphonQLConnectionImpl() {
        //TODO remove this temporary method
        outPath = Paths.get("resources/TyphonQL_queries.txt");
    }

    //TODO use this constructor when the Engine interface is available from QL (should be given as a parameter of EvolutionTool.evolve(...) method)
//    public TyphonQLConnectionImpl(Engine engine) {
//        this.engine = engine;
//    }

    //TODO remove this temporary method
    @Override
    public String executeTyphonQLDDL(String tqlDDL) {
        logger.info("Executing TyphonQL DDL [{}] \n on TyphonML", tqlDDL);
        writeToFile(tqlDDL);
        return tqlDDL;
    }

    @Override
    public void setSchema(Model schema) {
        this.schema = schema;
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

    public Model getSchema() {
        return schema;
    }
}
