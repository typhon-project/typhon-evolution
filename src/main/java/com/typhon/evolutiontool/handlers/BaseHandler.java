package com.typhon.evolutiontool.handlers;

import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.services.EvolutionServiceImpl;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import typhonml.Model;

import java.util.List;

public class BaseHandler implements Handler{
    Logger logger = LoggerFactory.getLogger(EvolutionServiceImpl.class);

    protected TyphonDLInterface typhonDLInterface;
    protected TyphonMLInterface typhonMLInterface;
    protected TyphonQLInterface typhonQLInterface;

    public BaseHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql){
        typhonMLInterface = tml;
        typhonQLInterface = tql;
        typhonDLInterface = tdl;
    }


    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        return null;
    }

    protected boolean containParameters(SMO smo, List<String> parameters) {
        logger.info("Verifying input parameter for [{}] - [{}] operator",smo.getTyphonObject(), smo.getEvolutionOperator());
        return smo.inputParametersContainsExpected(parameters);
    }
}