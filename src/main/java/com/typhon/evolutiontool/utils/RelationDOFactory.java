package com.typhon.evolutiontool.utils;

import com.typhon.evolutiontool.entities.*;
import typhonml.AddRelation;
import typhonml.Relation;

public class RelationDOFactory {

    public static RelationDO createRelationDOFromRelationML(Relation relation) {
        return new RelationAdapter(relation);
    }

    public static RelationDO createRelationDO(String relationName, EntityDO firstNewEntity, EntityDO secondNewEntity, RelationDO oppositerelation, boolean isContainment, CardinalityDO cardinality) {
        return new RelationDOJsonImpl(relationName, firstNewEntity, secondNewEntity, oppositerelation, isContainment, cardinality);
    }
}
