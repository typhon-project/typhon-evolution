package com.typhon.evolutiontool.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Component
public class TyphonDLConnectorImpl implements TyphonDLConnector {


    Logger logger = LoggerFactory.getLogger(TyphonDLConnectorImpl.class);
    /**
     * Verify on the TyphonDL module that the given @param databasename of type @param databasetype is running.
     * @param databasetype
     * @param databasename
     * @return
     */
    @Override
    public boolean isDatabaseRunning(String databasetype, String databasename) {
        logger.info("Verifying that database [?] of type [?] is running",databasename,databasetype);
        //TODO implement TyphonDL verifiy database running
        throw new NotImplementedException();
    }

    @Override
    public void createDatabase(String databasetype, String databasename) {
        logger.info("Asking TyphonDL module to create database [?] of type [?]",databasename,databasetype);
        //TODO implement TyphonDL create Database
        throw new NotImplementedException();
    }
}
