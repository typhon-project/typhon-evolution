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
import typhonml.Database;
import typhonml.Model;
import typhonml.Relation;
import typhonml.RelationalDB;

import java.util.Arrays;

public class RelationChangeContainmentHandler extends BaseHandler {

    public RelationChangeContainmentHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Arrays.asList(ChangeOperatorParameter.RELATION, ChangeOperatorParameter.NEW_CONTAINMENT))) {
            RelationDO relationDO = RelationDOFactory.buildInstance((Relation) smo.getInputParameter().get(ChangeOperatorParameter.RELATION), false);
            Boolean newContainment = (Boolean) smo.getInputParameter().get(ChangeOperatorParameter.NEW_CONTAINMENT);
            Database database = typhonMLInterface.getEntityDatabase(relationDO.getSourceEntity().getName(), model);
            if (database instanceof RelationalDB) {
                throw new InputParameterException("Cannot produce a containment relationship in relational database source entity");
            }
            Model targetModel = model;
            if (newContainment != null) {
                if (newContainment) {
                    targetModel = typhonMLInterface.enableContainment(relationDO, model);
                    typhonQLInterface.enableContainment(relationDO.getName(), relationDO.getSourceEntity().getName(), targetModel);
                } else {
                    targetModel = typhonMLInterface.disableContainment(relationDO, model);
                    typhonQLInterface.disableContainment(relationDO.getName(), relationDO.getSourceEntity().getName(), targetModel);
                }
            }
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ChangeOperatorParameter.RELATION + ", " + ChangeOperatorParameter.NEW_CONTAINMENT + "]");
        }
    }
}
