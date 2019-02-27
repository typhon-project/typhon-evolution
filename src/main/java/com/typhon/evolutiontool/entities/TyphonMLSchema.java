package com.typhon.evolutiontool.entities;

import org.springframework.data.annotation.Id;

import java.util.List;

public class TyphonMLSchema {

    @Id
    private String version;
    private List<Database> databases;
    private List<Entity> entities;

    public TyphonMLSchema() {
    }

    public TyphonMLSchema(String typhonMLVersion) {
        this.version = typhonMLVersion;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Database> getDatabases() {
        return databases;
    }

    public void setDatabases(List<Database> databases) {
        this.databases = databases;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }
}
