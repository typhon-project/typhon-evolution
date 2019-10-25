package com.typhon.evolutiontool.handlers.relation;

import com.typhon.evolutiontool.entities.ParametersKeyString;
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

import java.util.Collections;

public class RelationEnableContainmentHandler extends BaseHandler {

    public RelationEnableContainmentHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Collections.singletonList(ParametersKeyString.RELATION))) {
            RelationDO relationDO = RelationDOFactory.buildInstance((Relation) smo.getInputParameter().get(ParametersKeyString.RELATION), false);
            Database database = typhonMLInterface.getEntityDatabase(relationDO.getSourceEntity().getName(), model);
            if (database instanceof RelationalDB) {
                throw new InputParameterException("Cannot produce a containment relationship in relational database source entity");
            }
            Model targetModel = typhonMLInterface.enableContainment(relationDO, model);
            typhonQLInterface.enableContainment(relationDO.getName(), relationDO.getSourceEntity().getName(), targetModel);
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameter. Needed [" + ParametersKeyString.RELATION + "]");
        }
    }
}
