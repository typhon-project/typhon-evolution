package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.Entity;
import com.typhon.evolutiontool.entities.ParametersKeyString;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
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
     3. Notify the TyphonML module that the operator is executed an that the current running TyphonML model can be changed.
 */
@Service
public class EvolutionServiceImpl implements EvolutionService{


    Logger logger = LoggerFactory.getLogger(EvolutionServiceImpl.class);
    @Autowired
    @Qualifier("fakeimplementation")
    private TyphonDLInterface typhonDLInterface;
    @Autowired
    @Qualifier("typhonql")
    private TyphonInterface typhonInterface;
    @Autowired
    @Qualifier("fakeimplementation")
    private TyphonMLInterface typhonMLInterface;


    @Override
    public String addEntity(SMO smo) throws InputParameterException {
        Entity newEntity;
        String databasetype, databasename, targetmodelid;
        // Verify ParametersKeyString
        if(containParameters(smo,Arrays.asList(ParametersKeyString.ENTITY,ParametersKeyString.TARGETMODEL,ParametersKeyString.DATABASENAME,ParametersKeyString.DATABASETYPE))){
            targetmodelid = smo.getInputParameter().get(ParametersKeyString.TARGETMODEL).toString();
            databasetype = smo.getInputParameter().get(ParametersKeyString.DATABASETYPE).toString();
            databasename = smo.getInputParameter().get(ParametersKeyString.DATABASENAME).toString();
            // Verify that an instance of the underlying database is running in the TyphonDL.
            if (!typhonDLInterface.isDatabaseRunning(databasetype, databasename)) {
                typhonDLInterface.createDatabase(databasetype, databasename);
            }
            //Executing evolution operations
            newEntity = smo.getPOJOFromInputParameter(ParametersKeyString.ENTITY, Entity.class);
            typhonInterface.createEntity(newEntity,targetmodelid);
            //Informing TyphonML to set the targetModel as the current one and regenerate API.
            typhonMLInterface.setNewTyphonMLModel(targetmodelid);
            return "entity created";
        }
        else
            throw new InputParameterException("Missing parameter");

    }

    @Override
    public String removeEntity(SMO smo) throws InputParameterException {
        return null;
    }

    @Override
    public String renameEntity(SMO smo) throws InputParameterException {
        String oldEntityName,newEntityName, targetmodel;
        if (containParameters(smo, Arrays.asList(ParametersKeyString.OLDENTITYNAME, ParametersKeyString.NEWENTITYNAME, ParametersKeyString.TARGETMODEL))) {
            targetmodel = smo.getInputParameter().get(ParametersKeyString.TARGETMODEL).toString();
            oldEntityName = smo.getInputParameter().get(ParametersKeyString.OLDENTITYNAME).toString();
            newEntityName = smo.getInputParameter().get(ParametersKeyString.NEWENTITYNAME).toString();
            typhonInterface.renameEntity(oldEntityName, newEntityName,targetmodel);
            typhonMLInterface.setNewTyphonMLModel(targetmodel);
            return "entity renamed";
        } else {
            throw new InputParameterException("Missing parameter");
        }
    }

    @Override
    public String splitHorizontal(SMO smo) throws InputParameterException {
        return null;
    }

    @Override
    public String splitVertical(SMO smo) throws InputParameterException {
        return null;
    }

    @Override
    public String migrateEntity(SMO smo) throws InputParameterException {
        Entity entity;
        String entityname, targetmodelid, databasetype, databasename, sourcemodelid;
        if (containParameters(smo, Arrays.asList(ParametersKeyString.ENTITY, ParametersKeyString.TARGETMODEL, ParametersKeyString.SOURCEMODEL, ParametersKeyString.DATABASENAME, ParametersKeyString.DATABASETYPE))) {
            entityname = smo.getInputParameter().get(ParametersKeyString.ENTITY).toString();
            targetmodelid = smo.getInputParameter().get(ParametersKeyString.TARGETMODEL).toString();
            sourcemodelid = smo.getInputParameter().get(ParametersKeyString.SOURCEMODEL).toString();
            databasetype = smo.getInputParameter().get(ParametersKeyString.DATABASETYPE).toString();
            databasename = smo.getInputParameter().get(ParametersKeyString.DATABASENAME).toString();
            entity = typhonMLInterface.getEntityTypeFromId(entityname, sourcemodelid);
            // Verify that an instance of the underlying database is running in the TyphonDL.
            if (!typhonDLInterface.isDatabaseRunning(databasetype, databasename)) {
                typhonDLInterface.createDatabase(databasetype, databasename);
            }
            typhonInterface.createEntity(entity, targetmodelid);
            typhonInterface.writeWorkingSetData(typhonInterface.readEntityData(entity,sourcemodelid),targetmodelid);
            typhonMLInterface.setNewTyphonMLModel(targetmodelid);
            return "entity migrated";
        } else {
            throw new InputParameterException("Missing parameter");
        }
    }

    @Override
    public String mergeEntities(SMO smo) throws InputParameterException {
        return null;
    }

    @Override
    public String addRelationship(SMO smo) throws InputParameterException {
        return null;
    }

    @Override
    public String removeRelationship(SMO smo) {
        return null;
    }

    @Override
    public String enableContainmentInRelationship(SMO smo) {
        return null;
    }

    @Override
    public String disableContainmentInRelationship(SMO smo) {
        return null;
    }

    @Override
    public String enableOppositeRelationship(SMO smo) {
        return null;
    }

    @Override
    public String disableOppositeRelationship(SMO smo) {
        return null;
    }

    @Override
    public String changeCardinality(SMO smo) {
        return null;
    }

    @Override
    public String addAttribute(SMO smo) {
        return null;
    }

    @Override
    public String removeAttribute(SMO smo) {
        return null;
    }

    @Override
    public String renameAttribute(SMO smo) {
        return null;
    }

    @Override
    public String changeTypeAttribute(SMO smo) {
        return null;
    }

    @Override
    public String addIdentifier(SMO smo) {
        return null;
    }

    @Override
    public String addComponentToIdentifier(SMO smo) {
        return null;
    }

    @Override
    public String removeIdentifier(SMO smo) {
        return null;
    }

    @Override
    public String removeComponentToIdentifier(SMO smo) {
        return null;
    }

    @Override
    public String addIndex(SMO smo) {
        return null;
    }

    @Override
    public String removeIndex(SMO smo) {
        return null;
    }

    @Override
    public String addComponentToIndex(SMO smo) {
        return null;
    }

    @Override
    public String removeComponentToIndex(SMO smo) {
        return null;
    }

    @Override
    public String renameRelationalTable(SMO smo) {
        return null;
    }

    @Override
    public String renameDocumentCollection(SMO smo) {
        return null;
    }

    @Override
    public String renameColumnFamilyName(SMO smo) {
        return null;
    }

    public boolean containParameters(SMO smo, List<String> parameters) {
        logger.info("Verifying input parameter for [{}] - [{}] operator",smo.getTyphonObject(), smo.getEvolutionOperator());
        return smo.inputParametersContainsExpected(parameters);
    }

    public void setTyphonDLInterface(TyphonDLInterface typhonDLInterface) {
        this.typhonDLInterface = typhonDLInterface;
    }

    public void setTyphonInterface(TyphonInterface typhonInterface) {
        this.typhonInterface = typhonInterface;
    }

    public void setTyphonMLInterface(TyphonMLInterface typhonMLInterface) {
        this.typhonMLInterface = typhonMLInterface;
    }
}
