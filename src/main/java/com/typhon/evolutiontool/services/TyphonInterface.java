package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.Entity;
import com.typhon.evolutiontool.entities.WorkingSet;

/*
    This interface specifies the operations that needs to be performed on the Typhon polystore in order to apply a Schema Modification Operator. Either modification of structure or of data.
 */
public interface TyphonInterface {

    String createEntityType(Entity newEntity, String typhonMLVersion);

    void renameEntity(String oldEntityName, String newEntityName, String typhonMLVersion);

    WorkingSet readAllEntityData(Entity entity, String typhonMLVersion);

    WorkingSet readAllEntityData(String entityId, String typhonMLVersion);

    void writeWorkingSetData(WorkingSet workingSetData, String typhonMLVersion);

    WorkingSet deleteAllEntityData(String entityid, String typhonMLVersion);

    void deleteEntityStructure(String entityname, String typhonMLVersion);
}
