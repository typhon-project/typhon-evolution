package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.Entity;

/*
    This interface specifies the operations that needs to be performed on the Typhon polystore in order to apply a Schema Modification Operator. Either modification of structure or of data.
 */
public interface TyphonInterface {

    String createEntity(Entity newEntity);

    void setTyphonMLTargetModel(String targetmodel);

    void renameEntity(String oldEntityName, String newEntityName);
}
