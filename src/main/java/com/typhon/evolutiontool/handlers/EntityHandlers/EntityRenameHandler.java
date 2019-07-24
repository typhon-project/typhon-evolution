package com.typhon.evolutiontool.handlers.EntityHandlers;

import com.typhon.evolutiontool.entities.EvolutionOperator;
import com.typhon.evolutiontool.entities.ParametersKeyString;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import typhonml.Model;

import java.util.Arrays;

public class EntityRenameHandler extends BaseHandler {

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException, EvolutionOperationNotSupported {

        if(smo.getEvolutionOperator() == EvolutionOperator.RENAME){
            return renameEntityType(smo, model);
        }
        else{
            return delegateToNext(smo, model);
        }
    }

    public Model renameEntityType(SMO smo, Model model) throws InputParameterException {
        String oldEntityName,newEntityName;
        Model targetModel;

        if (containParameters(smo, Arrays.asList(ParametersKeyString.ENTITYNAME, ParametersKeyString.NEWENTITYNAME))) {
            oldEntityName = smo.getInputParameter().get(ParametersKeyString.ENTITYNAME).toString();
            newEntityName = smo.getInputParameter().get(ParametersKeyString.NEWENTITYNAME).toString();
            typhonQLInterface.renameEntity(oldEntityName, newEntityName,model);
            targetModel = typhonMLInterface.renameEntity(oldEntityName, newEntityName, model);
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameter");
        }
    }
}
