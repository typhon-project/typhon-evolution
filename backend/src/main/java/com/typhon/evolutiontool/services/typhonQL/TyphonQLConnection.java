package com.typhon.evolutiontool.services.typhonQL;

import com.typhon.evolutiontool.entities.WorkingSet;
import typhonml.ChangeOperator;
import typhonml.Model;

/**
 * This interface represents the TyphonQL Engine interface. Methods specification are directly taken as specified in deliverable D4.3.
 */
public interface TyphonQLConnection {

    String executeTyphonQLDDL(String tqlDDL);

    void uploadModel(String schemaContent);

    void evolve(ChangeOperator changeOperator);

    void insert(WorkingSet workingSet);

    void delete(WorkingSet workingSet);

    void update(WorkingSet workingSet);

    void execute(String statement, Object... params);

    WorkingSet query(String query, Object... params);

//        void registerMonitor(Event event, Monitor handler);

//        void removeMonitor(Event event, Monitor handler);

//        void registerProcedure(String name, Object impl);

}
