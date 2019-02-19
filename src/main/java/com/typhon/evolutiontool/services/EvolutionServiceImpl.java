package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.Entity;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Arrays;
import java.util.List;

@Service
public class EvolutionServiceImpl implements EvolutionService{


    Logger logger = LoggerFactory.getLogger(EvolutionServiceImpl.class);
    @Autowired
    private TyphonDLConnector typhonDLConnection;
    @Autowired
    @Qualifier("typhonql")
    private TyphonInterface typhonInterface;


    @Override
    public String addEntity(SMO smo) throws InputParameterException {
        Entity newEntity;
        String databasetype, databasename;
        // Verify InputParameters
        if(containParameters(smo,Arrays.asList("entity","targetmodel","databasetype","databasename"))){
            typhonInterface.setTyphonMLTargetModel(smo.getInputParameter().get("targetmodel").toString());
            databasetype = smo.getInputParameter().get("databasetype").toString();
            databasename = smo.getInputParameter().get("databasename").toString();
            // Verify that an instance of the underlying database is running in the TyphonDL.
            if (!typhonDLConnection.isDatabaseRunning(databasetype, databasename)) {
                typhonDLConnection.createDatabase(databasetype, databasename);
            }
            newEntity = smo.getPOJOFromInputParameter("entity", Entity.class);
            typhonInterface.createEntity(newEntity);
            return "entity created";
        }
        else
            throw new InputParameterException("Missing parameter");

    }

    @Override
    public String renameEntity(SMO smo) throws InputParameterException {
        String oldEntityName,newEntityName, targetmodel;
        if (containParameters(smo, Arrays.asList("oldEntityName", "newEntityName", "targetmodel"))) {
            targetmodel = smo.getInputParameter().get("targetmodel").toString();
            oldEntityName = smo.getInputParameter().get("oldEntityName").toString();
            newEntityName = smo.getInputParameter().get("newEntityName").toString();
            typhonInterface.setTyphonMLTargetModel(targetmodel);
            typhonInterface.renameEntity(oldEntityName, newEntityName);
            return "entity renamed";
        } else {
            throw new InputParameterException("Missing parameter");
        }

    }

    private boolean containParameters(SMO smo, List<String> parameters) {
        logger.info("Verifying input parameter for [{}] - [{}] operator",smo.getTyphonObject(), smo.getEvolutionOperator());
        return smo.inputParametersContainsExpected(parameters);
    }
}
