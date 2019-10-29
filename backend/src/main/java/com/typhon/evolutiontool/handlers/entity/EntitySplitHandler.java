package com.typhon.evolutiontool.handlers.entity;

import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import com.typhon.evolutiontool.utils.EntityDOFactory;
import typhonml.Database;
import typhonml.Entity;
import typhonml.Model;

public abstract class EntitySplitHandler extends BaseHandler {

    EntitySplitHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public abstract Model handle(SMO smo, Model model) throws InputParameterException;

    Model splitEntity(SMO smo, Model model) {
        EntityDO sourceEntity = EntityDOFactory.buildInstance((Entity) smo.getInputParameter().get(ParametersKeyString.ENTITY), false);
        EntityDO firstNewEntity = EntityDOFactory.buildInstance((Entity) smo.getInputParameter().get(ParametersKeyString.FIRSTNEWENTITY), false);
        EntityDO secondNewEntity = EntityDOFactory.buildInstance((Entity) smo.getInputParameter().get(ParametersKeyString.SECONDNEWENTITY), false);
        String sourceEntityNameInDatabase = typhonMLInterface.getEntityNameInDatabase(sourceEntity.getName(), model);

        Model targetModel = typhonMLInterface.createEntityType(model, firstNewEntity);
        targetModel = typhonMLInterface.createEntityType(targetModel, secondNewEntity);
        RelationDO relationDO = new RelationDOImpl("splitRelation", secondNewEntity.getName(), firstNewEntity, secondNewEntity, null, false, CardinalityDO.ONE);
        targetModel = typhonMLInterface.createRelationship(relationDO, targetModel);
        Database sourceDatabase = typhonMLInterface.getEntityDatabase(sourceEntity.getName(), targetModel);
        DatabaseType sourceDatabaseType = getDatabaseType(sourceDatabase);
        targetModel = typhonMLInterface.deleteEntityMappings(sourceEntity.getName(), sourceEntityNameInDatabase, targetModel);
        targetModel = typhonMLInterface.createNewEntityMappingInDatabase(sourceDatabaseType, sourceDatabase.getName(), firstNewEntity.getName(), firstNewEntity.getName(), targetModel);
        targetModel = typhonMLInterface.createNewEntityMappingInDatabase(sourceDatabaseType, sourceDatabase.getName(), secondNewEntity.getName(), secondNewEntity.getName(), targetModel);
        targetModel = typhonMLInterface.deleteEntityType(sourceEntity.getName(), targetModel);
        return targetModel;
    }
}
