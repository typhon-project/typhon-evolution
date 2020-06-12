package com.typhon.evolutiontool.entities;

public class StorageUnitDOImpl implements StorageUnitDO {

    private String databaseName;
    private String name;
    private EntityDO entity;
    private IdSpecDO idSpec;

    public StorageUnitDOImpl(String name, EntityDO entity, IdSpecDO idSpec) {
        this.name = name;
        this.entity = entity;
        this.idSpec = idSpec;
    }


    @Override
    public String getDatabaseName() {
        return this.databaseName;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public EntityDO getEntity() {
        return this.entity;
    }

    @Override
    public IdSpecDO getIdSpec() {
        return this.idSpec;
    }
}
