package com.typhon.evolutiontool.handlers.relation;

import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import com.typhon.evolutiontool.utils.RelationDOFactory;
import typhonml.Model;
import typhonml.Relation;

import java.util.Collections;

public class RelationEnableOppositeHandler extends BaseHandler {

    public RelationEnableOppositeHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Collections.singletonList(ChangeOperatorParameter.RELATION))) {
            RelationDO relationDO = RelationDOFactory.buildInstance((Relation) smo.getInputParameter().get(ChangeOperatorParameter.RELATION), false);
            RelationDO oppositeRelation = new RelationDOImpl(
                    relationDO.getName().concat("_opposite"),
                    relationDO.getSourceEntity().getName(),
                    relationDO.getTargetEntity(),
                    relationDO.getSourceEntity(),
                    relationDO,
                    false,
                    reverseCardinality(relationDO.getCardinality().getValue())
            );

            Model targetModel = typhonMLInterface.createRelationship(oppositeRelation, model);
            targetModel = typhonMLInterface.enableOpposite(relationDO, oppositeRelation, targetModel);

            typhonQLInterface.createRelationshipType(oppositeRelation, targetModel);
            //TODO: complete the QL necessary operations

            return targetModel;
        } else {
            throw new InputParameterException("Missing parameter. Needed [" + ChangeOperatorParameter.RELATION + "]");
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
