package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
    This class implements @see TyphonInterface using the TyphonML module CRUD API.
 */
@Component("typhoncrudapi")
public class TyphonCRUDApiInterfaceImpl implements TyphonInterface {

    Logger logger = LoggerFactory.getLogger(TyphonCRUDApiInterfaceImpl.class);

    @Override
    public String createEntity(Entity newEntity, String typhonMLVersion) {
        logger.info("Create entity [{}] via TyphonCRUD API on typhon", newEntity.getEntityName());
        return "Call to CRUD API on TyphonML model : "+typhonMLVersion;
    }

    @Override
    public void renameEntity(String oldEntityName, String newEntityName, String typhonMLVersion) {
        logger.info("Rename Entity [{}] to [{}] via TyphonCRUD API on typhonML version [{}]", oldEntityName, newEntityName, typhonMLVersion);
    }
}
