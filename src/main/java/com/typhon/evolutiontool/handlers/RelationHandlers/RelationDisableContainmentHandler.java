package com.typhon.evolutiontool.handlers.RelationHandlers;

import com.typhon.evolutiontool.entities.EvolutionOperator;
import com.typhon.evolutiontool.entities.ParametersKeyString;
import com.typhon.evolutiontool.entities.RelationDO;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import org.springframework.beans.factory.annotation.Autowired;
import typhonml.Model;

import java.util.Arrays;

public class RelationDisableContainmentHandler extends BaseHandler {

    public RelationDisableContainmentHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    public Model handle(SMO smo, Model model) throws InputParameterException, EvolutionOperationNotSupported {

        if(smo.getEvolutionOperator() == EvolutionOperator.DISABLECONTAINMENT){
            return disableContainmentInRelationship(smo, model);
        }
        else{
            return delegateToNext(smo, model);
        }
    }


    public Model disableContainmentInRelationship(SMO smo, Model model) throws InputParameterException {
        RelationDO relation;
        Model targetModel;

        if (containParameters(smo, Arrays.asList(ParametersKeyString.RELATION))) {
            relation = smo.getRelationDOFromInputParameter(ParametersKeyString.RELATION);
            targetModel = typhonMLInterface.disableContainment(relation, model);
            typhonQLInterface.disableContainment(relation.getName(),relation.getSourceEntity().getName(), targetModel);
            return targetModel;
        }
        else{
            throw new InputParameterException("Missing parameter. Needed [" + ParametersKeyString.RELATION+"]");
        }

    }
}
