package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import typhonml.Model;

public interface EvolutionToolFacade {
	
	String evolve(String initialModelPath, String finalModelPath);

    Model executeChangeOperators(Model model) throws InputParameterException, EvolutionOperationNotSupported;

}
