package com.typhon.evolutiontool.dummy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.services.TyphonInterface;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("fakeimplementation")
public class DummyImplementation implements TyphonInterface, TyphonQLConnection, TyphonMLInterface, TyphonDLInterface {

    Logger logger = LoggerFactory.getLogger(DummyImplementation.class);
    ObjectMapper mapper = new ObjectMapper();
    WorkingSet workingSetData = new WorkingSetDummyImpl();
    boolean isTyphonQL = true;

    public DummyImplementation() {

    }


    @Override
    public String createEntity(Entity newEntity, String typhonMLVersion) {
        String tql="ON ["+typhonMLVersion+"] TyphonQL CREATE ENTITY "+newEntity.getId()+" {"+newEntity.getAttributes().entrySet().stream().map(entry -> entry.getKey()+" "+entry.getValue()).collect(Collectors.joining(","))+"}";
        executeTyphonQLDDL(tql);
        return "Entity ["+newEntity.getId()+"] created";
    }

    @Override
    public void renameEntity(String oldEntityName, String newEntityName, String typhonMLVersion) {
        String tql = "ON ["+typhonMLVersion+"] TyphonQL RENAME ENTITY "+ oldEntityName +" TO "+ newEntityName;
        executeTyphonQLDDL(tql);
    }

    @Override
    public WorkingSet readEntityData(Entity entity, String typhonMLVersion) {
        try {
            query("ON ["+typhonMLVersion+"] from ? e select e " + entity.getId());
            workingSetData.setRows(mapper.readerFor(LinkedHashMap.class).readValue(new File("src/main/resources/test/"+typhonMLVersion+"_WorkingSetData.json")));
            WorkingSet workingSet = new WorkingSetDummyImpl();
            LinkedHashMap<String, List<Entity>> data = new LinkedHashMap();
            data.put(entity.getId(), workingSetData.rows().get(entity.getId()));
            workingSet.setRows(data);
            return workingSet;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workingSetData;
    }

    @Override
    public void writeWorkingSetData(WorkingSet workingSetDataToInsert, String typhonMLVersion) {
        query("ON ["+typhonMLVersion+"] insert " + workingSetDataToInsert);
    }

    @Override
    public boolean isDatabaseRunning(String databasetype, String databasename) {
        TyphonDatabase typhonDatabase = new TyphonDatabase(databasetype, databasename);
        logger.info("Checking that the database is runnning [{} - {}]",databasetype, databasename);
        try {
            TyphonDLSchema typhonDLSchema = mapper.readerFor(TyphonDLSchema.class).readValue(new File("C:\\Users\\Admin\\Documents\\IdeaProjects\\typhon\\src\\main\\resources\\test\\TyphonDL_Current.json"));
            if(typhonDLSchema.getRunningdb().contains(typhonDatabase))
                return true;
            else
                return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void createDatabase(String databasetype, String databasename) {
        TyphonDatabase typhonDatabase = new TyphonDatabase(databasetype, databasename);
        logger.info("Creating the database [{} - {}]", databasetype, databasename);
        try {
            TyphonDLSchema typhonDLSchema = mapper.readerFor(TyphonDLSchema.class).readValue(new File("C:\\Users\\Admin\\Documents\\IdeaProjects\\typhon\\src\\main\\resources\\test\\TyphonDL_Current.json"));
            typhonDLSchema.getRunningdb().add(typhonDatabase);
            mapper.writerFor(TyphonDLSchema.class).writeValue(new File("C:\\Users\\Admin\\Documents\\IdeaProjects\\typhon\\src\\main\\resources\\test\\TyphonDL_Current.json"), typhonDLSchema);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void setNewTyphonMLModel(String newModelIdentifier) {
        logger.info("Setting current TyphonML to [{}]", newModelIdentifier);
        try {
            TyphonMLSchema typhonMLSchema = mapper.readerFor(TyphonMLSchema.class).readValue((new File("C:\\Users\\Admin\\Documents\\IdeaProjects\\typhon\\src\\main\\resources\\test\\"+newModelIdentifier+".json")));
            mapper.writerFor(TyphonMLSchema.class).writeValue(new File("C:\\Users\\Admin\\Documents\\IdeaProjects\\typhon\\src\\main\\resources\\test\\TyphonML_Current.json"), typhonMLSchema);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Entity getEntityTypeFromId(String entityid, String sourcemodelid) {
        logger.info("Retrieving Entity object of [{}] from model [{}]", entityid, sourcemodelid);
        Entity entity;
        try {
            TyphonMLSchema typhonMLSchema = mapper.readerFor(TyphonMLSchema.class).readValue((new File("C:\\Users\\Admin\\Documents\\IdeaProjects\\typhon\\src\\main\\resources\\test\\" + sourcemodelid + ".json")));
            entity = typhonMLSchema.getEntityFromName(entityid);
            return entity;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String executeTyphonQLDDL(String tqlDDL) {
        logger.info("Executing TyphonQL DDL command [{}]",tqlDDL);
        writeQueryTofile(tqlDDL);
        return null;
    }

    @Override
    public WorkingSet query(String queryString, Object... params) {
        logger.info("Executing TyphonQL DML command [{}]",queryString);
        writeQueryTofile(queryString + params+"\n");
        return null;
    }

    @Override
    public WorkingSet query(String queryString, Map<String, Object> params) {
        return null;
    }

    @Override
    public WorkingSet update(WorkingSet ws) {
        return null;
    }

    @Override
    public WorkingSet insert(WorkingSet ws) {
        return null;
    }

    @Override
    public WorkingSet delete(WorkingSet ws) {
        return null;
    }


    private void writeQueryTofile(String tql) {
        try {
            Path path = Paths.get("C:\\Users\\Admin\\Documents\\IdeaProjects\\typhon\\src\\main\\resources\\test\\GeneratedTyphonQLQueries.txt");
            Files.write(path, tql.getBytes(), StandardOpenOption.APPEND);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTyphonQL(boolean typhonQL) {
        isTyphonQL = typhonQL;
    }

    public WorkingSet getWorkingSetData() {
        return workingSetData;
    }

    public void setWorkingSetData(WorkingSet workingSetData) {
        this.workingSetData = workingSetData;
    }
}
