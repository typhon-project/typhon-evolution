package com.typhon.evolutiontool.handlers.attribute;

import com.typhon.evolutiontool.entities.ParametersKeyString;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import org.springframework.stereotype.Component;
import typhonml.Entity;
import typhonml.Model;
import typhonml.Relation;

import java.util.Arrays;

@Component("attributerename")
public class AttributeRenameHandler extends BaseHandler {

    public AttributeRenameHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Arrays.asList(ParametersKeyString.OLDATTRIBUTENAME, ParametersKeyString.NEWATTRIBUTENAME, ParametersKeyString.ENTITYNAME))) {
            String oldAttributeName = String.valueOf(smo.getInputParameter().get(ParametersKeyString.OLDATTRIBUTENAME));
            String newAttributeName = String.valueOf(smo.getInputParameter().get(ParametersKeyString.NEWATTRIBUTENAME));
            String entityName = String.valueOf(smo.getInputParameter().get(ParametersKeyString.ENTITYNAME));
            Model targetModel = typhonMLInterface.renameAttribute(oldAttributeName, newAttributeName, entityName, model);
            typhonQLInterface.renameAttribute(oldAttributeName, newAttributeName, entityName, targetModel);
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ParametersKeyString.OLDATTRIBUTENAME + ", " + ParametersKeyString.NEWATTRIBUTENAME + ", " + ParametersKeyString.ENTITYNAME + "]");
        }
    }

}
