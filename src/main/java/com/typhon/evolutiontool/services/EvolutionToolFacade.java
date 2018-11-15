package com.typhon.evolutiontool.services;

import com.typhon.evolutiontool.Message;
import com.typhon.evolutiontool.entities.SMO;

public interface EvolutionToolFacade {

    Message executeSMO(SMO smo, long counter);

    boolean verifyInputParameter(SMO smo);
}
