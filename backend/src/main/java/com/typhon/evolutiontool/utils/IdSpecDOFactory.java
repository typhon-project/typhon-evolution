package com.typhon.evolutiontool.utils;

import com.typhon.evolutiontool.entities.AttributeDO;
import com.typhon.evolutiontool.entities.IdSpecDO;
import com.typhon.evolutiontool.entities.IdSpecDOImpl;
import typhonml.Attribute;
import typhonml.IdSpec;

import java.util.ArrayList;
import java.util.List;

public class IdSpecDOFactory {

    private IdSpecDOFactory() {
    }

    public static IdSpecDO buildInstance(IdSpec idSpec) {
        if (idSpec != null) {
            List<Attribute> attributes = idSpec.getAttributes();
            List<AttributeDO> attributesDO = new ArrayList<>();
            if (attributes != null) {
                for (Attribute attribute : attributes) {
                    attributesDO.add(AttributeDOFactory.buildInstance(attribute));
                }
            }
            return new IdSpecDOImpl(attributesDO);
        }
        return null;
    }
}
