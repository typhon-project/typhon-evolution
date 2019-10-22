package main.java.com.typhon.evolutiontool.entities;

import java.util.LinkedHashMap;
import java.util.List;

import typhonml.Entity;

/**
 * Return type of TyphonQL query. Defined in deliverable D4.2
 */
public interface WorkingSet {

    void setEntityRows(String entity, List<Entity> entities);

    List<Entity> getEntityInstanceRows(String entityname);

    void deleteEntityRows(String entityname);

    LinkedHashMap<String, List<Entity>> getRows();

    void setRows(LinkedHashMap<String, List<Entity>> rows) ;

}
