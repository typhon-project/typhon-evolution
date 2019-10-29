package main.java.com.typhon.evolutiontool.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class EntityDOJsonImpl implements EntityDO {

    @JsonProperty("name")
    private String name;
    @JsonProperty("attributes")
    private Map<String,Object> attributes;
    private String identifier;


    public EntityDOJsonImpl() {
    }

    public EntityDOJsonImpl(String name) {
        this.name = name;
    }

    @Override
    public void addAttribute(String name, String datatype) {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        attributes.put(name, datatype);
    }

    @Override
    public String toString() {
        return "EntityDO{" +
                "name='" + name + '\'' +
                ", attributes=" + attributes +
                ", identifier='" + identifier + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityDOJsonImpl entity = (EntityDOJsonImpl) o;
        return Objects.equals(name, entity.name) &&
                Objects.equals(attributes, entity.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, attributes);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Map<String,Object> getAttributes() {
        return attributes;
    }

}
