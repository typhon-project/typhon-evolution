package com.typhon.evolutiontool.handlers.relation;

import com.typhon.evolutiontool.entities.CardinalityDO;
import com.typhon.evolutiontool.entities.ParametersKeyString;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import com.typhon.evolutiontool.utils.RelationDOFactory;
import org.springframework.stereotype.Component;
import typhonml.Cardinality;
import typhonml.Model;
import typhonml.Relation;

import java.util.Arrays;

@Component("relationrechangecardinality")
public class RelationChangeCardinalityHandler extends BaseHandler {

    public RelationChangeCardinalityHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Arrays.asList(ParametersKeyString.RELATION, ParametersKeyString.CARDINALITY))) {
            Relation relation = (Relation) smo.getInputParameter().get(ParametersKeyString.RELATION);
            Cardinality cardinality = (Cardinality) smo.getInputParameter().get(ParametersKeyString.CARDINALITY);
            Model targetModel = typhonMLInterface.changeCardinalityInRelation(RelationDOFactory.buildInstance(relation), CardinalityDO.get(cardinality.getValue()), model);
            typhonQLInterface.changeCardinalityInRelation(relation.getName(), relation.getType().getName(), CardinalityDO.get(cardinality.getValue()), targetModel);
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ParametersKeyString.RELATION + ", " + ParametersKeyString.RELATIONNAME + "]");
        }
    }

}
