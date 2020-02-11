package com.typhon.evolutiontool.entities;

public interface RelationDO {

    String getName();

    String getTypeName();

    void setTypeName(String typeName);

    EntityDO getSourceEntity();

    EntityDO getTargetEntity();

    RelationDO getOpposite();

    boolean isContainment();

    CardinalityDO getCardinality();
}
