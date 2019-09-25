package com.typhon.evolutiontool.handlers.entity;

import com.typhon.evolutiontool.entities.ParametersKeyString;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import org.springframework.stereotype.Component;
import typhonml.Model;

import java.util.Arrays;

@Component("entityrename")
public class EntityRenameHandler extends BaseHandler {

    public EntityRenameHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }


    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        String oldEntityName, newEntityName;
        Model targetModel;

        if (containParameters(smo, Arrays.asList(ParametersKeyString.ENTITYNAME, ParametersKeyString.NEWENTITYNAME))) {
            oldEntityName = smo.getInputParameter().get(ParametersKeyString.ENTITYNAME).toString();
            newEntityName = smo.getInputParameter().get(ParametersKeyString.NEWENTITYNAME).toString();
            typhonQLInterface.renameEntity(oldEntityName, newEntityName, model);
            targetModel = typhonMLInterface.renameEntity(oldEntityName, newEntityName, model);
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ParametersKeyString.ENTITYNAME + ", " + ParametersKeyString.NEWENTITYNAME + "]");
        }
    }

}
