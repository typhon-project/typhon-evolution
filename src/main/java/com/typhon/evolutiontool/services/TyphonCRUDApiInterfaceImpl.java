package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("typhoncrudapi")
public class TyphonCRUDApiInterfaceImpl implements TyphonInterface {

    Logger logger = LoggerFactory.getLogger(TyphonCRUDApiInterfaceImpl.class);
    private String typhonMLTargetModel;

    @Override
    public String createEntity(Entity newEntity) {
        logger.info("Create entity [{}] via TyphonCRUD API", newEntity.getEntityName());
        return "Call to CRUD API on TyphonML model : "+this.typhonMLTargetModel;
    }

    @Override
    public void setTyphonMLTargetModel(String targetmodel) {
        this.typhonMLTargetModel = targetmodel;
    }

    @Override
    public void renameEntity(String oldEntityName, String newEntityName) {
        logger.info("Rename Entity [{}] to [{}] via TyphonCRUD API", oldEntityName, newEntityName);
    }
}
