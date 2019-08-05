package com.typhon.evolutiontool.utils;

import com.typhon.evolutiontool.entities.AttributeDO;
import com.typhon.evolutiontool.entities.AttributeDOAdapter;
import typhonml.AddAttribute;
import typhonml.Attribute;

public class AttributeDOFactory {

    public static AttributeDO createAttributeDOFromAttributeML(Attribute attribute) {
        return new AttributeDOAdapter(attribute);
    }
}
