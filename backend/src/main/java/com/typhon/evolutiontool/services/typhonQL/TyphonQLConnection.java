package com.typhon.evolutiontool.services.typhonQL;

import com.typhon.evolutiontool.entities.TyphonMLSchema;
import com.typhon.evolutiontool.entities.WorkingSet;

import java.io.IOException;
import java.util.Map;

/**
 * This interface represent the TyphonQL module interface. Methods specification are directly taken as specified in deliverable D4.2
 */
public interface TyphonQLConnection {
    /*
    Added by M.Gobert
    Is supposed to compile to actual ddl command and be sent to TyphonDL module. (ex: CREATE TABLE t...)
     */
    String executeTyphonQLDDL(String tqlDDL);

    //Taken from D4.2
    static TyphonQLConnection newEngine(TyphonMLSchema schema) {
            return new TyphonQLConnectionImpl(schema);
    }

    WorkingSet query(String queryString, String ...params);
    WorkingSet query(String queryString, Map<String,Object> params);
    WorkingSet update(WorkingSet ws);
    WorkingSet insert(WorkingSet ws);
    WorkingSet delete(WorkingSet ws);

    //    void registerMonitor(Event event, Monitor handler);
    //    void removeMonitor(Event event, Monitor handler);

}
