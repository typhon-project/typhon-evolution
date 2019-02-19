package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;

public interface EvolutionService {
    String addEntity(SMO smo) throws InputParameterException;

    String renameEntity(SMO smo) throws InputParameterException;
}
