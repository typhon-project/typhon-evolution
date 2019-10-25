package com.typhon.evolutiontool.handlers.relation;

import com.typhon.evolutiontool.entities.DatabaseType;
import com.typhon.evolutiontool.entities.ParametersKeyString;
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

public class RelationChangeContainmentHandler extends BaseHandler {

    public RelationChangeContainmentHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Arrays.asList(ParametersKeyString.RELATION, ParametersKeyString.NEWCONTAINMENT))) {
            RelationDO relationDO = RelationDOFactory.buildInstance((Relation) smo.getInputParameter().get(ParametersKeyString.RELATION), false);
            Boolean newContainment = (Boolean) smo.getInputParameter().get(ParametersKeyString.NEWCONTAINMENT);
            if (typhonMLInterface.getDatabaseType(relationDO.getSourceEntity().getName(), model) == DatabaseType.RELATIONALDB) {
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
            throw new InputParameterException("Missing parameters. Needed [" + ParametersKeyString.RELATION + ", " + ParametersKeyString.NEWCONTAINMENT + "]");
        }
    }
}
