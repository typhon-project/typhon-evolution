package com.typhon.evolutiontool.handlers.relation;

import com.typhon.evolutiontool.entities.CardinalityDO;
import com.typhon.evolutiontool.entities.ParametersKeyString;
import com.typhon.evolutiontool.entities.RelationDO;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import com.typhon.evolutiontool.utils.RelationDOFactory;
import typhonml.Cardinality;
import typhonml.Model;
import typhonml.Relation;

import java.util.Arrays;

public class RelationChangeCardinalityHandler extends BaseHandler {

    public RelationChangeCardinalityHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Arrays.asList(ParametersKeyString.RELATION, ParametersKeyString.CARDINALITY))) {
            RelationDO relationDO = RelationDOFactory.buildInstance((Relation) smo.getInputParameter().get(ParametersKeyString.RELATION), false);
            Cardinality cardinality = (Cardinality) smo.getInputParameter().get(ParametersKeyString.CARDINALITY);
            Model targetModel = typhonMLInterface.changeCardinalityInRelation(relationDO, CardinalityDO.get(cardinality.getValue()), model);
            typhonQLInterface.changeCardinalityInRelation(relationDO.getName(), relationDO.getSourceEntity().getName(), CardinalityDO.get(cardinality.getValue()), targetModel);
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ParametersKeyString.RELATION + ", " + ParametersKeyString.RELATIONNAME + "]");
        }
    }

}
