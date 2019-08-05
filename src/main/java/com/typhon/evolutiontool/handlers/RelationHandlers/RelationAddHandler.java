package com.typhon.evolutiontool.handlers.RelationHandlers;

import com.typhon.evolutiontool.entities.EvolutionOperator;
import com.typhon.evolutiontool.entities.ParametersKeyString;
import com.typhon.evolutiontool.entities.RelationDO;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import typhonml.Model;

import java.util.Arrays;

public class RelationAddHandler extends BaseHandler {
    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException, EvolutionOperationNotSupported {

        if(smo.getEvolutionOperator() == EvolutionOperator.ADD){
            return addRelationship(smo, model);
        }
        else{
            return delegateToNext(smo, model);
        }
    }


    private Model addRelationship(SMO smo, Model model) throws InputParameterException {
        RelationDO relation;
        String targetmodelid;
        Model targetModel;

        if (containParameters(smo, Arrays.asList(ParametersKeyString.RELATION))) {
            relation = smo.getRelationDOFromInputParameter(ParametersKeyString.RELATION);
            targetModel = typhonMLInterface.createRelationship(relation, model);
            typhonQLInterface.createRelationshipType(relation,targetModel);
            return targetModel;
        }else {
            throw new InputParameterException("Missing parameter. Needed [" + ParametersKeyString.RELATION+"]");
        }

    }
}
