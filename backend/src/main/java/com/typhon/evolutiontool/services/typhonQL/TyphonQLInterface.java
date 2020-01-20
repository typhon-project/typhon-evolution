package com.typhon.evolutiontool.services.typhonQL;

import com.typhon.evolutiontool.entities.*;
import typhonml.Model;

import java.util.Set;

/*
    This interface specifies the operations that needs to be performed on the Typhon polystore in order to apply a Schema Modification Operator. Either modification of structure or of data.
 */
public interface TyphonQLInterface {

    /**
     * Upload a new XMI model to the polystore
     *
     * @param model the new TyphonML model
     * @return the TyphonQL query
     */
    String uploadSchema(Model model);

    /**
     * Create a new entity in the polystore using a TyphonQL query
     *
     * @param entityName the name of the entity
     * @return the TyphonQL query
     */
    String createEntity(String entityName, String databaseName);

    /**
     * Create a new attribute for the entity in the polystore using a TyphonQL query
     *
     * @param entityName        the name of the entity
     * @param attributeName     the name of the attribute
     * @param attributeTypeName the type name of the attribute
     * @return the TyphonQL query
     */
    String createEntityAttribute(String entityName, String attributeName, String attributeTypeName);

    /**
     * Create a new relation for the entity in the polystore using a TyphonQL query
     *
     * @param entityName             the name of the entity
     * @param relationName           the name of the relation
     * @param containment            the boolean value of containment of the relation
     * @param relationTargetTypeName the name of the target type of the relation
     * @param cardinality            the cardinality of the relation
     * @return the TyphonQL query
     */
    String createEntityRelation(String entityName, String relationName, boolean containment, String relationTargetTypeName, CardinalityDO cardinality);

    /**
     * Select the entity data from the polystore using a TyphonQL query
     *
     * @param entityName the name of the entity
     * @return the WorkingSet results
     */
    WorkingSet selectEntityData(String entityName);

    /**
     * Update the entity name in the source entity data
     * @param sourceEntityData the source entity data
     * @param sourceEntityName the source entity name
     * @param targetEntityName the target entity name
     */
    void updateEntityNameInSourceEntityData(WorkingSet sourceEntityData, String sourceEntityName, String targetEntityName);

    /**
     * Insert the working set data into the entity in the polystore using a TyphonQL query
     *
     * @param entityName       the name of the entity
     * @param ws               the WorkingSet containing the data to insert
     * @param entityDO               the entityDO containing the attributes and their types
     * @return the TyphonQL query
     */
    String insertEntityData(String entityName, WorkingSet ws, EntityDO entityDO);

    /**
     * Drop the entity in the polystore using a TyphonQL query
     *
     * @param entityName the name of the entity
     * @return the TyphonQL query
     */
    String dropEntity(String entityName);

    void renameEntity(String oldEntityName, String newEntityName);

    /**
     * Retrieve the entity data using the model provided.
     *
     * @param entityId the identifier of the entity
     * @return the WorkingSet containing the entity data
     */
    WorkingSet readAllEntityData(String entityId);

    WorkingSet readEntityDataEqualAttributeValue(String sourceEntityName, String attributeName, String attributeValue);

    WorkingSet readEntityDataSelectAttributes(String sourceEntityName, Set<String> attributes);

    void deleteAllEntityData(String entityid);

    /**
     * Remove the attributes and their data of a given entity.
     *
     * @param entityName the name of the entity
     * @param attribute  the list of attributes to remove
     */
    void removeAttribute(String entityName, String attribute);

    void deleteWorkingSetData(WorkingSet dataToDelete);

    /**
     * Depending on the underlying databases. Creates foreign key (for relational) or changes the way the data must be inserted (for NoSQL). See detailed action plan appendix.
     *
     * @param relation the foreign key relation
     */
    void createRelationshipType(RelationDO relation);

    void writeWorkingSetData(WorkingSet workingSetData);

    void deleteRelationshipInEntity(String relationname, String entityname);

    void enableContainment(String relationName, String entityname);

    void disableContainment(String relationName, String entityname);

    void changeCardinalityInRelation(String relationname, String entityname, CardinalityDO cardinality);

    void addAttribute(AttributeDO attributeDO, String entityname);

    void renameRelation(String entityName, String relationName, String newRelationName);

    void renameAttribute(String oldAttributeName, String newAttributeName, String entityName);

    void changeTypeAttribute(AttributeDO attribute, String entityName);
}
