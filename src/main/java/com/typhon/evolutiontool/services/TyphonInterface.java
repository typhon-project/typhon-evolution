package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.Entity;
import com.typhon.evolutiontool.entities.WorkingSet;

/*
    This interface specifies the operations that needs to be performed on the Typhon polystore in order to apply a Schema Modification Operator. Either modification of structure or of data.
 */
public interface TyphonInterface {

    String createEntity(Entity newEntity, String typhonMLVersion);

    void renameEntity(String oldEntityName, String newEntityName, String typhonMLVersion);

    WorkingSet readEntityData(Entity entity);

    void writeWorkingSetData(WorkingSet workingSetData);
}
