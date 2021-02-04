package com.typhon.evolutiontool.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class EntityInstance {

    private String uuid;
    private Map<String, Object> attributes;
    private Map<String, Set<String>> relations;

    public EntityInstance(String uuid) {
        this.uuid = uuid;
    }

    public Object getAttribute(String name) {
        if (attributes == null) {
            return null;
        }
        return attributes.get(name);
    }

    public void addAttribute(String attributeName, Object attributeValue) {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        attributes.put(attributeName, attributeValue);
    }

    public Object getRelation(String name) {
        if (relations == null) {
            return null;
        }
        return relations.get(name);
    }

    public void addRelation(String relationName, Set<String> relationValue) {
        if (relations == null) {
            relations = new HashMap<>();
        }
        relations.put(relationName, relationValue);
    }

    public boolean sameAttributes(EntityInstance entityInstance) {
        return Objects.equals(this.attributes, entityInstance.attributes);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Set<String>> getRelations() {
        return relations;
    }

    public void setRelations(Map<String, Set<String>> relations) {
        this.relations = relations;
    }

    @Override
    public String toString() {
        return "EntityDO{\n" +
                "uuid='" + getUuid() + "'\n" +
                ", attributes='" + getAttributes() + "'\n" +
                ", relations='" + getRelations() + "'\n" +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityInstance entity = (EntityInstance) o;
        return Objects.equals(getUuid(), entity.getUuid()) &&
                Objects.equals(getAttributes(), entity.getAttributes()) &&
                Objects.equals(getRelations(), entity.getRelations());
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, attributes, relations);
    }

}
