package com.typhon.evolutiontool.entities;

public interface RelationDO {

    String getName();

    EntityDO getSourceEntity();

    EntityDO getTargetEntity();

    RelationDO getOpposite();

    boolean isContainment();

    CardinalityDO getCardinality();
}
