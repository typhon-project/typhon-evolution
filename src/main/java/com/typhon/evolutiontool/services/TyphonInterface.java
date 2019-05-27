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

    void renameEntity(String oldEntityName, String newEntityName, String typhonMLVersion);

    WorkingSet readAllEntityData(Entity entity, String typhonMLVersion);

    WorkingSet readAllEntityData(String entityId, String typhonMLVersion);

    WorkingSet readEntityDataEqualAttributeValue(Entity sourceEntity, String attributeName, String attributeValue, String typhonMLVersion);

    WorkingSet readEntityDataSelectAttributes(String sourceEntityName, List<String> attributes, String typhonMLVersion);


    WorkingSet deleteAllEntityData(String entityid, String typhonMLVersion);

    void deleteEntityStructure(String entityname, String typhonMLVersion);

    void deleteAttributes(String entityname, List<String> attributes, String typhonMLVersion);

    void deleteWorkingSetData(WorkingSet dataToDelete, String typhonMLVersion);

    void createRelationshipType(Relation relation, String typhonMLVersion);

    void writeWorkingSetData(WorkingSet workingSetData, String typhonMLVersion);

    void addForeignKey(Entity sourceEntity, Entity targetEntity, String targetmodelid, boolean isMandatory, boolean isIdentifier);

    void createJoinTable(Entity sourceEntity, Entity targetEntity);

    void deleteForeignKey(Entity sourceEntity, Entity targetEntity);

    WorkingSet readRelationship(Relation relation, String typhonMLVersion);

    WorkingSet deleteRelationship(Relation relation, boolean datadelete, String typhonMLversion);
}
