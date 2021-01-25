package com.typhon.evolutiontool.utils;

import com.typhon.evolutiontool.entities.AttributeDO;
import com.typhon.evolutiontool.entities.AttributeDOImpl;
import com.typhon.evolutiontool.entities.EntityDO;
import typhonml.*;

public class AttributeDOFactory {

    private AttributeDOFactory() {
    }

    public static AttributeDO buildInstance(Attribute attribute) {
        if (attribute != null) {
            Entity entity = null;
            if (attribute instanceof AddAttribute) {
                entity = ((AddAttribute) attribute).getOwnerEntity();
            } else if (attribute.eContainer() instanceof Entity) {
                entity = (Entity) attribute.eContainer();
            }
            EntityDO entityDO = entity != null ? EntityDOFactory.buildInstance(entity, false) : null;
            return new AttributeDOImpl(attribute.getName(), attribute.getImportedNamespace(), DataTypeDOFactory.buildInstance(attribute.getType()), entityDO);
        }
        return null;
    }

    public static AttributeDO buildInstance(AddAttribute attribute) {
        if (attribute != null) {
            Entity entity = attribute.getOwnerEntity();
            EntityDO entityDO = entity != null ? EntityDOFactory.buildInstance(entity, false) : null;
            if (attribute instanceof AddPrimitiveDataTypeAttribute) {
                return new AttributeDOImpl(attribute.getName(), attribute.getImportedNamespace(), DataTypeDOFactory.buildInstance(((AddPrimitiveDataTypeAttribute) attribute).getType()), entityDO);
            } else if (attribute instanceof AddCustomDataTypeAttribute) {
                return new AttributeDOImpl(attribute.getName(), attribute.getImportedNamespace(), DataTypeDOFactory.buildInstance(((AddCustomDataTypeAttribute) attribute).getType()), entityDO);
            } else {
                return new AttributeDOImpl(attribute.getName(), attribute.getImportedNamespace(), DataTypeDOFactory.buildInstance(((Attribute) attribute).getType()), entityDO);
            }
        }
        return null;
    }
}
