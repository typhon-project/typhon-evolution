package com.typhon.evolutiontool.services.typhonDL;

public interface TyphonDLConnector {

    boolean isDatabaseRunning(String databasetype, String databasename);

    void createDatabase(String databasetype, String databasename);
}
