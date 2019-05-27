package com.typhon.evolutiontool.services.typhonML;

import com.typhon.evolutiontool.entities.Entity;
import com.typhon.evolutiontool.entities.Relation;
import com.typhon.evolutiontool.entities.WorkingSet;
import com.typhon.evolutiontool.services.TyphonInterface;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import typhonml.Model;

/*
    This class implements @see TyphonInterface using the TyphonML module CRUD API.
 */
@Component("typhoncrudapi")
public class TyphonCRUDApiInterfaceImpl implements TyphonInterface {

    Logger logger = LoggerFactory.getLogger(TyphonCRUDApiInterfaceImpl.class);


    @Override
    public String createEntityType(Entity newEntity, Model model) {
        logger.info("Create entity [{}] via TyphonCRUD API on typhon", newEntity.getName());
        return "Call to CRUD API on TyphonML model : "+model;
    }

    @Override
    public void renameEntity(String oldEntityName, String newEntityName, String typhonMLVersion) {

    }

    @Override
    public WorkingSet readAllEntityData(Entity entity, String typhonMLVersion) {
        return null;
    }

    @Override
    public WorkingSet readAllEntityData(String entityId, String typhonMLVersion) {
        return null;
    }

    @Override
    public WorkingSet readEntityDataEqualAttributeValue(Entity sourceEntity, String attributeName, String attributeValue, String typhonMLVersion) {
        return null;
    }

    @Override
    public WorkingSet readEntityDataSelectAttributes(String sourceEntityName, List<String> attributes, String typhonMLVersion) {
        return null;
    }

    @Override
    public WorkingSet deleteAllEntityData(String entityid, String typhonMLVersion) {
        return null;
    }

    @Override
    public void deleteEntityStructure(String entityname, String typhonMLVersion) {

    }

    @Override
    public void deleteAttributes(String entityname, List<String> attributes, String typhonMLVersion) {

    }

    @Override
    public void deleteWorkingSetData(WorkingSet dataToDelete, String typhonMLVersion) {

    }

    @Override
    public void createRelationshipType(Relation relation, String typhonMLVersion) {

    }

    @Override
    public void writeWorkingSetData(WorkingSet workingSetData, String typhonMLVersion) {

    }

    @Override
    public void addForeignKey(Entity sourceEntity, Entity targetEntity, String targetmodelid, boolean isMandatory, boolean isIdentifier) {

    }

    @Override
    public void createJoinTable(Entity sourceEntity, Entity targetEntity) {

    }

    @Override
    public void deleteForeignKey(Entity sourceEntity, Entity targetEntity) {

    }

    @Override
    public WorkingSet readRelationship(Relation relation, String typhonMLVersion) {
        return null;
    }

    @Override
    public WorkingSet deleteRelationship(Relation relation, boolean datadelete, String typhonMLversion) {
        return null;
    }
}
