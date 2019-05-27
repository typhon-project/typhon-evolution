package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;

import java.util.List;

public interface EvolutionToolFacade {

    String executeSMO(List<SMO> smoList, String initialModelPath, String finalModelPath) throws InputParameterException ;

}
