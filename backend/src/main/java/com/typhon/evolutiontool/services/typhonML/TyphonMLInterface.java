package com.typhon.evolutiontool.services.typhonML;


import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import typhonml.*;

import javax.xml.crypto.Data;

/**
 * Interface to access TyphonML.
 * Changes the current model or querying the model.
 */
public interface TyphonMLInterface {

    /**
     * Returns an EntityDO object of entity @param entityid in the TyphonML version @param sourcemodelid.
     * @param entityid
     * @param model
     * @return
     */
    Entity getEntityTypeFromName(String entityid, Model model);

    /**
     * Asks TyphonML module if the given entity name @param entityname is involved (as source or target) in a relationship.
     * @param entityname
     * @return
     */
    boolean hasRelationship(String entityname, Model model);

    Database getEntityDatabase(String entityName, Model model);

    String getEntityNameInDatabase(String entityName, Model model);

    Relation getRelationFromNameInEntity(String relationname, String entityname, Model model);

    DataType getDataTypeFromName(String dataTypeName, Model model);

    Model createEntityType(Model sourceModel, EntityDO newEntity);

    Model deleteEntityType(String entityname, Model model);

    Model renameEntity(String oldEntityName, String newEntityName, Model model);

    Model copyEntityType(String sourceEntityName, String targetEntityName, Model model);

    Model createNewEntityMappingInDatabase(DatabaseType databaseType, String dbname, String targetLogicalName, String entityTypeNameToMap, Model model);

    Model updateEntityMappingInDatabase(String entityName, String databaseName, Model model);

    Database getDatabaseFromName(String dbname, Model model);

    Model createDatabase(DatabaseType dbtype, String databasename, Model targetModel) throws InputParameterException;

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
}
