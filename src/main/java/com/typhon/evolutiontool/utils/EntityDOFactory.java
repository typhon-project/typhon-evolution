package com.typhon.evolutiontool.utils;

import com.typhon.evolutiontool.entities.EntityDO;
import com.typhon.evolutiontool.entities.EntityDOImpl;
import com.typhon.evolutiontool.entities.RelationDO;
import typhonml.Attribute;
import typhonml.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityDOFactory {

    private EntityDOFactory() {
    }

    public static EntityDO buildInstance(Entity entity) {
        if (entity != null) {
            // Not useful and StackOverflowError
//        List<Relation> relations = entity.getRelations();
            List<RelationDO> relationsDO = new ArrayList<>();
//        if (relations != null) {
//            for (Relation relation : relations) {
//                relationsDO.add(RelationDOFactory.buildInstance(relation));
//            }
//        }
            List<Attribute> entityAttributes = entity.getAttributes();
            Map<String, Object> attributes = new HashMap<>();
            if (entityAttributes != null) {
                for (Attribute attribute : entityAttributes) {
                    attributes.put(attribute.getName(), attribute.getType());
                }
            }
            return new EntityDOImpl(entity.getName(), entity.getImportedNamespace(), relationsDO, null, attributes);
        }
        return null;
    }
}
