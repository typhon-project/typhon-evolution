package com.typhon.evolutiontool.utils;

import com.typhon.evolutiontool.entities.EntityDO;
import com.typhon.evolutiontool.entities.IdSpecDO;
import com.typhon.evolutiontool.entities.StorageUnitDO;
import com.typhon.evolutiontool.entities.StorageUnitDOImpl;
import typhonml.Collection;
import typhonml.Table;

public class StorageUnitDOFactory {

    private StorageUnitDOFactory() {
    }

    public static StorageUnitDO buildInstance(Table table) {
        if (table != null) {
            EntityDO entityDO = table.getEntity() != null ? EntityDOFactory.buildInstance(table.getEntity(), true) : null;
            IdSpecDO idSpecDO = table.getIdSpec() != null ? IdSpecDOFactory.buildInstance(table.getIdSpec()) : null;
            return new StorageUnitDOImpl(table.getName(), entityDO, idSpecDO);
        }
        return null;
    }

    public static StorageUnitDO buildInstance(Collection collection) {
        if (collection != null) {
            EntityDO entityDO = collection.getEntity() != null ? EntityDOFactory.buildInstance(collection.getEntity(), true) : null;
            //TODO: build the IdSpecDO when ML and QL have implemented the change operator
//            IdSpecDO idSpecDO = collection.getIdSpec() != null ? IdSpecDOFactory.buildInstance(collection.getIdSpec()) : null;
            return new StorageUnitDOImpl(collection.getName(), entityDO, null/*idSpecDO*/);
        }
        return null;
    }
}
