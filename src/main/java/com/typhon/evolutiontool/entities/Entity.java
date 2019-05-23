package com.typhon.evolutiontool.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class Entity {

    @Id
    @JsonProperty("name")
    private String name;
    @JsonProperty("attributes")
    private Map<String,Object> attributes;
    private String identifier;


    public Entity() {
    }

    public Entity(String name) {
        this.name = name;
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
                "name='" + name + '\'' +
                ", attributes=" + attributes +
                ", identifier='" + identifier + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return Objects.equals(name, entity.name) &&
                Objects.equals(attributes, entity.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, attributes);
    }

    public boolean sameAttributes(Entity e) {
        return Objects.equals(this.attributes, e.attributes);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Map<String,Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String,Object> attributes) {
        this.attributes = attributes;
    }
}
