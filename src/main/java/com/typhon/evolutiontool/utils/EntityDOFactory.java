package com.typhon.evolutiontool.utils;

import com.typhon.evolutiontool.entities.EntityDO;
import com.typhon.evolutiontool.entities.EntityDOAdapter;
import typhonml.ChangeOperator;
import typhonml.Entity;

public class EntityDOFactory {

    public static EntityDO createEntityDOFromEntityML(Entity entity) {
        return new EntityDOAdapter(entity);
    }
}
