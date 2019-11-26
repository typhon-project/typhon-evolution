package com.typhon.evolutiontool.handlers.entity;

import com.typhon.evolutiontool.entities.ChangeOperatorParameter;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import typhonml.Model;

import java.util.Collections;

public class EntityRemoveHandler extends BaseHandler {

    public EntityRemoveHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        Model targetModel;
        String entityname;

        if (containParameters(smo, Collections.singletonList(ChangeOperatorParameter.ENTITY_NAME))) {
            entityname = smo.getInputParameter().get(ChangeOperatorParameter.ENTITY_NAME).toString();
            String sourceEntityNameInDatabase = typhonMLInterface.getEntityNameInDatabase(entityname, model);
            //If the entity is involved in a relationship. Abort
            if (typhonMLInterface.hasRelationship(entityname, model)) {
                throw new InputParameterException("Cannot delete an entity involved in a relationship. Remove the relationships first.");
            }
            //Delete data
            typhonQLInterface.deleteAllEntityData(entityname, model);
            //Delete structures
            typhonQLInterface.dropEntity(entityname);

            targetModel = typhonMLInterface.deleteEntityMappings(entityname, sourceEntityNameInDatabase, model);
            targetModel = typhonMLInterface.deleteEntityType(entityname, targetModel);

            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ChangeOperatorParameter.ENTITY_NAME + "]");
        }
    }

}
