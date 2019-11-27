package com.typhon.evolutiontool.utils;

import com.typhon.evolutiontool.entities.CardinalityDO;
import com.typhon.evolutiontool.entities.EntityDO;
import com.typhon.evolutiontool.entities.RelationDO;
import com.typhon.evolutiontool.entities.RelationDOImpl;
import typhonml.AddRelation;
import typhonml.Entity;
import typhonml.Relation;

public class RelationDOFactory {

    private RelationDOFactory() {
    }

    public static RelationDO buildInstance(Relation relation, boolean initialized) {
        if (relation != null) {
            Entity sourceEntity = null;
            if (relation instanceof AddRelation) {
                sourceEntity = ((AddRelation) relation).getOwnerEntity();
            } else if (relation.eContainer() instanceof Entity) {
                sourceEntity = (Entity) relation.eContainer();
            }
            EntityDO sourceEntityDO = null;
            EntityDO targetEntityDO = null;
            RelationDO oppositeRelationDO = null;
            if (!initialized) {
                sourceEntityDO = sourceEntity != null ? EntityDOFactory.buildInstance(sourceEntity, true) : null;
                Entity targetEntity = relation.getType();
                targetEntityDO = targetEntity != null ? EntityDOFactory.buildInstance(targetEntity, true) : null;
                oppositeRelationDO = null;
                if (relation.getOpposite() != null) {
                    oppositeRelationDO = buildInstance(relation.getOpposite(), false);
                }
            }
            boolean isContainment = relation.getIsContainment() != null ? relation.getIsContainment() : false;
            CardinalityDO cardinalityDO = null;
            if (relation.getCardinality() != null) {
                cardinalityDO = CardinalityDO.get(relation.getCardinality().getValue());
            }
            return new RelationDOImpl(relation.getName(), relation.getType().getName(), sourceEntityDO, targetEntityDO, oppositeRelationDO, isContainment, cardinalityDO);
        }
        return null;
    }
}
