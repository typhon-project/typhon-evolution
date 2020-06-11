package com.typhon.evolutiontool.entities;

import java.util.List;

public class IdSpecDOImpl implements IdSpecDO {

    private List<AttributeDO> attributes;

    public IdSpecDOImpl(List<AttributeDO> attributes) {
        this.attributes = attributes;
    }

    @Override
    public List<AttributeDO> getAttributes() {
        return this.attributes;
    }
}
