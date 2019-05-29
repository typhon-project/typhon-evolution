package com.typhon.evolutiontool.services.typhonQL;

import com.typhon.evolutiontool.dummy.WorkingSetDummyImpl;
import com.typhon.evolutiontool.entities.TyphonMLSchema;
import com.typhon.evolutiontool.entities.WorkingSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.joining;


public class TyphonQLConnectionImpl implements TyphonQLConnection {

    Logger logger = LoggerFactory.getLogger(TyphonQLConnectionImpl.class);
    private TyphonMLSchema schema;

    public TyphonQLConnectionImpl(TyphonMLSchema schema) {
        this.schema = schema;
    }

    @Override
    public String executeTyphonQLDDL(String tqlDDL) {
        logger.info("Executing TyphonQL DDL [{}] \n on TyphonML [{}]", tqlDDL, this.schema.getVersion());
        //TODO implement effective execution
        return tqlDDL;
    }

    @Override
    public WorkingSet query(String queryString, String... params) {
        WorkingSet ws = new WorkingSetDummyImpl();
        //TODO implement real connection
        logger.info(queryString + Arrays.stream(params).collect(joining(",")));
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
        return null;
    }

    @Override
    public WorkingSet delete(WorkingSet ws) {
        logger.info("TyphonQL 'delete' command working set : [{}] ", ws);
        return null;
    }

    public TyphonMLSchema getSchema() {
        return schema;
    }

    public void setSchema(TyphonMLSchema schema) {
        this.schema = schema;
    }
}
