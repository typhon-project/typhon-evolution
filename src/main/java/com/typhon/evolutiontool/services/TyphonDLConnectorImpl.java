package com.typhon.evolutiontool.services;


import org.springframework.stereotype.Component;

@Component
public class TyphonDLConnectorImpl implements TyphonDLConnector {
    @Override
    public boolean isRunning(String databasetype, String databasename) {
        return false;
    }
}
