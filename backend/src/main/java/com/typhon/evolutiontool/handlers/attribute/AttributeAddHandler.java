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
import typhonml.Attribute;
import typhonml.Model;

import java.util.Collections;

public class AttributeAddHandler extends BaseHandler {

    public AttributeAddHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Collections.singletonList(ChangeOperatorParameter.ATTRIBUTE))) {
            AttributeDO attributeDO = AttributeDOFactory.buildInstance((Attribute) smo.getInputParameter().get(ChangeOperatorParameter.ATTRIBUTE));

            Model targetModel = typhonMLInterface.addAttribute(attributeDO, attributeDO.getEntity().getName(), model);
            typhonQLInterface.addAttribute(attributeDO, attributeDO.getEntity().getName(), targetModel);
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameter. Needed [" + ChangeOperatorParameter.ATTRIBUTE + "]");
        }
    }
}
