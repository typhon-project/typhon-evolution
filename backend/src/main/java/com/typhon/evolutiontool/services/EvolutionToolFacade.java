package main.java.com.typhon.evolutiontool.services;

import main.java.com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import main.java.com.typhon.evolutiontool.exceptions.InputParameterException;
import typhonml.Model;

public interface EvolutionToolFacade {
	
	String evolve(String initialModelPath, String finalModelPath);

    Model executeChangeOperators(Model model) throws InputParameterException, EvolutionOperationNotSupported;

}
