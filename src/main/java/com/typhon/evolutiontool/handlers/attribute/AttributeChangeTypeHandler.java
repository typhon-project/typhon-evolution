package com.typhon.evolutiontool.handlers.attribute;

import com.typhon.evolutiontool.entities.AttributeDO;
import com.typhon.evolutiontool.entities.AttributeDOImpl;
import com.typhon.evolutiontool.entities.ParametersKeyString;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import com.typhon.evolutiontool.utils.AttributeDOFactory;
import org.springframework.stereotype.Component;
import typhonml.Attribute;
import typhonml.DataType;
import typhonml.Model;

import java.util.Arrays;

@Component("attributechangetype")
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
