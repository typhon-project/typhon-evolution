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

import java.util.Arrays;

public class RelationRenameHandler extends BaseHandler {

    public RelationRenameHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Arrays.asList(ChangeOperatorParameter.RELATION, ChangeOperatorParameter.RELATION_NAME))) {
            RelationDO relationDO = RelationDOFactory.buildInstance((Relation) smo.getInputParameter().get(ChangeOperatorParameter.RELATION), false);
            String newRelationName = smo.getInputParameter().get(ChangeOperatorParameter.RELATION_NAME).toString();

            //Typhon QL
            //TODO not implemented yet by TyphonQL
            typhonQLInterface.renameRelation(relationDO.getSourceEntity().getName(), relationDO.getName(), newRelationName);

            //Typhon ML
            Model targetModel = typhonMLInterface.renameRelation(relationDO.getName(), relationDO.getSourceEntity().getName(), newRelationName, model);
            targetModel = typhonMLInterface.removeCurrentChangeOperator(targetModel);

            //Upload the new XMI to the polystore
            typhonQLInterface.uploadSchema(targetModel);

            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ChangeOperatorParameter.RELATION + ", " + ChangeOperatorParameter.RELATION_NAME + "]");
        }
    }

}
