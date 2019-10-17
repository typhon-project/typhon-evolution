package main.java.com.typhon.evolutiontool.services;

import main.java.com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import main.java.com.typhon.evolutiontool.exceptions.InputParameterException;
import typhonml.Model;

public interface EvolutionToolFacade {

    Model executeChangeOperators(Model model) throws InputParameterException, EvolutionOperationNotSupported;

}
