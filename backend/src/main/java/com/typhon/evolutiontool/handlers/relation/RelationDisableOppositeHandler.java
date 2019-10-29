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

public class RelationDisableOppositeHandler extends BaseHandler {

    public RelationDisableOppositeHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Collections.singletonList(ChangeOperatorParameter.RELATION))) {
        	RelationDO relation = RelationDOFactory.buildInstance((Relation) smo.getInputParameter().get(ChangeOperatorParameter.RELATION), false);
        	RelationDO oppositeRelation = relation.getOpposite();

//        	Model targetModel = typhonMLInterface.deleteRelationshipInEntity(oppositeRelation.getName(), oppositeRelation.getSourceEntity() != null ? oppositeRelation.getSourceEntity().getName() : null, model);
            Model targetModel = typhonMLInterface.disableOpposite(relation, model);

//            typhonQLInterface.deleteRelationshipInEntity(oppositeRelation.getName(), oppositeRelation.getSourceEntity().getName(), targetModel);
            //TODO: complete the QL necessary operations

            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ChangeOperatorParameter.RELATION + "]");
        }
    }
}
