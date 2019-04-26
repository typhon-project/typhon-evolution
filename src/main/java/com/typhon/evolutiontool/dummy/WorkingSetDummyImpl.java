package com.typhon.evolutiontool.dummy;

import com.typhon.evolutiontool.entities.Entity;
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
    public LinkedHashMap<String, List<EntityInstance>> rows() {
        return rows;
    }

    public void setEntityRows(String entity, List<EntityInstance> entities) {
        rows.put(entity, entities);
    }

    public LinkedHashMap<String, List<EntityInstance>> getRows() {
        return rows;
    }

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
