package com.typhon.evolutiontool.services.typhonML;


import com.typhon.evolutiontool.entities.DatabaseType;
import com.typhon.evolutiontool.entities.EntityDO;
import com.typhon.evolutiontool.entities.Relation;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import typhonml.Model;

/**
 * Interface to access TyphonML.
 * Changes the current model or querying the model.
 */
public interface TyphonMLInterface {

    /**
     *  Asks the TyphonML module to set the running TyphonML model to the specified version @param newModelIdentifier
     * @param newModelIdentifier
     */
    void setNewTyphonMLModel(String newModelIdentifier);


    /**
     * Returns an EntityDO object of entity @param entityid in the TyphonML version @param sourcemodelid.
     * @param entityid
     * @param model
     * @return
     */
    typhonml.Entity getEntityTypeFromName(String entityid, Model model);

    String getAttributeIdOfEntityType(String sourceEntityName);

    /**
     * Asks TyphonML module if the given entity name @param entityname is involved (as source or target) in a relationship.
     * @param entityname
     * @return
     */
    boolean hasRelationship(String entityname, Model model);

    DatabaseType getDatabaseType(String entityname, Model model);

    String getAttributeOfType(String entityname, EntityDO targetEntityType);

    Relation getRelationFromName(String relationname);

    Model createEntityType(Model sourceModel, EntityDO newEntity);

    Model deleteEntityType(String entityname, Model model);

    Model renameEntity(String oldEntityName, String newEntityName, Model model);

    Model copyEntityType(String sourceEntityName, String targetEntityName, Model model);

    Model createNewEntityMappingInDatabase(DatabaseType databaseType, String dbname, String targetLogicalName, String entityTypeNameToMap, Model targetModel);

    Model createDatabase(DatabaseType dbtype, String databasename, Model targetModel) throws InputParameterException;

    String getDatabaseName(String sourceEntityName, Model model);

    Model deleteEntityMappings(String entityname, Model model);

    Model createRelationship(Relation relation, Model model);

    Model deleteRelationshipInEntity(String relationname, String entityname, Model model);
}
