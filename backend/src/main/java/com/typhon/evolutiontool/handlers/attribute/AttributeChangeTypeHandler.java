package com.typhon.evolutiontool.handlers.attribute;

import com.typhon.evolutiontool.datatypes.DataTypeDO;
import com.typhon.evolutiontool.entities.AttributeDO;
import com.typhon.evolutiontool.entities.ChangeOperatorParameter;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import com.typhon.evolutiontool.utils.AttributeDOFactory;
import com.typhon.evolutiontool.utils.DataTypeDOFactory;
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
        throw new UnsupportedOperationException("The attribute change type operator is not yet supported by QL.");
//        if (containParameters(smo, Arrays.asList(ChangeOperatorParameter.ATTRIBUTE, ChangeOperatorParameter.ATTRIBUTE_TYPE))) {
//            AttributeDO attributeDO = AttributeDOFactory.buildInstance((Attribute) smo.getInputParameter().get(ChangeOperatorParameter.ATTRIBUTE));
//            String entityName = attributeDO.getEntity().getName();
//            DataTypeDO dataTypeDO = DataTypeDOFactory.buildInstance((DataType) smo.getInputParameter().get(ChangeOperatorParameter.ATTRIBUTE_TYPE));
//            if (dataTypeDO != null) {
//                //TyphonQL
//                typhonQLInterface.changeTypeAttribute(attributeDO.getName(), dataTypeDO, entityName);
//
//                //TyphonML
//                Model targetModel = typhonMLInterface.changeTypeAttribute(attributeDO, entityName, dataTypeDO, model);
//                targetModel = typhonMLInterface.removeCurrentChangeOperator(targetModel);
//
//                typhonQLInterface.uploadSchema(targetModel);
//
//                return targetModel;
//            } else {
//                throw new InputParameterException("Missing attribute type parameter. Needed [" + ChangeOperatorParameter.ATTRIBUTE_TYPE + "]");
//            }
//        } else {
//            throw new InputParameterException("Missing parameters. Needed [" + ChangeOperatorParameter.ATTRIBUTE + ", " + ChangeOperatorParameter.ATTRIBUTE_TYPE + "] + ");
//        }
    }
}
