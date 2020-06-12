package com.typhon.evolutiontool.entities;

import com.typhon.evolutiontool.datatypes.DataTypeDO;

public class AttributeDOImpl implements AttributeDO {

    private String name;
    private String importedNamespace;
    private DataTypeDO dataTypeDO;
    private EntityDO entityDO;

    public AttributeDOImpl(String name, String importedNamespace, DataTypeDO dataTypeDO, EntityDO entityDO) {
        this.name = name;
        this.importedNamespace = importedNamespace;
        this.dataTypeDO = dataTypeDO;
        this.entityDO = entityDO;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getImportedNamespace() {
        return this.importedNamespace;
    }

    @Override
    public DataTypeDO getDataTypeDO() {
        return this.dataTypeDO;
    }

    @Override
    public EntityDO getEntity() {
        return this.entityDO;
    }
}
