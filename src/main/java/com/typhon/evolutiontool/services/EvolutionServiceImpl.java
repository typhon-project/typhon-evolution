package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class EvolutionServiceImpl implements EvolutionService{

    @Autowired
    private TyphonDLConnector typhonDLConnection;



    @Override
    public String addEntity(SMO smo) throws InputParameterException {
        // Verify InputParameters
        if(smo.verifyInputParameters(Arrays.asList("entity","targetmodel"))){
            // Verify that an instance of the underlying database is running in the TyphonDL.
            typhonDLConnection.isRunning(smo.getInputParameter().get("databasetype").toString(), smo.getInputParameter().get("databasename").toString());
            return "entity created";
        }
        else
            throw new InputParameterException("Missing parameter");


    }

    @Override
    public String renameEntity(SMO smo) {
        return null;
    }
}
