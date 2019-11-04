package com.typhon.evolutiontool.handlers.attribute;

import java.util.Arrays;

import com.typhon.evolutiontool.entities.AttributeDO;
import com.typhon.evolutiontool.entities.ChangeOperatorParameter;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import com.typhon.evolutiontool.utils.AttributeDOFactory;
import typhonml.Attribute;
import typhonml.DataType;
import typhonml.Model;

public class AttributeChangeTypeHandler extends BaseHandler {

    public AttributeChangeTypeHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Arrays.asList(ChangeOperatorParameter.ATTRIBUTE, ChangeOperatorParameter.ATTRIBUTE_TYPE))) {
            AttributeDO attributeDO = AttributeDOFactory.buildInstance((Attribute) smo.getInputParameter().get(ChangeOperatorParameter.ATTRIBUTE));
            String entityName = attributeDO.getEntity().getName();
            String dataTypeName = ((DataType) smo.getInputParameter().get(ChangeOperatorParameter.ATTRIBUTE_TYPE)).getName();
            Model targetModel = typhonMLInterface.changeTypeAttribute(attributeDO, entityName, dataTypeName, model);
            typhonQLInterface.changeTypeAttribute(attributeDO, entityName, targetModel);
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ChangeOperatorParameter.ATTRIBUTE_NAME + ", " + ChangeOperatorParameter.ATTRIBUTE_TYPE + "]");
        }
    }
}
