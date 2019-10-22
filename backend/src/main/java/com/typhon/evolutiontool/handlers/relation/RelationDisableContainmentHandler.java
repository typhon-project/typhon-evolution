package main.java.com.typhon.evolutiontool.handlers.relation;

import main.java.com.typhon.evolutiontool.entities.ParametersKeyString;
import main.java.com.typhon.evolutiontool.entities.RelationDO;
import main.java.com.typhon.evolutiontool.entities.SMO;
import main.java.com.typhon.evolutiontool.exceptions.InputParameterException;
import main.java.com.typhon.evolutiontool.handlers.BaseHandler;
import main.java.com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import main.java.com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import main.java.com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import typhonml.Model;

import java.util.Collections;

public class RelationDisableContainmentHandler extends BaseHandler {

    public RelationDisableContainmentHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Collections.singletonList(ParametersKeyString.RELATION))) {
            RelationDO relation = (RelationDO) smo.getInputParameter().get(ParametersKeyString.RELATION);
            Model targetModel = typhonMLInterface.disableContainment(relation, model);
            typhonQLInterface.disableContainment(relation.getName(), relation.getSourceEntity().getName(), targetModel);
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameter. Needed [" + ParametersKeyString.RELATION + "]");
        }
    }
}
