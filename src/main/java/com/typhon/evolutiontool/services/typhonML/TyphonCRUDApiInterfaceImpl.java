package com.typhon.evolutiontool.services.typhonML;

import com.typhon.evolutiontool.entities.Entity;
import com.typhon.evolutiontool.entities.WorkingSet;
import com.typhon.evolutiontool.services.TyphonInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/*
    This class implements @see TyphonInterface using the TyphonML module CRUD API.
 */
@Component("typhoncrudapi")
public class TyphonCRUDApiInterfaceImpl implements TyphonInterface {

    Logger logger = LoggerFactory.getLogger(TyphonCRUDApiInterfaceImpl.class);

    @Override
    public String createEntity(Entity newEntity, String typhonMLVersion) {
        logger.info("Create entity [{}] via TyphonCRUD API on typhon", newEntity.getId());
        return "Call to CRUD API on TyphonML model : "+typhonMLVersion;
    }

    @Override
    public void renameEntity(String oldEntityName, String newEntityName, String typhonMLVersion) {
        logger.info("Rename Entity [{}] to [{}] via TyphonCRUD API on typhonML version [{}]", oldEntityName, newEntityName, typhonMLVersion);
    }

    @Override
    public WorkingSet readEntityData(Entity entity, String typhonMLVersion) {
        throw new NotImplementedException();
    }

    @Override
    public void writeWorkingSetData(WorkingSet workingSetData, String typhonMLVersion) {
        throw new NotImplementedException();
    }
}
