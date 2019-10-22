package main.java.com.typhon.evolutiontool.handlers.relation;

import java.util.Collections;

import main.java.com.typhon.evolutiontool.entities.DatabaseType;
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

public class RelationEnableContainmentHandler extends BaseHandler {

    public RelationEnableContainmentHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Collections.singletonList(ParametersKeyString.RELATION))) {
        	RelationDO relationDO = RelationDOFactory.buildInstance((Relation) smo.getInputParameter().get(ParametersKeyString.RELATION), false);
            if (typhonMLInterface.getDatabaseType(relationDO.getSourceEntity().getName(), model) == DatabaseType.RELATIONALDB) {
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
