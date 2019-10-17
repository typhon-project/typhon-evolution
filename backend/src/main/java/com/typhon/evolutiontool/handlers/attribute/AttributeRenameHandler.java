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

public class AttributeRenameHandler extends BaseHandler {

    public AttributeRenameHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Arrays.asList(ParametersKeyString.ATTRIBUTE, ParametersKeyString.NEWATTRIBUTENAME, ParametersKeyString.ENTITYNAME))) {
            AttributeDO attributeDO = AttributeDOFactory.buildInstance((Attribute) smo.getInputParameter().get(ParametersKeyString.ATTRIBUTE));
            String newAttributeName = String.valueOf(smo.getInputParameter().get(ParametersKeyString.NEWATTRIBUTENAME));
            String entityName = String.valueOf(smo.getInputParameter().get(ParametersKeyString.ENTITYNAME));
            Model targetModel = typhonMLInterface.renameAttribute(attributeDO.getName(), newAttributeName, entityName, model);
            typhonQLInterface.renameAttribute(attributeDO.getName(), newAttributeName, entityName, targetModel);
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ParametersKeyString.ATTRIBUTE + ", " + ParametersKeyString.NEWATTRIBUTENAME + ", " + ParametersKeyString.ENTITYNAME + "]");
        }
    }

}
