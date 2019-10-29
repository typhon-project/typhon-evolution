package main.java.com.typhon.evolutiontool.utils;

import main.java.com.typhon.evolutiontool.entities.AttributeDO;
import main.java.com.typhon.evolutiontool.entities.AttributeDOImpl;
import typhonml.Attribute;

public class AttributeDOFactory {

    private AttributeDOFactory() {
    }

    public static AttributeDO buildInstance(Attribute attribute) {
        if (attribute != null) {
            return new AttributeDOImpl(attribute.getName(), attribute.getImportedNamespace(), DataTypeDOFactory.buildInstance(attribute.getType()));
        }
        return null;
    }
}
