package com.typhon.evolutiontool.services.typhonQL;

import com.typhon.evolutiontool.datatypes.DataTypeDO;
import com.typhon.evolutiontool.entities.*;
import typhonml.Model;

import java.util.List;
import java.util.Set;

public interface TyphonQLInterface {

    /**
     * Upload a new XMI model to the polystore
     *
     * @param model the new TyphonML model
     * @return the TyphonQL query
     */
    String uploadSchema(Model model);

    /**
     * Retrieve the latest version of the XMI deployed on the polystore
     *
     * @return the Model containing the XMI
     */
    Model getCurrentModel();

    /**
     * Create a new entity (with its attributes and relations) in the polystore using a TyphonQL query
     *
     * @param entity the entity
     */
    void createEntity(EntityDO entity, String databaseName);

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
     * @param entityName    the name of the entity
     * @param attributeName the name of the attribute
     * @param dataType      the type of the attribute
     * @return the TyphonQL query
     */
    String createEntityAttribute(String entityName, String attributeName, DataTypeDO dataType);

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
     * @param entityName               the name of the entity
     * @param attributesToSelect       the attributes to select from the entity
     * @param relationsToSelect        the attributes to select from the entity
     * @param attributeToFilterOn      the name of the entity attribute for the "where" clause
     * @param attributeToFilterOnValue the value of the entity attribute for the "where" clause
     * @return the WorkingSet results
     */
    WorkingSet selectEntityData(String entityName, Set<String> attributesToSelect, List<String> relationsToSelect, String attributeToFilterOn, String attributeToFilterOnValue);

    /**
     * Update the entity name in the source entity data
     *
     * @param sourceEntityData the source entity data
     * @param sourceEntityName the source entity name
     * @param targetEntityName the target entity name
     */
    void updateEntityNameInSourceEntityData(WorkingSet sourceEntityData, String sourceEntityName, String targetEntityName);

    /**
     * Remove the entity useless attributes in the entity data
     *
     * @param entityData       the entity data
     * @param entityName       the entity name
     * @param attributesToKeep the entity attributes to keep in the entity data
     */
    void removeUselessAttributesInSourceEntityData(WorkingSet entityData, String entityName, Set<String> attributesToKeep);

    /**
     * Insert the working set data into the entity in the polystore using a TyphonQL query
     *
     * @param entityName the name of the entity
     * @param ws         the WorkingSet containing the data to insert
     * @param entityDO   the entityDO containing the attributes and their types
     * @return the TyphonQL query
     */
    String insertEntityData(String entityName, WorkingSet ws, EntityDO entityDO);

    /**
     * Delete the entity data from the polystore using a TyphonQL query
     *
     * @param entityName     the name of the entity
     * @param attributeName  the name of the entity attribute for the "where" clause
     * @param attributeValue the value of the entity attribute for the "where" clause
     */
    void deleteEntityData(String entityName, String attributeName, String attributeValue);

    /**
     * Drop the entity in the polystore using a TyphonQL query
     *
     * @param entityName the name of the entity
     * @return the TyphonQL query
     */
    String dropEntity(String entityName);

    void renameEntity(String oldEntityName, String newEntityName);

    /**
     * Remove the attributes and their data of a given entity.
     *
     * @param entityName the name of the entity
     * @param attribute  the list of attributes to remove
     */
    void removeAttribute(String entityName, String attribute);

    /**
     * Depending on the underlying databases. Creates foreign key (for relational) or changes the way the data must be inserted (for NoSQL). See detailed action plan appendix.
     *
     * @param relation the foreign key relation
     */
    void createRelationshipType(RelationDO relation);

    void deleteRelationshipInEntity(String relationname, String entityname);

    void enableContainment(String relationName, String entityname);

    void disableContainment(String relationName, String entityname);

    void changeCardinalityInRelation(String relationname, String entityname, CardinalityDO cardinality);

    void addAttribute(AttributeDO attributeDO, String entityname);

    void addAttribute(String attributeName, String entityname, DataTypeDO dataType);

    void renameRelation(String entityName, String relationName, String newRelationName);

    void renameAttribute(String oldAttributeName, String newAttributeName, String entityName);

    /**
     * Change the attribute type in the polystore using a TyphonQL query
     *
     * @param attributeName     the name of the attribute
     * @param attributeTypeName the name of the attribute type
     * @param entityName        the name of the entity containing the attribute
     */
    void changeTypeAttribute(String attributeName, String attributeTypeName, String entityName);

    /**
     * Update entity data (first WorkingSet) with the data from the second WorkingSet, joined by the relation.
     * According firstOrSecondEntityRelation, the relation data are contained in the first or in the second WS.
     *
     * @param entityName the entity name in which the data are updated
     * @param firstWs the existing entity data
     * @param secondWs the data to be updated into the entity
     * @param secondEntity the entity to be merged
     * @param relationName the name of the relation between the entity and the second WS
     * @param firstOrSecondEntityRelation true if the entity contains the reference data to the second WS, false if the second WS contains the reference data to the entity
     */
    void updateEntityData(String entityName, WorkingSet firstWs, WorkingSet secondWs, EntityDO secondEntity, String relationName, Boolean firstOrSecondEntityRelation);

    /**
     * Add the entity attributes to the index of the table
     *
     * @param entityName the name of the entity containing the attributes to add to the index
     * @param attributesNames the names of the attributes to add to the index
     */
    void addTableIndex(String entityName, Set<String> attributesNames);
}
