package com.typhon.evolutiontool.services.typhonML;


import com.typhon.evolutiontool.entities.Database;
import com.typhon.evolutiontool.entities.Entity;
import com.typhon.evolutiontool.entities.Relation;
import typhonml.Model;

import java.util.List;

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
     * Returns an Entity object of entity @param entityid in the TyphonML version @param sourcemodelid.
     * @param entityid
     * @param sourcemodelid
     * @return
     */
    Entity getEntityTypeFromId(String entityid, String sourcemodelid);

    String getAttributeIdOfEntityType(String sourceEntityName);

    /**
     * Asks TyphonML module if the given entity name @param entityname is involved (as source or target) in a relationship.
     * @param entityname
     * @return
     */
    boolean hasRelationship(String entityname, Model model);

    Database getDatabaseType(String entityname);

    String getAttributeOfType(String entityname, Entity targetEntityType);

    Relation getRelationFromName(String relationname);

    Model createEntityType(Model sourceModel, Entity newEntity);

    Model deleteEntityType(String entityname, Model model);
}
