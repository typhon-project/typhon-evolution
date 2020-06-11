package com.typhon.evolutiontool.utils;

import com.typhon.evolutiontool.datatypes.DataTypeDO;
import com.typhon.evolutiontool.entities.EntityDO;
import com.typhon.evolutiontool.entities.EntityDOImpl;
import com.typhon.evolutiontool.entities.RelationDO;
import typhonml.Attribute;
import typhonml.Entity;
import typhonml.EntityAttributeKind;
import typhonml.Relation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityDOFactory {

    private EntityDOFactory() {
    }

    public static EntityDO buildInstance(Entity entity, boolean initialized) {
        if (entity != null) {
            List<Relation> relations = entity.getRelations();
            List<RelationDO> relationsDO = new ArrayList<>();
            if (relations != null) {
                for (Relation relation : relations) {
                    relationsDO.add(RelationDOFactory.buildInstance(relation, initialized));
                }
            }
            List<EntityAttributeKind> entityAttributes = entity.getAttributes();
            Map<String, DataTypeDO> attributes = new HashMap<>();
            if (entityAttributes != null) {
                for (EntityAttributeKind attribute : entityAttributes) {
                    if (attribute != null) {
                        if (attribute instanceof Attribute) {
                            attributes.put(attribute.getName(), DataTypeDOFactory.buildInstance(((Attribute) attribute).getType()));
                        }
                    }
                }
            }
            return new EntityDOImpl(entity.getName(), entity.getImportedNamespace(), relationsDO, null, attributes);
        }
        return null;
    }
}
