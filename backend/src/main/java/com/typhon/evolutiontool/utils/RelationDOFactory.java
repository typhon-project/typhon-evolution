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

    public static RelationDO buildInstance(Relation relation, boolean isOppositeRelationInitialized) {
        if (relation != null) {
            Entity sourceEntity = null;
            if (relation instanceof AddRelation) {
                sourceEntity = ((AddRelation) relation).getOwnerEntity();
            } else if (relation.eContainer() instanceof Entity) {
                sourceEntity = (Entity) relation.eContainer();
            }
            EntityDO sourceEntityDO = sourceEntity != null ? EntityDOFactory.buildInstance(sourceEntity) : null;
            Entity targetEntity = relation.getType();
            EntityDO targetEntityDO = targetEntity != null ? EntityDOFactory.buildInstance(targetEntity) : null;
            RelationDO oppositeRelationDO = null;
            if (relation.getOpposite() != null && !isOppositeRelationInitialized) {
                oppositeRelationDO = buildInstance(relation.getOpposite(), true);
            }
            boolean isContainment = relation.getIsContainment() != null ? relation.getIsContainment() : false;
            CardinalityDO cardinalityDO = null;
            if (relation.getCardinality() != null) {
                cardinalityDO = CardinalityDO.get(relation.getCardinality().getValue());
            }
            return new RelationDOImpl(relation.getName(), sourceEntityDO, targetEntityDO, oppositeRelationDO, isContainment, cardinalityDO);
        }
        return null;
    }
}
