package com.typhon.evolutiontool.services.typhonDL;

public interface TyphonDLInterface {

    /**
     * This function asks the TyphonDL module if a database @param databasename of type @param databasetype is running.
     * @param databasetype
     * @param databasename
     * @return
     */
    boolean isDatabaseRunning(String databasetype, String databasename);

    /**
     * This function asks the TyphonDL module to create and run an instance of a database of type @param databasetype with the provided name @param databasename.
     * @param databasetype
     * @param databasename
     */
    void createDatabase(String databasetype, String databasename);
}
