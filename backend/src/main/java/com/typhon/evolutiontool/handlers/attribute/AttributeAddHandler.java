package com.typhon.evolutiontool.handlers.attribute;

import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import com.typhon.evolutiontool.utils.DataTypeDOFactory;
import com.typhon.evolutiontool.utils.EntityDOFactory;
import typhonml.DataType;
import typhonml.Entity;
import typhonml.Model;

import java.util.Arrays;

public class AttributeAddHandler extends BaseHandler {

    public AttributeAddHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Arrays.asList(ParametersKeyString.ENTITY, ParametersKeyString.ATTRIBUTENAME, ParametersKeyString.ATTRIBUTETYPE))) {
            EntityDO entityDO = EntityDOFactory.buildInstance((Entity) smo.getInputParameter().get(ParametersKeyString.ENTITY));
            String attributeName = String.valueOf(smo.getInputParameter().get(ParametersKeyString.ATTRIBUTENAME));
            String attributeImportedNamespace = String.valueOf(smo.getInputParameter().get(ParametersKeyString.ATTRIBUTEIMPORTEDNAMESPACE));
            DataTypeDO dataTypeDO = DataTypeDOFactory.buildInstance((DataType) smo.getInputParameter().get(ParametersKeyString.ATTRIBUTETYPE));
            AttributeDO attributeDO = new AttributeDOImpl(attributeName, attributeImportedNamespace, dataTypeDO, entityDO);
            Model targetModel = typhonMLInterface.addAttribute(attributeDO, entityDO.getName(), model);
            typhonQLInterface.addAttribute(attributeDO, entityDO.getName(), targetModel);
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ParametersKeyString.ENTITY + ", " + ParametersKeyString.ATTRIBUTENAME + ", " + ParametersKeyString.ATTRIBUTETYPE + "]");
        }
    }
}
