package com.typhon.evolutiontool.services.typhonQL;

import com.typhon.evolutiontool.entities.Entity;
import com.typhon.evolutiontool.entities.TyphonMLSchema;
import com.typhon.evolutiontool.entities.WorkingSet;
import com.typhon.evolutiontool.services.TyphonInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component("typhonql")
public class TyphonInterfaceQLImpl implements TyphonInterface {

    Logger logger = LoggerFactory.getLogger(TyphonInterfaceQLImpl.class);
    private TyphonQLConnection typhonQLConnection;


    public TyphonInterfaceQLImpl() {

    }

    @Override
    public String createEntity(Entity newEntity, String typhonMLVersion) {
        String tql;
        logger.info("Create entity [{}] via TyphonQL DDL query on TyphonML model [{}] ", newEntity.getId(),typhonMLVersion);
        tql="TyphonQL CREATE ENTITY "+newEntity.getId()+" {"+newEntity.getAttributes().entrySet().stream().map(entry -> entry.getKey()+" "+entry.getValue()).collect(Collectors.joining(","))+"}";
        getTyphonQLConnection(typhonMLVersion).executeTyphonQLDDL(tql);
        return tql;
    }

    private TyphonQLConnection getTyphonQLConnection(String typhonMLVersion) {
        //TODO Investigate String vs Object typhonML schema.
        return TyphonQLConnection.newEngine(new TyphonMLSchema(typhonMLVersion));
    }

    @Override
    public void renameEntity(String oldEntityName, String newEntityName, String typhonMLVersion) {
        logger.info("Rename Entity [{}] to [{}] via TyphonQL on TyphonML model [{}]", oldEntityName, newEntityName, typhonMLVersion);
        String tql = "TyphonQL RENAME ENTITY "+ oldEntityName +" TO "+ newEntityName;
        getTyphonQLConnection(typhonMLVersion).executeTyphonQLDDL(tql);

    }

    @Override
    public WorkingSet readEntityData(Entity entity, String typhonMLVersion) {
        return getTyphonQLConnection(typhonMLVersion).query("from ? e select e", entity.getId());
    }

    @Override
    public void writeWorkingSetData(WorkingSet workingSetData, String typhonMLVersion) {
        getTyphonQLConnection(typhonMLVersion).insert(workingSetData);
    }

}
