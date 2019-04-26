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


    private TyphonQLConnection getTyphonQLConnection(String typhonMLVersion) {
        //TODO Investigate String vs Object typhonML schema.
        return TyphonQLConnection.newEngine(new TyphonMLSchema(typhonMLVersion));
    }

    @Override
    public String createEntityType(Entity newEntity, String typhonMLVersion) {
        String tql;
        logger.info("Create entity [{}] via TyphonQL DDL query on TyphonML model [{}] ", newEntity.getId(),typhonMLVersion);
        tql="TQLDDL CREATE ENTITY "+newEntity.getId()+" {"+newEntity.getAttributes().entrySet().stream().map(entry -> entry.getKey()+" "+entry.getValue()).collect(Collectors.joining(","))+"}";
        getTyphonQLConnection(typhonMLVersion).executeTyphonQLDDL(tql);
        return tql;
    }

    @Override
    public void renameEntity(String oldEntityName, String newEntityName, String typhonMLVersion) {
        logger.info("Rename Entity [{}] to [{}] via TyphonQL on TyphonML model [{}]", oldEntityName, newEntityName, typhonMLVersion);
        String tql = "TQL DDL RENAME ENTITY "+ oldEntityName +" TO "+ newEntityName;
        getTyphonQLConnection(typhonMLVersion).executeTyphonQLDDL(tql);

    }

    @Override
    public WorkingSet readAllEntityData(Entity entity, String typhonMLVersion) {
        return getTyphonQLConnection(typhonMLVersion).query("from ? e select e", entity.getId());
    }

    @Override
    public WorkingSet readAllEntityData(String entityId, String typhonMLVersion) {
        return getTyphonQLConnection(typhonMLVersion).query("from ? e select e", entityId);
    }

    @Override
    public void writeWorkingSetData(WorkingSet workingSetData, String typhonMLVersion) {
        getTyphonQLConnection(typhonMLVersion).insert(workingSetData);
    }

    @Override
    public WorkingSet deleteAllEntityData(String entityid, String typhonMLVersion) {
        return getTyphonQLConnection(typhonMLVersion).delete(this.readAllEntityData(entityid, typhonMLVersion));
    }

    @Override
    public void deleteEntityStructure(String entityname, String typhonMLVersion) {
        String tql = "TQLDDL DELETE ENTITY " + entityname + " on TyphonML [" + typhonMLVersion + "]";
        logger.info("Delete entity [{}] via TyphonQL DDL on TyphonML model [{}] ", entityname, typhonMLVersion);
        getTyphonQLConnection(typhonMLVersion).executeTyphonQLDDL(tql);
    }

}
