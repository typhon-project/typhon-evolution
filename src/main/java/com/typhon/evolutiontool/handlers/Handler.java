package com.typhon.evolutiontool.handlers;

import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import typhonml.Model;

public interface Handler {
    public void  setNext(Handler handler);
    public Model handle(SMO smo, Model model) throws InputParameterException, EvolutionOperationNotSupported;
}
