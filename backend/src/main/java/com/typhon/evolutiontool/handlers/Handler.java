package main.java.com.typhon.evolutiontool.handlers;

import main.java.com.typhon.evolutiontool.entities.SMO;
import main.java.com.typhon.evolutiontool.exceptions.InputParameterException;
import typhonml.Model;

public interface Handler {
    Model handle(SMO smo, Model model) throws InputParameterException;
}
