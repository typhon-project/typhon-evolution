package com.typhon.evolutiontool.handlers.entity;

import com.typhon.evolutiontool.entities.ChangeOperatorParameter;
import com.typhon.evolutiontool.entities.EntityDO;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import com.typhon.evolutiontool.utils.EntityDOFactory;
import typhonml.Database;
import typhonml.Entity;
import typhonml.Model;

import java.util.Collections;

public class EntityAddHandler extends BaseHandler {

    public EntityAddHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (this.containParameters(smo, Collections.singletonList(ChangeOperatorParameter.ENTITY))) {
            EntityDO entityDO = EntityDOFactory.buildInstance((Entity) smo.getInputParameter().get(ChangeOperatorParameter.ENTITY), false);
            //Retrieve source database information
            Database sourceDatabase = typhonMLInterface.getEntityDatabase(entityDO.getName(), model);
            if (sourceDatabase != null) {
                //Typhon ML
                Model targetModel = typhonMLInterface.createEntityType(model, entityDO);
                targetModel = typhonMLInterface.updateEntityMappingInDatabase(entityDO.getName(), sourceDatabase.getName(), targetModel);
                targetModel = typhonMLInterface.removeCurrentChangeOperator(targetModel);

                //Typhon QL
                //Upload the new XMI to the polystore
                typhonQLInterface.uploadSchema(targetModel);
                //Create the new entity, with its attributes and relations
                typhonQLInterface.createEntity(entityDO, sourceDatabase.getName());
                return targetModel;
            } else {
                throw new InputParameterException("No storage unit (e.g. table, collection) defined for the entity to add. See the documentation in the README file. Aborting the evolution operator");
            }
        } else
            throw new InputParameterException("Missing parameter. Needed [" + ChangeOperatorParameter.ENTITY + "]");

    }

}
