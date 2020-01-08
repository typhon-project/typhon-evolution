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

import java.util.Arrays;

public class AttributeRenameHandler extends BaseHandler {

    public AttributeRenameHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Arrays.asList(ChangeOperatorParameter.ATTRIBUTE, ChangeOperatorParameter.NEW_ATTRIBUTE_NAME))) {
            AttributeDO attributeDO = AttributeDOFactory.buildInstance((Attribute) smo.getInputParameter().get(ChangeOperatorParameter.ATTRIBUTE));
            String newAttributeName = String.valueOf(smo.getInputParameter().get(ChangeOperatorParameter.NEW_ATTRIBUTE_NAME));
            String entityName = attributeDO.getEntity().getName();
            Model targetModel = typhonMLInterface.renameAttribute(attributeDO.getName(), newAttributeName, entityName, model);
            typhonQLInterface.renameAttribute(attributeDO.getName(), newAttributeName, entityName);
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ChangeOperatorParameter.NEW_ATTRIBUTE_NAME + ", " + ChangeOperatorParameter.ENTITY_NAME + "]");
        }
    }

}
