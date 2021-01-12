package com.typhon.evolutiontool.services.typhonML;


import com.typhon.evolutiontool.datatypes.DataTypeDO;
import com.typhon.evolutiontool.entities.*;
import typhonml.Database;
import typhonml.Entity;
import typhonml.Model;
import typhonml.Relation;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface to access TyphonML.
 * Changes the current model or querying the model.
 */
public interface TyphonMLInterface {

    /**
     * Returns an EntityDO object of entity @param entityid in the TyphonML version @param sourcemodelid.
     *
     * @param entityid
     * @param model
     * @return
     */
    Entity getEntityByName(String entityid, Model model);

    /**
     * Asks TyphonML module if the given entity name @param entityname is involved (as source or target) in a relationship.
     *
     * @param entityname
     * @return
     */
    boolean hasRelationship(String entityname, Model model);

    /**
     * Check if the table in the database has an index
     * @param databaseName the name of the database containing the table
     * @param tableName the name of the table for which we check if an index exists
     * @param sourceModel the current ML model
     * @return true if the table contains an index; false otherwise
     */
    boolean hasTableIndex(String databaseName, String tableName, Model sourceModel);
    /**
     * Add a list of attributes to the index list of a table in the ML model
     * @param databaseName the name of the database containing the table
     * @param tableName the name of the table containing the existing index list
     * @param entityName the name of the entity containing the attributes to add to the index
     * @param entityAttributesNames the attributes names to add to the index list of the table
     * @param sourceModel the current ML model to update
     * @return the updated ML model
     */
    Model addTableIndex(String databaseName, String tableName, String entityName, Set<String> entityAttributesNames, Model sourceModel);

    /**
     * Add a list of attributes to the index list of a collection in the ML model
     * @param databaseName the name of the database containing the collection
     * @param collectionName the name of the collection containing the existing index list
     * @param entityName the name of the entity containing the attributes to add to the index
     * @param entityAttributesNames the attributes names to add to the index list of the collection
     * @param sourceModel the current ML model to update
     * @return the updated ML model
     */
    Model addCollectionIndex(String databaseName, String collectionName, String entityName, Set<String> entityAttributesNames, Model sourceModel);

    Database getEntityDatabase(String entityName, Model model);

    String getEntityNameInDatabase(String entityName, Model model);

    Relation getRelationFromNameInEntity(String relationname, String entityname, Model model);

    Model createEntityType(Model sourceModel, EntityDO newEntity);

    Model deleteEntityType(String entityname, Model model);

    Model renameEntity(String oldEntityName, String newEntityName, Model model);

    Model createNewEntityMappingInDatabase(DatabaseType databaseType, String dbname, String targetLogicalName, String entityTypeNameToMap, Model model);

    Model updateEntityMappingInDatabase(String entityName, String databaseName, Model model);

    Database getDatabaseFromName(String dbname, Model model);

    Model deleteEntityMappings(String entityName, String entityNameInDatabase, Model model);

    Model createRelationship(RelationDO relation, Model model);

    Model deleteRelationshipInEntity(String relationname, String entityname, Model model);

    Model enableContainment(RelationDO relation, Model model);

    Model disableContainment(RelationDO relation, Model model);

    Model changeCardinalityInRelation(RelationDO relationName, CardinalityDO cardinality, Model model);

    Model addAttribute(AttributeDO attributeDO, String entityName, Model model);

    Model removeAttribute(String attributeName, String entityName, Model model);

    Model renameAttribute(String oldAttributeName, String newAttributeName, String entityName, Model model);

    Model changeTypeAttribute(AttributeDO attributeDO, String entityName, String dataTypeName, Model model);

    Model enableOpposite(RelationDO relation, RelationDO oppositeRelation, Model model);

    Model disableOpposite(RelationDO relation, Model model);

    Model renameRelation(String relationName, String entityName, String newRelationName, Model model);

    Model removeCurrentChangeOperator(Model model);

    /**
     * Merge the second entity into the first one
     * @param firstEntityName the name of the entity who will contain the second one
     * @param secondEntityName the entity name to be merged
     * @return the ML model containing the merged entity
     */
    Model mergeEntities(String firstEntityName, String secondEntityName, Model model);
}
