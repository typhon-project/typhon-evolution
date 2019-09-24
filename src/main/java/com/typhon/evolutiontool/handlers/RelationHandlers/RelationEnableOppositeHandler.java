package com.typhon.evolutiontool.handlers.RelationHandlers;

import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import com.typhon.evolutiontool.entities.RelationDOImpl;
import typhonml.Model;

import java.util.Arrays;

public class RelationEnableOppositeHandler extends BaseHandler {

    public RelationEnableOppositeHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    public Model handle(SMO smo, Model model) throws InputParameterException {
        RelationDO relation, oppositeRelation;
        Model targetModel;

        if (containParameters(smo, Arrays.asList(ParametersKeyString.RELATION, ParametersKeyString.RELATIONNAME))) {
            relation = smo.getRelationDOFromInputParameter(ParametersKeyString.RELATION);
            oppositeRelation = new RelationDOImpl(
                    smo.getInputParameter().get(ParametersKeyString.RELATIONNAME).toString(),
                    relation.getTargetEntity(),
                    relation.getSourceEntity(),
                    relation,
                    false,
                    reverseCardinality(relation.getCardinality().getValue())
            );

            targetModel = typhonMLInterface.createRelationship(oppositeRelation, model);
            targetModel = typhonMLInterface.enableOpposite(relation, oppositeRelation, targetModel);

            typhonQLInterface.createRelationshipType(oppositeRelation, targetModel);
            //TODO: complete the QL necessary operations

            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ParametersKeyString.RELATION + ", " + ParametersKeyString.RELATIONNAME + "]");
        }
    }

    /**
     * Reverse the input cardinality:
     * 0-1 <-> 1-1
     * 1-1 <-> 1-1
     * 0-* <-> 1-*
     * 1-* <-> 1-*
     */
    private CardinalityDO reverseCardinality(int cardinalityValue) {
        CardinalityDO cardinalityDO;
        switch (cardinalityValue) {
            case CardinalityDO.ZERO_ONE_VALUE:
            case CardinalityDO.ONE_VALUE:
                cardinalityDO = CardinalityDO.ONE;
                break;
            case CardinalityDO.ZERO_MANY_VALUE:
            case CardinalityDO.ONE_MANY_VALUE:
                cardinalityDO = CardinalityDO.ONE_MANY;
                break;
            default:
                cardinalityDO = CardinalityDO.get(cardinalityValue);
                break;
        }
        return cardinalityDO;
    }

}
