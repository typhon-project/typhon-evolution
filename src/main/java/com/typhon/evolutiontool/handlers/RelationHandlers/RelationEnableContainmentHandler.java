package com.typhon.evolutiontool.handlers.RelationHandlers;

import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import typhonml.Model;

import java.util.Arrays;

public class RelationEnableContainmentHandler extends BaseHandler {

    public Model handle(SMO smo, Model model) throws InputParameterException, EvolutionOperationNotSupported {

        if(smo.getEvolutionOperator() == EvolutionOperator.ENABLECONTAINMENT){
            return enableContainmentInRelationship(smo, model);
        }
        else{
            return delegateToNext(smo, model);
        }
    }


    private Model enableContainmentInRelationship(SMO smo, Model model) throws InputParameterException {
        RelationDO relation;
        Model targetModel;

        if (containParameters(smo, Arrays.asList(ParametersKeyString.RELATION))) {
            relation = smo.getRelationDOFromInputParameter(ParametersKeyString.RELATION);
            if (typhonMLInterface.getDatabaseType(relation.getSourceEntity().getName(),model) == DatabaseType.RELATIONALDB) {
                throw new InputParameterException("Cannot produce a containment relationship in relational database source entity");
            }
            targetModel = typhonMLInterface.enableContainment(relation, model);
            typhonQLInterface.enableContainment(relation.getName(),relation.getSourceEntity().getName(), targetModel);
            return targetModel;
        }
        else{
            throw new InputParameterException("Missing parameter");
        }
    }
}
