package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;

public interface EvolutionToolFacade {

    String executeSMO(SMO smo) throws InputParameterException;

}
