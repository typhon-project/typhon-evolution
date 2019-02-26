package com.typhon.evolutiontool.dummy;

import com.typhon.evolutiontool.entities.Entity;
import com.typhon.evolutiontool.entities.WorkingSet;

import java.util.LinkedHashMap;
import java.util.List;

public class WorkingSetDummyImpl implements WorkingSet {

    public LinkedHashMap<String, List<Entity>> rows;

    public WorkingSetDummyImpl() {
        rows = new LinkedHashMap<>();
    }

    @Override
    public LinkedHashMap<String, List<Entity>> rows() {
        return rows;
    }

    public void setEntityRows(String entity, List<Entity> entities) {
        rows.put(entity, entities);
    }
    }
