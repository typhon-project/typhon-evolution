package com.typhon.evolutiontool.entities;

import java.util.LinkedHashMap;
import java.util.List;

public interface WorkingSet {

    LinkedHashMap<String, List<EntityInstance>> getRows();

    void setRows(LinkedHashMap<String, List<EntityInstance>> rows);

    List<EntityInstance> getEntityRows(String entityName);

    void addEntityRows(String entity, List<EntityInstance> entities);

    void deleteEntityRows(String entityName);
}
