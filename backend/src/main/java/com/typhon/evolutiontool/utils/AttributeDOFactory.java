package com.typhon.evolutiontool.utils;

import com.typhon.evolutiontool.entities.AttributeDO;
import com.typhon.evolutiontool.entities.AttributeDOImpl;
import com.typhon.evolutiontool.entities.EntityDO;
import typhonml.Attribute;
import typhonml.Entity;

public class AttributeDOFactory {

    private AttributeDOFactory() {
    }

    public static AttributeDO buildInstance(Attribute attribute) {
        if (attribute != null) {
            Entity entity = (Entity) attribute.eContainer();
            EntityDO entityDO = entity != null ? EntityDOFactory.buildInstance(entity) : null;
            return new AttributeDOImpl(attribute.getName(), attribute.getImportedNamespace(), DataTypeDOFactory.buildInstance(attribute.getType()), entityDO);
        }
        return null;
    }
}
