package com.typhon.evolutiontool.entities;

import com.typhon.evolutiontool.datatypes.DataTypeDO;

public interface AttributeDO {

    String getName();

    String getImportedNamespace();

    DataTypeDO getDataTypeDO();

    EntityDO getEntity();

}
