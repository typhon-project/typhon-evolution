package com.typhon.evolutiontool.handlers.RelationHandlers;

import com.typhon.evolutiontool.entities.ParametersKeyString;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import typhonml.Model;

import java.util.Arrays;

public class RelationRemoveHandler extends BaseHandler {

    public RelationRemoveHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {

        String relationname;
        String entityname;
        Model targetModel;

        if (containParameters(smo, Arrays.asList(ParametersKeyString.RELATIONNAME, ParametersKeyString.ENTITYNAME))) {
            relationname = smo.getInputParameter().get(ParametersKeyString.RELATIONNAME).toString();
            entityname = smo.getInputParameter().get(ParametersKeyString.ENTITYNAME).toString();
            targetModel = typhonMLInterface.deleteRelationshipInEntity(relationname, entityname, model);
            typhonQLInterface.deleteRelationshipInEntity(relationname, entityname, targetModel);
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ParametersKeyString.RELATIONNAME + ", " + ParametersKeyString.ENTITYNAME + "]");
        }
    }


}
