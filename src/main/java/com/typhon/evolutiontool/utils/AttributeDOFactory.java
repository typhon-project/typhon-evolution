package com.typhon.evolutiontool.utils;

import com.typhon.evolutiontool.entities.AttributeDO;
import com.typhon.evolutiontool.entities.AttributeDOImpl;
import typhonml.Attribute;

public class AttributeDOFactory {

    private AttributeDOFactory() {
    }

    public static AttributeDO buildInstance(Attribute attribute) {
        if (attribute != null) {
            return new AttributeDOImpl(attribute.getName(), attribute.getImportedNamespace(), attribute.getType().getName(), attribute.getType().getImportedNamespace());
        }
        return null;
    }
}
