package com.typhon.evolutiontool.entities;

public interface RelationDO {

    String getName();

    String getTypeName();

    EntityDO getSourceEntity();

    EntityDO getTargetEntity();

    RelationDO getOpposite();

    boolean isContainment();

    CardinalityDO getCardinality();
}
