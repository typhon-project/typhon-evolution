package com.typhon.evolutiontool.entities;

import typhonml.Attribute;
import typhonml.Entity;

import java.util.HashMap;
import java.util.Map;

public class EntityDOAdapter implements EntityDO {

    private Entity entityML;
    private Map<String,Object> attributes = new HashMap<>();

    public EntityDOAdapter(Entity entityML) {
        this.entityML = entityML;
        for (Attribute a : entityML.getAttributes()) {
            attributes.put(a.getName(), a.getType());
        }
    }

    @Override
    public void addAttribute(String name, String datatype) {
        attributes.put(name, datatype);
    }

    @Override
    public String getName() {
        return entityML.getName();
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getIdentifier() {
        //TODO Correct implementation.
        return entityML.getIdentifer().getAttributes().get(0).getName();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

}
