package com.typhon.evolutiontool.entities;

public class Relation {

    private String name;
    private Entity sourceEntity;
    private Entity targetEntity;
    private Relation opposite;
    private boolean containment;
    private Cardinality cardinality;

    public Relation(String name, Entity sourceEntity, Entity targetEntity, Relation opposite, boolean containment, Cardinality cardinality) {
        this.name = name;
        this.sourceEntity = sourceEntity;
        this.targetEntity = targetEntity;
        this.opposite = opposite;
        this.containment = containment;
        this.cardinality = cardinality;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Entity getSourceEntity() {
        return sourceEntity;
    }

    public void setSourceEntity(Entity sourceEntity) {
        this.sourceEntity = sourceEntity;
    }

    public Entity getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(Entity targetEntity) {
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
