package com.typhon.evolutiontool.entities;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Return type of TyphonQL query. Defined in deliverable D4.2
 */
public interface WorkingSet {

    void setEntityRows(String entity, List<EntityInstance> entities);

    List<EntityInstance> getEntityInstanceRows(String entityname);

    void deleteEntityRows(String entityname);

    LinkedHashMap<String, List<EntityInstance>> getRows();

    void setRows(LinkedHashMap<String, List<EntityInstance>> rows) ;

}
