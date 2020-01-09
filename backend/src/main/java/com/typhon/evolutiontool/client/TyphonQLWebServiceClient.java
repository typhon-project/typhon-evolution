package com.typhon.evolutiontool.client;

import com.typhon.evolutiontool.entities.WorkingSet;
import typhonml.Model;

/**
 * This interface contains the methods used to query the web services of CLMS, for TyphonQL polystore manipulation
 */
public interface TyphonQLWebServiceClient {

    void uploadModel(String schemaContent);

    void resetDatabases();

    void getUsers();

    WorkingSet query(String query);

    void update(String query);

    Model getModel(Integer typhonMLModelVersion);

    void getModels();

    //Old methods hereafter:
//    void evolve(ChangeOperator changeOperator);
//    void insert(WorkingSet workingSet);
//    void delete(WorkingSet workingSet);
//    void update(WorkingSet workingSet);
//    void execute(String statement, Object... params);
//    WorkingSet query(String query, Object... params);

}
