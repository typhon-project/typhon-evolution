package com.typhon.evolutiontool.utils;

import com.typhon.evolutiontool.entities.CardinalityDO;
import com.typhon.evolutiontool.entities.EntityDO;
import com.typhon.evolutiontool.entities.RelationDO;
import com.typhon.evolutiontool.entities.RelationDOImpl;
import typhonml.Entity;
import typhonml.Relation;

public class RelationDOFactory {

    private RelationDOFactory() {
    }

    public static RelationDO buildInstance(Relation relation) {
        Entity sourceEntity = (Entity) relation.eContainer();
        EntityDO sourceEntityDO = sourceEntity != null ? EntityDOFactory.buildInstance(sourceEntity) : null;
        Entity targetEntity = relation.getType();
        EntityDO targetEntityDO = targetEntity != null ? EntityDOFactory.buildInstance(targetEntity) : null;
        RelationDO oppositeRelationDO = null;
        if (relation.getOpposite() != null) {
            oppositeRelationDO = buildInstance(relation.getOpposite());
        }
        CardinalityDO cardinalityDO = null;
        if (relation.getCardinality() != null) {
            cardinalityDO = CardinalityDO.get(relation.getCardinality().getValue());
        }
        return new RelationDOImpl(relation.getName(), sourceEntityDO, targetEntityDO, oppositeRelationDO, relation.getIsContainment(), cardinalityDO);
    }
}
