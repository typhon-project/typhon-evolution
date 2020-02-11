package com.typhon.evolutiontool.entities;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class WorkingSetImpl implements WorkingSet {

    private LinkedHashMap<String, List<EntityInstance>> rows;

    public WorkingSetImpl() {
        rows = new LinkedHashMap<>();
    }

    @Override
    public LinkedHashMap<String, List<EntityInstance>> getRows() {
        return rows;
    }

    @Override
    public void setRows(LinkedHashMap<String, List<EntityInstance>> rows) {
        this.rows = rows;
    }

    @Override
    public List<EntityInstance> getEntityRows(String entityName) {
        if (rows.get(entityName) == null) {
            return new ArrayList<>();
        } else {
            return rows.get(entityName);
        }
    }

    @Override
    public void addEntityRows(String entity, List<EntityInstance> entities) {
        rows.put(entity, entities);
    }

    @Override
    public void deleteEntityRows(String entityName) {
        rows.remove(entityName);
    }

    @Override
    public String toString() {
        return "WorkingSetImpl{" +
                "rows=" + rows +
                '}';
    }
}
