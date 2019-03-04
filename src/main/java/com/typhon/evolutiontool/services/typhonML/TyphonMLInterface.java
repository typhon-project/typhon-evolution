package com.typhon.evolutiontool.services.typhonML;


import com.typhon.evolutiontool.entities.Entity;

/**
 * Interface to access TyphonML.
 * Changes the current model or querying the model.
 */
public interface TyphonMLInterface {

    void setNewTyphonMLModel(String newModelIdentifier);

    Entity getEntityTypeFromId(String entityid, String sourcemodelid);

}
