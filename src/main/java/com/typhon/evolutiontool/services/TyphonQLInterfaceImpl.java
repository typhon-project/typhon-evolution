package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component("typhonql")
public class TyphonQLInterfaceImpl implements TyphonInterface {

    Logger logger = LoggerFactory.getLogger(TyphonQLInterfaceImpl.class);
    private String typhonMLTargetModel;
    private TyphonQLConnection typhonQLConnection;

    public TyphonQLInterfaceImpl() {
    }

    @Autowired
    public TyphonQLInterfaceImpl(TyphonQLConnection typhonQLConnection) {
        this.typhonQLConnection = typhonQLConnection;
    }

    @Override
    public String createEntity(Entity newEntity) {
        String tql;
        logger.info("Create entity [{}] via TyphonQL DDL query on TyphonML model [{}] ", newEntity.getEntityName(),this.typhonMLTargetModel);
        tql="TyphonQL CREATE ENTITY "+newEntity.getEntityName()+" {"+newEntity.getAttributes().entrySet().stream().map(entry -> entry.getKey()+" "+entry.getValue()).collect(Collectors.joining(","))+"}";
        typhonQLConnection.executeTyphonQLDDL(tql);
        return tql;
    }

    @Override
    public void setTyphonMLTargetModel(String targetmodel) {
        this.typhonMLTargetModel = targetmodel;
    }

    @Override
    public void renameEntity(String oldEntityName, String newEntityName) {
        logger.info("Rename Entity [{}] to [{}] via TyphonQL", oldEntityName, newEntityName);
        String tql = "TyphonQL RENAME ENTITY "+ oldEntityName +" TO "+ newEntityName;
    }

}
