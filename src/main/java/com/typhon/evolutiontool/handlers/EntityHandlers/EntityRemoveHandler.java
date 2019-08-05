package com.typhon.evolutiontool.handlers.EntityHandlers;

import com.typhon.evolutiontool.entities.EvolutionOperator;
import com.typhon.evolutiontool.entities.ParametersKeyString;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.entities.TyphonMLObject;
import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import org.springframework.beans.factory.annotation.Autowired;
import typhonml.Model;

import java.util.Arrays;

public class EntityRemoveHandler extends BaseHandler {

    @Autowired
    public EntityRemoveHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException, EvolutionOperationNotSupported {

        if(smo.getEvolutionOperator() == EvolutionOperator.REMOVE){
            return removeEntityType(smo, model);
        }
        else{
            return delegateToNext(smo, model);
        }
    }

    private Model removeEntityType(SMO smo, Model model) throws InputParameterException {
        Model targetModel;
        String entityname;

        if (containParameters(smo, Arrays.asList(ParametersKeyString.ENTITYNAME))) {
            entityname = smo.getInputParameter().get(ParametersKeyString.ENTITYNAME).toString();
            //If the entity is involved in a relationship. Abort
            if (typhonMLInterface.hasRelationship(entityname,model)) {
                throw new InputParameterException("Cannot delete an entity involved in a relationship. Remove the relationships first.");
            }
            //Delete data
            typhonQLInterface.deleteAllEntityData(entityname,model);
            //Delete structures
            typhonQLInterface.deleteEntityStructure(entityname, model);
            targetModel = typhonMLInterface.deleteEntityType(entityname, model);
            typhonMLInterface.deleteEntityMappings(entityname, model);
            //TODO Delete table mapping in TyphonML.

            return targetModel;
        }else {
            throw new InputParameterException("Missing parameters. Needed ["+ParametersKeyString.ENTITYNAME+"]");
        }

    }
}
