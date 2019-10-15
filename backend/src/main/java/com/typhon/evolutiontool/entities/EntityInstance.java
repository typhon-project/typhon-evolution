package com.typhon.evolutiontool.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/*
Implementation of EntityDO data contained in WorkingSet. Following D4.2
 */
public class EntityInstance {

    @JsonProperty("name")
    private Object id;
    @JsonProperty("attributes")
    private Map<String,Object> attributes;

    public EntityInstance() {
    }

    public EntityInstance(String id) {
        this.id = id;
    }

    public void addAttribute(String name, String datatype) {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        attributes.put(name, datatype);
    }

    @Override
    public String toString() {
        return "EntityDO{" +
                "id='" + id + '\'' +
                ", attributes=" + attributes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityInstance entity = (EntityInstance) o;
        return Objects.equals(id, entity.id) &&
                Objects.equals(attributes, entity.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, attributes);
    }

    public boolean sameAttributes(EntityInstance e) {
        return Objects.equals(this.attributes, e.attributes);
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }


    public Map<String,Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String,Object> attributes) {
        this.attributes = attributes;
    }
}
