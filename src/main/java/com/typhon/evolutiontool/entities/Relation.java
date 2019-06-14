package com.typhon.evolutiontool.entities;

public class Relation {

    private String name;
    private EntityDO sourceEntity;
    private EntityDO targetEntity;
    private Relation opposite;
    private boolean containment;
    private Cardinality cardinality;

    public Relation(String name, EntityDO sourceEntity, EntityDO targetEntity, Relation opposite, boolean containment, Cardinality cardinality) {
        this.name = name;
        this.sourceEntity = sourceEntity;
        this.targetEntity = targetEntity;
        this.opposite = opposite;
        this.containment = containment;
        this.cardinality = cardinality;
    }

    public Relation() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EntityDO getSourceEntity() {
        return sourceEntity;
    }

    public void setSourceEntity(EntityDO sourceEntity) {
        this.sourceEntity = sourceEntity;
    }

    public EntityDO getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(EntityDO targetEntity) {
        this.targetEntity = targetEntity;
    }

    public Relation getOpposite() {
        return opposite;
    }

    public void setOpposite(Relation opposite) {
        this.opposite = opposite;
    }

    public boolean isContainment() {
        return containment;
    }

    public void setContainment(boolean containment) {
        this.containment = containment;
    }

    public Cardinality getCardinality() {
        return cardinality;
    }

    public void setCardinality(Cardinality cardinality) {
        this.cardinality = cardinality;
    }
}
