package main.java.com.typhon.evolutiontool.handlers.relation;

import main.java.com.typhon.evolutiontool.entities.ParametersKeyString;
import main.java.com.typhon.evolutiontool.entities.RelationDO;
import main.java.com.typhon.evolutiontool.entities.SMO;
import main.java.com.typhon.evolutiontool.exceptions.InputParameterException;
import main.java.com.typhon.evolutiontool.handlers.BaseHandler;
import main.java.com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import main.java.com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import main.java.com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import main.java.com.typhon.evolutiontool.utils.RelationDOFactory;
import typhonml.Model;
import typhonml.Relation;

import java.util.Collections;

public class RelationDisableOppositeHandler extends BaseHandler {

    public RelationDisableOppositeHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Collections.singletonList(ParametersKeyString.RELATION))) {
        	RelationDO relation = RelationDOFactory.buildInstance((Relation) smo.getInputParameter().get(ParametersKeyString.RELATION), false);
        	RelationDO oppositeRelation = relation.getOpposite();

        	Model targetModel = typhonMLInterface.deleteRelationshipInEntity(oppositeRelation.getName(), oppositeRelation.getSourceEntity().getName(), model);
            targetModel = typhonMLInterface.disableOpposite(relation, targetModel);

            typhonQLInterface.deleteRelationshipInEntity(oppositeRelation.getName(), oppositeRelation.getSourceEntity().getName(), targetModel);
            //TODO: complete the QL necessary operations

            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ParametersKeyString.RELATION + "]");
        }
    }
}
