package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.Entity;
import com.typhon.evolutiontool.entities.Relation;
import com.typhon.evolutiontool.entities.WorkingSet;
import typhonml.Model;

import java.util.List;

/*
    This interface specifies the operations that needs to be performed on the Typhon polystore in order to apply a Schema Modification Operator. Either modification of structure or of data.
 */
public interface TyphonInterface {

    /**
     * Creates a new Entity
     * @param newEntity
     * @return
     */
    String createEntityType(Entity newEntity, Model model);

    void renameEntity(String oldEntityName, String newEntityName, Model model);

    WorkingSet readAllEntityData(Entity entity, Model model);

    WorkingSet readAllEntityData(String entityId, Model model);

    WorkingSet readEntityDataEqualAttributeValue(Entity sourceEntity, String attributeName, String attributeValue, Model model);

    WorkingSet readEntityDataSelectAttributes(String sourceEntityName, List<String> attributes, Model model);


    WorkingSet deleteAllEntityData(String entityid, Model model);

    void deleteEntityStructure(String entityname, Model model);

    void deleteAttributes(String entityname, List<String> attributes, Model model);

    void deleteWorkingSetData(WorkingSet dataToDelete, Model model);

    void createRelationshipType(Relation relation, Model model);

    void writeWorkingSetData(WorkingSet workingSetData, Model model);

    void addForeignKey(Entity sourceEntity, Entity targetEntity, String targetmodelid, boolean isMandatory, boolean isIdentifier);

    void createJoinTable(Entity sourceEntity, Entity targetEntity);

    void deleteForeignKey(Entity sourceEntity, Entity targetEntity);

    WorkingSet readRelationship(Relation relation, Model model);

    WorkingSet deleteRelationship(Relation relation, boolean datadelete, Model model);
}
