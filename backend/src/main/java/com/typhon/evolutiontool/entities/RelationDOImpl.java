package com.typhon.evolutiontool.entities;

public class RelationDOImpl implements RelationDO {

    private String relationName;
    private String typeName;
    private EntityDO sourceEntityDO;
    private EntityDO targetEntityDO;
    private RelationDO oppositeRelationDO;
    private boolean isContainment;
    private CardinalityDO cardinalityDO;

    public RelationDOImpl(String relationName, String typeName, EntityDO sourceEntityDO, EntityDO targetEntityDO, RelationDO oppositeRelationDO, boolean isContainment, CardinalityDO cardinalityDO) {
        this.relationName = relationName;
        this.typeName = typeName;
        this.sourceEntityDO = sourceEntityDO;
        this.targetEntityDO = targetEntityDO;
        this.oppositeRelationDO = oppositeRelationDO;
        this.isContainment = isContainment;
        this.cardinalityDO = cardinalityDO;
    }

    @Override
    public String getName() {
        return this.relationName;
    }

    @Override
    public String getTypeName() {
        return this.typeName;
    }

    @Override
    public EntityDO getSourceEntity() {
        return this.sourceEntityDO;
    }

    @Override
    public EntityDO getTargetEntity() {
        return this.targetEntityDO;
    }

    @Override
    public RelationDO getOpposite() {
        return this.oppositeRelationDO;
    }

    @Override
    public boolean isContainment() {
        return this.isContainment;
    }

    @Override
    public CardinalityDO getCardinality() {
        return this.cardinalityDO;
    }

}
