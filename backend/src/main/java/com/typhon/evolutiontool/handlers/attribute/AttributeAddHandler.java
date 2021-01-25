package com.typhon.evolutiontool.handlers.attribute;

import com.typhon.evolutiontool.entities.AttributeDO;
import com.typhon.evolutiontool.entities.ChangeOperatorParameter;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import com.typhon.evolutiontool.utils.AttributeDOFactory;
import typhonml.*;

import java.util.Collections;

public class AttributeAddHandler extends BaseHandler {

    public AttributeAddHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Collections.singletonList(ChangeOperatorParameter.ATTRIBUTE))) {
            AttributeDO attributeDO;
            Object attribute = smo.getInputParameter().get(ChangeOperatorParameter.ATTRIBUTE);
            if (attribute instanceof AddAttribute) {
                attributeDO = AttributeDOFactory.buildInstance((AddAttribute) attribute);
            } else {
                throw new InputParameterException("The input parameter 'attribute' type is not one of the required types: AddPrimitiveDataTypeAttribute, AddCustomDataTypeAttribute, AddAttribute");
            }

            //TyphonML
            Model targetModel = typhonMLInterface.addAttribute(attributeDO, attributeDO.getEntity().getName(), model);
            targetModel = typhonMLInterface.removeCurrentChangeOperator(targetModel);

            //TyphonQL
            typhonQLInterface.uploadSchema(targetModel);
            typhonQLInterface.addAttribute(attributeDO, attributeDO.getEntity().getName());

            return targetModel;
        } else {
            throw new InputParameterException("Missing parameter. Needed [" + ChangeOperatorParameter.ATTRIBUTE + "]");
        }
    }
}
