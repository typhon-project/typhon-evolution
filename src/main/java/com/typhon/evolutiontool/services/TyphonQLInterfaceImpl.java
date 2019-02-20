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
    private TyphonQLConnection typhonQLConnection;

    public TyphonQLInterfaceImpl() {
    }

    @Autowired
    public TyphonQLInterfaceImpl(TyphonQLConnection typhonQLConnection) {
        this.typhonQLConnection = typhonQLConnection;
    }

    @Override
    public String createEntity(Entity newEntity, String typhonMLVersion) {
        String tql;
        logger.info("Create entity [{}] via TyphonQL DDL query on TyphonML model [{}] ", newEntity.getEntityName(),typhonMLVersion);
        tql="TyphonQL CREATE ENTITY "+newEntity.getEntityName()+" {"+newEntity.getAttributes().entrySet().stream().map(entry -> entry.getKey()+" "+entry.getValue()).collect(Collectors.joining(","))+"}";
        typhonQLConnection.executeTyphonQLDDL(tql,typhonMLVersion);
        return tql;
    }

    @Override
    public void renameEntity(String oldEntityName, String newEntityName, String typhonMLVersion) {
        logger.info("Rename Entity [{}] to [{}] via TyphonQL on TyphonML model [{}]", oldEntityName, newEntityName, typhonMLVersion);
        String tql = "TyphonQL RENAME ENTITY "+ oldEntityName +" TO "+ newEntityName;
        typhonQLConnection.executeTyphonQLDDL(tql,typhonMLVersion);

    }

}
