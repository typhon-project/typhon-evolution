package com.typhon.evolutiontool.entities;

import typhonml.Attribute;

public class AttributeDOAdapter implements AttributeDO {

    String name;
    String datatypename;

    public AttributeDOAdapter(Attribute attribute) {
        this.name = attribute.getName();
        this.datatypename = attribute.getType().getName();
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDataTypeString() {
        return datatypename;
    }
}
