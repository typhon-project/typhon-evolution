package com.typhon.evolutiontool.handlers.relation;

import com.typhon.evolutiontool.entities.CardinalityDO;
import com.typhon.evolutiontool.entities.ChangeOperatorParameter;
import com.typhon.evolutiontool.entities.RelationDO;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import com.typhon.evolutiontool.utils.RelationDOFactory;
import typhonml.Model;
import typhonml.Relation;

import java.util.Arrays;

public class RelationChangeCardinalityHandler extends BaseHandler {

    public RelationChangeCardinalityHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Arrays.asList(ChangeOperatorParameter.RELATION, ChangeOperatorParameter.CARDINALITY))) {
            RelationDO relationDO = RelationDOFactory.buildInstance((Relation) smo.getInputParameter().get(ChangeOperatorParameter.RELATION), false);
            int cardinalityValue = (Integer) smo.getInputParameter().get(ChangeOperatorParameter.CARDINALITY);

            //Typhon ML
            Model targetModel = typhonMLInterface.changeCardinalityInRelation(relationDO, CardinalityDO.get(cardinalityValue), model);
            targetModel = typhonMLInterface.removeCurrentChangeOperator(targetModel);

            //Typhon QL
            //Upload the new XMI to the polystore
            typhonQLInterface.uploadSchema(targetModel);
            typhonQLInterface.changeCardinalityInRelation(relationDO.getName(), relationDO.getSourceEntity().getName(), CardinalityDO.get(cardinalityValue));

            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ChangeOperatorParameter.RELATION + ", " + ChangeOperatorParameter.RELATION_NAME + "]");
        }
    }

}
