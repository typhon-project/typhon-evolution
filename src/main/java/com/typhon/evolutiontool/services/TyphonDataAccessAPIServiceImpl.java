package com.typhon.evolutiontool.services;

import org.springframework.beans.factory.annotation.Autowired;

public class TyphonDataAccessAPIServiceImpl implements TyphonDataAccessAPIService {
    private TyphonDataAccessAPIConnection dataAccessAPIConnection;

    @Autowired
    public TyphonDataAccessAPIServiceImpl(TyphonDataAccessAPIConnection dataAccessAPIConnection) {
        this.dataAccessAPIConnection = dataAccessAPIConnection;
    }

    public void executeStructureChange(StructureChange structureChange){
        dataAccessAPIConnection.executeQuery(structureChange.getTqlQuery());
    }
}
