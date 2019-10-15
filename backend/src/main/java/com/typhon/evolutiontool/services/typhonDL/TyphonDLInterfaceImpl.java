package com.typhon.evolutiontool.services.typhonDL;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TyphonDLInterfaceImpl implements TyphonDLInterface {


    Logger logger = LoggerFactory.getLogger(TyphonDLInterfaceImpl.class);

    /**
     * Verify on the TyphonDL module that the given @param databasename of type @param databasetype is running.
     * @param databasetype
     * @param databasename
     * @return true if database is running
     */
    @Override
    public boolean isDatabaseRunning(String databasetype, String databasename) {
        logger.info("Verifying that database [{}] of type [{}] is running",databasename,databasetype);
        //TODO implement TyphonDL verifiy database running
        return true;
    }

    @Override
    public void createDatabase(String databasetype, String databasename) {
        logger.info("Asking TyphonDL module to create database [{}] of type [{}]",databasename,databasetype);
        //TODO implement TyphonDL create Database
    }
}
