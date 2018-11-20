package com.typhon.evolutiontool.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class Entity {

    @JsonProperty("entity")
    private String entityName;
    @JsonProperty("databasetype")
    private String databaseType;
    @JsonProperty("databasemappingname")
    private String databaseMappingName;
    @JsonProperty("id")
    private String entityId;
    private JsonNode attributes;

    public Entity() {
    }

    @Override
    public String toString() {
        return "Entity{" +
                "entityName='" + entityName + '\'' +
                ", databaseType='" + databaseType + '\'' +
                ", databaseMappingName='" + databaseMappingName + '\'' +
                ", entityId='" + entityId + '\'' +
                ", attributes=" + attributes +
                '}';
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
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

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public JsonNode getAttributes() {
        return attributes;
    }

    public void setAttributes(JsonNode attributes) {
        this.attributes = attributes;
    }
}
