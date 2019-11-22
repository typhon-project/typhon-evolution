package com.typhon.evolutiontool.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityDOImpl implements EntityDO {

    private String name;
    private String namespace;
    private List<RelationDO> relations;
    private String entityIdentifier;
    private Map<String, DataTypeDO> attributes;

    public EntityDOImpl() {
        this.relations = new ArrayList<>();
        this.attributes = new HashMap<>();
    }

    public EntityDOImpl(String name, String namespace, List<RelationDO> relations, String entityIdentifier, Map<String, DataTypeDO> attributes) {
        this.name = name;
        this.namespace = namespace;
        this.relations = relations;
        this.entityIdentifier = entityIdentifier;
        this.attributes = attributes;
    }

    @Override
    public void addAttribute(String name, DataTypeDO dataTypeDO) {
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }
        this.attributes.put(name, dataTypeDO);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getIdentifier() {
        //TODO New TyphonML does not support user defined identifier anymore. To adapt.
        return null;
    }

    @Override
    public Map<String, DataTypeDO> getAttributes() {
        return this.attributes;
    }

    public String getNamespace() {
        return namespace;
    }

    @Override
    public List<RelationDO> getRelations() {
        return relations;
    }

    public String getEntityIdentifier() {
        return entityIdentifier;
    }

}
