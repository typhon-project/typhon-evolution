package main.java.com.typhon.evolutiontool.handlers.attribute;

import main.java.com.typhon.evolutiontool.entities.AttributeDO;
import main.java.com.typhon.evolutiontool.entities.ParametersKeyString;
import main.java.com.typhon.evolutiontool.entities.SMO;
import main.java.com.typhon.evolutiontool.exceptions.InputParameterException;
import main.java.com.typhon.evolutiontool.handlers.BaseHandler;
import main.java.com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import main.java.com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import main.java.com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import main.java.com.typhon.evolutiontool.utils.AttributeDOFactory;
import typhonml.Attribute;
import typhonml.Model;

import java.util.Arrays;
import java.util.Collections;


public class AttributeRemoveHandler extends BaseHandler {

    public AttributeRemoveHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Arrays.asList(ParametersKeyString.ATTRIBUTE, ParametersKeyString.ENTITYNAME))) {
            AttributeDO attributeDO = AttributeDOFactory.buildInstance((Attribute) smo.getInputParameter().get(ParametersKeyString.ATTRIBUTE));
            String entityName = String.valueOf(smo.getInputParameter().get(ParametersKeyString.ENTITYNAME));
            Model targetModel = typhonMLInterface.removeAttribute(attributeDO, entityName, model);
            targetModel = typhonMLInterface.removeCurrentChangeOperator(model);
            typhonQLInterface.removeAttributes(entityName, Collections.singletonList(attributeDO.getName()), targetModel);
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ParametersKeyString.ATTRIBUTE + ", " + ParametersKeyString.ENTITYNAME + "]");
        }
    }


}
