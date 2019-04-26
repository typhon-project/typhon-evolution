package com.typhon.evolutiontool.entities;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Return type of TyphonQL query. Defined in deliverable D4.2
 */
public interface WorkingSet {

    LinkedHashMap<String, List<EntityInstance>> rows();

    public LinkedHashMap<String, List<EntityInstance>> getRows();

    public void setRows(LinkedHashMap<String, List<EntityInstance>> rows) ;

}
