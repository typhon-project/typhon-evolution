package com.typhon.evolutiontool.utils;

import com.typhon.evolutiontool.entities.RelationAdapter;
import com.typhon.evolutiontool.entities.RelationDO;
import typhonml.AddRelation;
import typhonml.Relation;

public class RelationDOFactory {

    public static RelationDO createRelationDOFromRelationML(Relation relation) {
        return new RelationAdapter(relation);
    }
}
