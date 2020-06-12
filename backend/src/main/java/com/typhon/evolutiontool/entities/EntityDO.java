package com.typhon.evolutiontool.entities;

import com.typhon.evolutiontool.datatypes.DataTypeDO;

import java.util.List;
import java.util.Map;

public interface EntityDO {

    void addAttribute(String name, DataTypeDO datatype);

    String getName();

    void setName(String name);

    String getIdentifier();

    Map<String, DataTypeDO> getAttributes();

    List<RelationDO> getRelations();
}
