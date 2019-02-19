package com.typhon.evolutiontool.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class Entity {

    @JsonProperty("name")
    private String entityName;
    @JsonProperty("attributes")
    private Map<String,String> attributes;

    public Entity() {
    }

    public Entity(String entityName) {
        this.entityName = entityName;
    }

    public void addAttribute(String name, String datatype) {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        attributes.put(name, datatype);
    }

    @Override
    public String toString() {
        return "Entity{" +
                "entityName='" + entityName + '\'' +
                ", attributes=" + attributes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return Objects.equals(entityName, entity.entityName) &&
                Objects.equals(attributes, entity.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityName, attributes);
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }


    public Map<String,String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String,String> attributes) {
        this.attributes = attributes;
    }
}
