package com.typhon.evolutiontool.entities;

public class AttributeJsonImpl implements AttributeDO {
    private String name;
    private String datatype;

    public AttributeJsonImpl() {
    }

    public String getName() {
        return name;
    }

    @Override
    public String getDataTypeString() {
        return datatype;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }
}
