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

import java.util.Arrays;
import java.util.stream.Collectors;

public class EntityNewMigrateHandler extends BaseHandler {

    public EntityNewMigrateHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    /**
     * Migrates data of entity in sourceModel (read) to entity in targetModel (write).
     * Data is then deleted from sourceModel.
     */
    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Arrays.asList(ChangeOperatorParameter.ENTITY, ChangeOperatorParameter.DATABASE))) {
            EntityDO entityDO = EntityDOFactory.buildInstance((Entity) smo.getInputParameter().get(ChangeOperatorParameter.ENTITY), false);
            Database sourceDatabase = typhonMLInterface.getEntityDatabase(entityDO.getName(), model);
            Database targetDatabase = (Database) smo.getInputParameter().get(ChangeOperatorParameter.DATABASE);
            String sourceEntityNameInDatabase = typhonMLInterface.getEntityNameInDatabase(entityDO.getName(), model);
            String sourceEntityName = entityDO.getName();
            DatabaseType targetDatabaseType = getDatabaseType(targetDatabase);

            //Typhon QL
            try {
                //Select the source entity data
                WorkingSet entityData = typhonQLInterface.selectEntityData(sourceEntityName, entityDO.getAttributes().keySet(), entityDO.getRelations().stream().map(RelationDO::getName).collect(Collectors.toList()), null, null);

                //Delete the source entity
                typhonQLInterface.dropEntity(sourceEntityName);

                //Typhon ML: delete old entity and its mappings
                Model targetModel = typhonMLInterface.deleteEntityMappings(sourceEntityName, sourceEntityNameInDatabase, model);
                targetModel = typhonMLInterface.deleteEntityType(sourceEntityName, targetModel);
                //Upload the new XMI to the polystore
                typhonQLInterface.uploadSchema(targetModel);

                //Typhon ML
                targetModel = typhonMLInterface.createEntityType(targetModel, entityDO);
                targetModel = typhonMLInterface.createNewEntityMappingInDatabase(targetDatabaseType, targetDatabase.getName(), sourceEntityName, sourceEntityName, targetModel);
                targetModel = typhonMLInterface.removeCurrentChangeOperator(targetModel);

                //Upload the new XMI to the polystore
                typhonQLInterface.uploadSchema(targetModel);

                //Create the new entity, with its attributes and relations
                typhonQLInterface.createEntity(entityDO, targetDatabase.getName());

                //Insert the source entity data into the target entity
                typhonQLInterface.insertEntityData(sourceEntityName, entityData, entityDO);

                return targetModel;
            } catch (Exception exception) {
                logger.error("Error while migrating the entity. Reverting the migration...");
                return model;
            }
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ChangeOperatorParameter.ENTITY + ", " + ChangeOperatorParameter.DATABASE + "]");
        }

    }

}
