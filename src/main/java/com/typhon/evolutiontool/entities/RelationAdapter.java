package com.typhon.evolutiontool.entities;

import com.typhon.evolutiontool.utils.EntityDOFactory;
import typhonml.Entity;
import typhonml.Relation;

public class RelationAdapter implements RelationDO {

    private Relation relation;

    public RelationAdapter(Relation relation) {
        this.relation=relation;
    }

    @Override
    public String getName() {
        return relation.getName();
    }

    @Override
    public EntityDO getSourceEntity() {
        return EntityDOFactory.createEntityDOFromEntityML((Entity) relation.eContainer());
    }

    @Override
    public EntityDO getTargetEntity() {
        return EntityDOFactory.createEntityDOFromEntityML(relation.getType());
    }

    @Override
    public RelationDO getOpposite() {
        return null;
    }

    @Override
    public boolean isContainment() {
        return relation.getIsContainment();
    }

    @Override
    public CardinalityDO getCardinality() {
        return CardinalityDO.valueOf(relation.getCardinality().getLiteral());
    }

}
