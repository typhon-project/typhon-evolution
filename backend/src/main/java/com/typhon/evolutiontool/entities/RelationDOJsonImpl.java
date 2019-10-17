package main.java.com.typhon.evolutiontool.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class RelationDOJsonImpl implements RelationDO {

    private String name;
    @JsonDeserialize(as=EntityDOJsonImpl.class)
    private EntityDO sourceEntity;
    @JsonDeserialize(as=EntityDOJsonImpl.class)
    private EntityDO targetEntity;
    private RelationDO opposite;
    private boolean containment;
    private CardinalityDO cardinality;

    public RelationDOJsonImpl(String name, EntityDO sourceEntity, EntityDO targetEntity, RelationDO opposite, boolean containment, CardinalityDO cardinality) {
        this.name = name;
        this.sourceEntity = sourceEntity;
        this.targetEntity = targetEntity;
        this.opposite = opposite;
        this.containment = containment;
        this.cardinality = cardinality;
    }

    public RelationDOJsonImpl() {
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public EntityDO getSourceEntity() {
        return sourceEntity;
    }

    @Override
    public EntityDO getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(EntityDO targetEntity) {
        this.targetEntity = targetEntity;
    }

    @Override
    public RelationDO getOpposite() {
        return opposite;
    }

    public void setOpposite(RelationDO opposite) {
        this.opposite = opposite;
    }

    @Override
    public boolean isContainment() {
        return containment;
    }

    public void setContainment(boolean containment) {
        this.containment = containment;
    }

    @Override
    public CardinalityDO getCardinality() {
        return cardinality;
    }

    public void setCardinality(CardinalityDO cardinality) {
        this.cardinality = cardinality;
    }

    @Override
    public String toString() {
        return "RelationDO{" +
                "name='" + name + '\'' +
                ", sourceEntity=" + sourceEntity.getName() +
                ", targetEntity=" + targetEntity.getName() +
                ", opposite=" + opposite +
                ", containment=" + containment +
                ", cardinality=" + cardinality +
                '}';
    }
}
