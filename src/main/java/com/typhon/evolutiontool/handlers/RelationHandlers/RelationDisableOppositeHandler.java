package com.typhon.evolutiontool.handlers.RelationHandlers;

import com.typhon.evolutiontool.entities.ParametersKeyString;
import com.typhon.evolutiontool.entities.RelationDO;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import typhonml.Model;
import typhonml.impl.RelationImpl;

import java.util.Collections;

public class RelationDisableOppositeHandler extends BaseHandler {

    public RelationDisableOppositeHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    public Model handle(SMO smo, Model model) throws InputParameterException {
        RelationDO relation, oppositeRelation;
        Model targetModel;

        if (containParameters(smo, Collections.singletonList(ParametersKeyString.RELATION))) {
            relation = smo.getRelationDOFromInputParameter(ParametersKeyString.RELATION);
            oppositeRelation = relation.getOpposite();
//            oppositeRelation = ((RelationImpl) smo.getInputParameter().get(ParametersKeyString.RELATION));

            targetModel = typhonMLInterface.deleteRelationshipInEntity(oppositeRelation.getName(), oppositeRelation.getSourceEntity().getName(), model);
            targetModel = typhonMLInterface.disableOpposite(relation, targetModel);

            typhonQLInterface.deleteRelationshipInEntity(oppositeRelation.getName(), oppositeRelation.getSourceEntity().getName(), targetModel);
            //TODO: complete the QL necessary operations

            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ParametersKeyString.RELATION + "]");
        }
    }
}
