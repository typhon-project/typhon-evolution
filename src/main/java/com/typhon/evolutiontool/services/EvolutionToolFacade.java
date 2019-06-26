package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.exceptions.InputParameterException;
import typhonml.Model;

public interface EvolutionToolFacade {

    Model executeChangeOperators(Model model) throws InputParameterException ;

}
