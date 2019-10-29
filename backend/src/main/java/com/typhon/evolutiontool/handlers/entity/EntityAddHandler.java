package com.typhon.evolutiontool.handlers.entity;

import com.typhon.evolutiontool.entities.EntityDO;
import com.typhon.evolutiontool.entities.ParametersKeyString;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import com.typhon.evolutiontool.utils.EntityDOFactory;
import typhonml.Entity;
import typhonml.Model;

import java.util.Collections;

public class EntityAddHandler extends BaseHandler {

    public EntityAddHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (this.containParameters(smo, Collections.singletonList(ParametersKeyString.ENTITY))) {
            // Verify that an instance of the underlying database is running in the TyphonDL.
//            if (!typhonDLInterface.isDatabaseRunning(databasetype, databasename)) {
//                typhonDLInterface.createDatabase(databasetype, databasename);
//            }
            //Executing evolution operations
//            newEntity = smo.getPOJOFromInputParameter(ParametersKeyString.ENTITY, EntityDOJsonImpl.class);
            EntityDO entityDO = EntityDOFactory.buildInstance((Entity) smo.getInputParameter().get(ParametersKeyString.ENTITY), false);
            Model targetModel = typhonMLInterface.createEntityType(model, entityDO);
//            targetModel = typhonMLInterface.createDatabase(dbtype, databasename, targetModel);
//            targetModel = typhonMLInterface.createNewEntityMappingInDatabase(dbtype, databasename, logicalname, entityDO.getName(), targetModel);
            typhonQLInterface.createEntityType(entityDO, targetModel);
            return targetModel;
        } else
            throw new InputParameterException("Missing parameter. Needed [" + ParametersKeyString.ENTITY + "]");

    }

}
