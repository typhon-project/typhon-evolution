package main.java.com.typhon.evolutiontool.handlers.attribute;

import main.java.com.typhon.evolutiontool.entities.AttributeDO;
import main.java.com.typhon.evolutiontool.entities.AttributeDOImpl;
import main.java.com.typhon.evolutiontool.entities.ParametersKeyString;
import main.java.com.typhon.evolutiontool.entities.SMO;
import main.java.com.typhon.evolutiontool.exceptions.InputParameterException;
import main.java.com.typhon.evolutiontool.handlers.BaseHandler;
import main.java.com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import main.java.com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import main.java.com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import main.java.com.typhon.evolutiontool.utils.AttributeDOFactory;
import typhonml.Attribute;
import typhonml.DataType;
import typhonml.Model;

import java.util.Arrays;

public class AttributeChangeTypeHandler extends BaseHandler {

    public AttributeChangeTypeHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Arrays.asList(ParametersKeyString.ENTITYNAME, ParametersKeyString.ATTRIBUTE, ParametersKeyString.ATTRIBUTETYPE))) {
            String entityName = String.valueOf(smo.getInputParameter().get(ParametersKeyString.ENTITYNAME));
            AttributeDO attributeDO = AttributeDOFactory.buildInstance((Attribute) smo.getInputParameter().get(ParametersKeyString.ATTRIBUTE));
            String dataTypeName = ((DataType) smo.getInputParameter().get(ParametersKeyString.ATTRIBUTETYPE)).getName();
            Model targetModel = typhonMLInterface.changeTypeAttribute(attributeDO, entityName, dataTypeName, model);
            typhonQLInterface.changeTypeAttribute(attributeDO, entityName, targetModel);
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ParametersKeyString.ENTITYNAME + ", " + ParametersKeyString.ATTRIBUTENAME + ", " + ParametersKeyString.ATTRIBUTETYPE + "]");
        }
    }
}
