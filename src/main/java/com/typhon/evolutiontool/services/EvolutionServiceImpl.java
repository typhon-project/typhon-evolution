package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class EvolutionServiceImpl implements EvolutionService{


    @Override
    public String addEntity(SMO smo) throws InputParameterException {
        if(smo.verifyInputParameters(Arrays.asList("entity","targetmodel")))
            return "entity created";
        else
            throw new InputParameterException("Missing parameter");
    }

    @Override
    public String renameEntity(SMO smo) {
        return null;
    }
}
