package main.java.com.typhon.evolutiontool.services.typhonQL;

import main.java.com.typhon.evolutiontool.entities.*;
import typhonml.Model;

import java.util.List;

/*
    This interface specifies the operations that needs to be performed on the Typhon polystore in order to apply a Schema Modification Operator. Either modification of structure or of data.
 */
public interface TyphonQLInterface {

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

    void removeAttributes(String entityname, List<String> attributes, Model model);

    void deleteWorkingSetData(WorkingSet dataToDelete, Model model);

    void createRelationshipType(RelationDO relation, Model model);

    void writeWorkingSetData(WorkingSet workingSetData, Model model);

    void deleteForeignKey(EntityDO sourceEntity, EntityDO targetEntity);

    WorkingSet readRelationship(RelationDO relation, Model model);

    void deleteRelationship(RelationDO relation, boolean datadelete, Model model);

    void deleteRelationshipInEntity(String relationname, String entityname, Model model);

    void enableContainment(String relationName, String entityname, Model model);

    void disableContainment(String relationName, String entityname, Model model);

    void changeCardinalityInRelation(String relationname, String entityname, CardinalityDO cardinality, Model model);

    void addAttribute(AttributeDO attributeDO, String entityname, Model model);

    void renameRelation(String relationName, String newRelationName, Model model);

    void renameAttribute(String oldAttributeName, String newAttributeName, String entityName, Model model);

    void changeTypeAttribute(AttributeDO attribute, String entityName, Model model);
}
