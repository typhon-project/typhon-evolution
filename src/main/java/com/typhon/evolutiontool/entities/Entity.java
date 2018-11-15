package com.typhon.evolutiontool.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;

public class Entity {

    private String entity;
    @JsonProperty("databasetype")
    private String databaseType;
    @JsonProperty("databasemappingname")
    private String databaseMappingName;
    private String id;
    private JsonNode attributes;

    public Entity() {
    }

    @Override
    public String toString() {
        return "Entity{" +
                "entity='" + entity + '\'' +
                ", databaseType='" + databaseType + '\'' +
                ", databaseMappingName='" + databaseMappingName + '\'' +
                ", id='" + id + '\'' +
                ", attributes=" + attributes +
                '}';
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public String getDatabaseMappingName() {
        return databaseMappingName;
    }

    public void setDatabaseMappingName(String databaseMappingName) {
        this.databaseMappingName = databaseMappingName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JsonNode getAttributes() {
        return attributes;
    }

    public void setAttributes(JsonNode attributes) {
        this.attributes = attributes;
    }
}
