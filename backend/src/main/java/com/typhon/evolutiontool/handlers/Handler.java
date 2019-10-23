package com.typhon.evolutiontool.handlers;

import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import typhonml.Model;

public interface Handler {
    Model handle(SMO smo, Model model) throws InputParameterException;
}
