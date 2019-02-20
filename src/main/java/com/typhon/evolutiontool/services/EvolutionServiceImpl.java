package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.Entity;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/*
    This class implements the operations needed to complete the execution of a Schema Modification Operator (SMO).
    For each operator it will :
     1. Verify that the needed parameters are in the input parameter map of the SMO object.
     2. Execute the structure and data change operations of the SMO operator by calling the TyphonInterface object implementation
     3. Informe the TyphonML module that the operator is executed an that the current running TyphonML model can be changed.
 */
@Service
public class EvolutionServiceImpl implements EvolutionService{


    Logger logger = LoggerFactory.getLogger(EvolutionServiceImpl.class);
    @Autowired
    private TyphonDLConnector typhonDLConnection;
    @Autowired
    @Qualifier("typhonql")
    private TyphonInterface typhonInterface;
    @Autowired
    private TyphonMLInterface typhonMLInterface;


    @Override
    public String addEntity(SMO smo) throws InputParameterException {
        Entity newEntity;
        String databasetype, databasename, targetmodelid;
        // Verify InputParameters
        if(containParameters(smo,Arrays.asList("entity","targetmodel","databasetype","databasename"))){
            targetmodelid = smo.getInputParameter().get("targetmodel").toString();
            typhonInterface.setTyphonMLTargetModel(targetmodelid);
            databasetype = smo.getInputParameter().get("databasetype").toString();
            databasename = smo.getInputParameter().get("databasename").toString();
            // Verify that an instance of the underlying database is running in the TyphonDL.
            if (!typhonDLConnection.isDatabaseRunning(databasetype, databasename)) {
                typhonDLConnection.createDatabase(databasetype, databasename);
            }
            //Executing evolution operations
            newEntity = smo.getPOJOFromInputParameter("entity", Entity.class);
            typhonInterface.createEntity(newEntity);
            //Informing TyphonML to set the targetModel as the current one and regenerate API.
            typhonMLInterface.setNewTyphonMLModel(targetmodelid);
            return "entity created";
        }
        else
            throw new InputParameterException("Missing parameter");

    }

    @Override
    public String renameEntity(SMO smo) throws InputParameterException {
        String oldEntityName,newEntityName, targetmodel;
        if (containParameters(smo, Arrays.asList("oldentityname", "newentityname", "targetmodel"))) {
            targetmodel = smo.getInputParameter().get("targetmodel").toString();
            oldEntityName = smo.getInputParameter().get("oldentityname").toString();
            newEntityName = smo.getInputParameter().get("newentityname").toString();
            typhonInterface.setTyphonMLTargetModel(targetmodel);
            typhonInterface.renameEntity(oldEntityName, newEntityName);
            typhonMLInterface.setNewTyphonMLModel(targetmodel);
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
