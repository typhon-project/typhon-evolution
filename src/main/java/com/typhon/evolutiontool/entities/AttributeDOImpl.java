package com.typhon.evolutiontool.entities;

public class AttributeDOImpl implements AttributeDO {

    private String name;
    private String datatypeName;

    public AttributeDOImpl(String name, String datatypeName) {
        this.name = name;
        this.datatypeName = datatypeName;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDataTypeString() {
        return datatypeName;
    }
}
