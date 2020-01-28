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


public class AttributeRemoveHandler extends BaseHandler {

    public AttributeRemoveHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Collections.singletonList(ChangeOperatorParameter.ATTRIBUTE))) {
            AttributeDO attributeDO = AttributeDOFactory.buildInstance((Attribute) smo.getInputParameter().get(ChangeOperatorParameter.ATTRIBUTE));
            String entityName = attributeDO.getEntity().getName();

            //TyphonQL
            typhonQLInterface.removeAttribute(entityName, attributeDO.getName());

            //TyphonML
            Model targetModel = typhonMLInterface.removeAttribute(attributeDO.getName(), entityName, model);
            targetModel = typhonMLInterface.removeCurrentChangeOperator(targetModel);

            typhonQLInterface.uploadSchema(targetModel);

            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ChangeOperatorParameter.ATTRIBUTE + ", " + ChangeOperatorParameter.ENTITY_NAME + "]");
        }
    }


}
