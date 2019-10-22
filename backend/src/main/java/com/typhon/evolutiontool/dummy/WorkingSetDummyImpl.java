package main.java.com.typhon.evolutiontool.dummy;

import main.java.com.typhon.evolutiontool.entities.WorkingSet;
import typhonml.Entity;

import java.util.LinkedHashMap;
import java.util.List;

public class WorkingSetDummyImpl implements WorkingSet {

    public LinkedHashMap<String, List<Entity>> rows;

    public WorkingSetDummyImpl() {
        rows = new LinkedHashMap<>();
    }

    @Override
    public void setEntityRows(String entity, List<Entity> entities) {
        rows.put(entity, entities);
    }

    @Override
    public List<Entity> getEntityInstanceRows(String entityname) {
        return rows.get(entityname);
    }

    @Override
    public void deleteEntityRows(String entityname) {
        rows.remove(entityname);
    }

    @Override
    public LinkedHashMap<String, List<Entity>> getRows() {
        return rows;
    }

    @Override
    public void setRows(LinkedHashMap<String, List<Entity>> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "WorkingSetDummyImpl{" +
                "rows=" + rows +
                '}';
    }
}
