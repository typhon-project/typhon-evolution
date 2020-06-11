package com.typhon.evolutiontool.entities;

public interface StorageUnitDO {

    String getDatabaseName();
    String getName();
    EntityDO getEntity();
    IdSpecDO getIdSpec();
}
