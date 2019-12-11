package com.typhon.evolutiontool.services.typhonQL;

import com.typhon.evolutiontool.entities.*;
import typhonml.Model;

import java.util.List;
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
     * @param model the TyphonML schema
     * @return the TyphonQL query
     */
    String createEntity(String entityName, String databaseName, Model model);

    /**
     * Create a new attribute for the entity in the polystore using a TyphonQL query
     *
     * @param entityName        the name of the entity
     * @param attributeName     the name of the attribute
     * @param attributeTypeName the type name of the attribute
     * @param model the TyphonML schema
     * @return the TyphonQL query
     */
    String createEntityAttribute(String entityName, String attributeName, String attributeTypeName, Model model);

    /**
     * Create a new relation for the entity in the polystore using a TyphonQL query
     *
     * @param entityName             the name of the entity
     * @param relationName           the name of the relation
     * @param containment            the boolean value of containment of the relation
     * @param relationTargetTypeName the name of the target type of the relation
     * @param cardinality            the cardinality of the relation
     * @param model the TyphonML schema
     * @return the TyphonQL query
     */
    String createEntityRelation(String entityName, String relationName, boolean containment, String relationTargetTypeName, CardinalityDO cardinality, Model model);

    /**
     * Select the entity data from the polystore using a TyphonQL query
     *
     * @param entityName the name of the entity
     * @param model the TyphonML schema
     * @return the WorkingSet results
     */
    WorkingSet selectEntityData(String entityName, Model model);

    /**
     * Insert the working set data into the entity in the polystore using a TyphonQL query
     *
     * @param entityName       the name of the entity
     * @param entityAttributes the set of entity attributes
     * @param ws the WorkingSet containing the data to insert
     * @param model the TyphonML schema
     * @return the TyphonQL query
     */
    String insertEntityData(String entityName, Set<String> entityAttributes, WorkingSet ws, Model model);

    /**
     * Drop the entity in the polystore using a TyphonQL query
     *
     * @param entityName the name of the entity
     * @param model the TyphonML schema
     * @return the TyphonQL query
     */
    String dropEntity(String entityName, Model model);

    void renameEntity(String oldEntityName, String newEntityName, Model model);

    WorkingSet readAllEntityData(EntityDO entity, Model model);

    /**
     * Retrieve the entity data using the model provided.
     *
     * @param entityId the identifier of the entity
     * @param model    the model containing the information about the entity
     * @return the WorkingSet containing the entity data
     */
    WorkingSet readAllEntityData(String entityId, Model model);

    WorkingSet readEntityDataEqualAttributeValue(String sourceEntityName, String attributeName, String attributeValue, Model model);

    WorkingSet readEntityDataSelectAttributes(String sourceEntityName, Set<String> attributes, Model model);

    void deleteAllEntityData(String entityid, Model model);

    /**
     * Remove the attributes and their data of a given entity.
     *
     * @param entityName the name of the entity
     * @param attribute the list of attributes to remove
     * @param model      the model containing the information about the entity and its attributes
     */
    void removeAttribute(String entityName, String attribute, Model model);

    void deleteWorkingSetData(WorkingSet dataToDelete, Model model);

    /**
     * Depending on the underlying databases. Creates foreign key (for relational) or changes the way the data must be inserted (for NoSQL). See detailed action plan appendix.
     *
     * @param relation the foreign key relation
     * @param model    the model containing the information about the relation
     */
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

    void renameRelation(String entityName, String relationName, String newRelationName, Model model);

    void renameAttribute(String oldAttributeName, String newAttributeName, String entityName, Model model);

    void changeTypeAttribute(AttributeDO attribute, String entityName, Model model);
}
