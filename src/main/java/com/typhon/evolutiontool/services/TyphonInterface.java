package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.EntityDO;
import com.typhon.evolutiontool.entities.Relation;
import com.typhon.evolutiontool.entities.WorkingSet;
import typhonml.Model;

import java.util.List;

/*
    This interface specifies the operations that needs to be performed on the Typhon polystore in order to apply a Schema Modification Operator. Either modification of structure or of data.
 */
public interface TyphonInterface {

    /**
     * Creates a new EntityDO
     * @param newEntity
     * @return
     */
    String createEntityType(EntityDO newEntity, Model model);

    String createEntityType(typhonml.Entity newEntity, Model model);

    void renameEntity(String oldEntityName, String newEntityName, Model model);

    WorkingSet readAllEntityData(EntityDO entity, Model model);

    WorkingSet readAllEntityData(String entityId, Model model);

    WorkingSet readEntityDataEqualAttributeValue(String sourceEntityName, String attributeName, String attributeValue, Model model);

    WorkingSet readEntityDataSelectAttributes(String sourceEntityName, List<String> attributes, Model model);


    WorkingSet deleteAllEntityData(String entityid, Model model);

    void deleteEntityStructure(String entityname, Model model);

    void deleteAttributes(String entityname, List<String> attributes, Model model);

    void deleteWorkingSetData(WorkingSet dataToDelete, Model model);

    void createRelationshipType(Relation relation, Model model);

    void writeWorkingSetData(WorkingSet workingSetData, Model model);

    void addForeignKey(EntityDO sourceEntity, EntityDO targetEntity, String targetmodelid, boolean isMandatory, boolean isIdentifier);

    void createJoinTable(EntityDO sourceEntity, EntityDO targetEntity);

    void deleteForeignKey(EntityDO sourceEntity, EntityDO targetEntity);

    WorkingSet readRelationship(Relation relation, Model model);

    WorkingSet deleteRelationship(Relation relation, boolean datadelete, Model model);

    void deleteRelationshipInEntity(String relationname, String entityname, Model model);
}
