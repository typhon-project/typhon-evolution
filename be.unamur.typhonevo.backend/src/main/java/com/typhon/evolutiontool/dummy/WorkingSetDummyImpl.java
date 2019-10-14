package com.typhon.evolutiontool.dummy;

import com.typhon.evolutiontool.entities.EntityInstance;
import com.typhon.evolutiontool.entities.WorkingSet;

import java.util.LinkedHashMap;
import java.util.List;

public class WorkingSetDummyImpl implements WorkingSet {

    public LinkedHashMap<String, List<EntityInstance>> rows;

    public WorkingSetDummyImpl() {
        rows = new LinkedHashMap<>();
    }

    @Override
    public void setEntityRows(String entity, List<EntityInstance> entities) {
        rows.put(entity, entities);
    }

    @Override
    public List<EntityInstance> getEntityInstanceRows(String entityname) {
        return rows.get(entityname);
    }

    @Override
    public void deleteEntityRows(String entityname) {
        rows.remove(entityname);
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
    public String toString() {
        return "WorkingSetDummyImpl{" +
                "rows=" + rows +
                '}';
    }
}
