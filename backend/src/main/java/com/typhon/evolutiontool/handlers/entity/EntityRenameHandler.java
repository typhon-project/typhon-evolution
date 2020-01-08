package com.typhon.evolutiontool.handlers.entity;

import com.typhon.evolutiontool.entities.ChangeOperatorParameter;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import typhonml.Model;

import java.util.Arrays;

public class EntityRenameHandler extends BaseHandler {

    public EntityRenameHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Arrays.asList(ChangeOperatorParameter.ENTITY_NAME, ChangeOperatorParameter.NEW_ENTITY_NAME))) {
            String oldEntityName = String.valueOf(smo.getInputParameter().get(ChangeOperatorParameter.ENTITY_NAME));
            String newEntityName = String.valueOf(smo.getInputParameter().get(ChangeOperatorParameter.NEW_ENTITY_NAME));
            Model targetModel = typhonMLInterface.renameEntity(oldEntityName, newEntityName, model);
            targetModel = typhonMLInterface.removeCurrentChangeOperator(targetModel);
            typhonQLInterface.renameEntity(oldEntityName, newEntityName);
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ChangeOperatorParameter.ENTITY_NAME + ", " + ChangeOperatorParameter.NEW_ENTITY_NAME + "]");
        }
    }
}
