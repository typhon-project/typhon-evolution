package com.typhon.evolutiontool.handlers.relation;

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

import java.util.Collections;

public class RelationDisableContainmentHandler extends BaseHandler {

    public RelationDisableContainmentHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Collections.singletonList(ChangeOperatorParameter.RELATION))) {
            RelationDO relation = RelationDOFactory.buildInstance((Relation) smo.getInputParameter().get(ChangeOperatorParameter.RELATION), false);
            Model targetModel = typhonMLInterface.disableContainment(relation, model);
            typhonQLInterface.disableContainment(relation.getName(), relation.getSourceEntity().getName());
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameter. Needed [" + ChangeOperatorParameter.RELATION + "]");
        }
    }
}
