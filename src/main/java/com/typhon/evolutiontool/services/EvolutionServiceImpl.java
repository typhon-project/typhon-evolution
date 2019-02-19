package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.Entity;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class EvolutionServiceImpl implements EvolutionService{


    Logger logger = LoggerFactory.getLogger(EvolutionServiceImpl.class);
    @Autowired
    private TyphonDLConnector typhonDLConnection;
    private TyphonQLGenerator typhonQLGenerator;


    @Override
    public String addEntity(SMO smo) throws InputParameterException {
        Entity newEntity;
        String databasetype, databasename;
        // Verify InputParameters
        if(smo.inputParametersContainsExpected(Arrays.asList("entity","targetmodel"))){
            databasetype = smo.getInputParameter().get("databasetype").toString();
            databasename = smo.getInputParameter().get("databasename").toString();
            // Verify that an instance of the underlying database is running in the TyphonDL.
            if (!typhonDLConnection.isDatabaseRunning(databasetype, databasename)) {
                typhonDLConnection.createDatabase(databasetype, databasename);
            }
            newEntity = smo.getPOJOFromInputParameter("entity", Entity.class);
            typhonQLGenerator.createEntity(newEntity);
            return "entity created";
        }
        else
            throw new InputParameterException("Missing parameter");

    }

    @Override
    public String renameEntity(SMO smo) {
        return null;
    }
}
