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
import java.util.Map;
import java.util.stream.Collectors;

public class EntityMigrateHandler extends BaseHandler {

    public EntityMigrateHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
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
            Database database = (Database) smo.getInputParameter().get(ChangeOperatorParameter.DATABASE);
            String sourceEntityNameInDatabase = typhonMLInterface.getEntityNameInDatabase(entityDO.getName(), model);
            String sourceEntityName = entityDO.getName();
            String targetEntityName = sourceEntityName + "_migrated";
            entityDO.setName(targetEntityName);
            DatabaseType targetDatabaseType = getDatabaseType(database);
            Map<String, String> incomingRelations = typhonMLInterface.getEntityIncomingRelations(sourceEntityName, model);

            //Typhon ML
            //Check entity self referencing relations
            checkEntityRelations(sourceEntityName, entityDO);
            Model targetModel = typhonMLInterface.createEntityType(model, entityDO);
//            targetModel = typhonMLInterface.createDatabase(targetDatabaseType, database.getName(), targetModel);
            targetModel = typhonMLInterface.createNewEntityMappingInDatabase(targetDatabaseType, database.getName(), targetEntityName, targetEntityName, targetModel);
            if (incomingRelations != null && !incomingRelations.isEmpty()) {
                for (String entityName : incomingRelations.keySet()) {
                    targetModel = typhonMLInterface.deleteRelationshipInEntity(incomingRelations.get(entityName), entityName, targetModel);
                }
            }
            targetModel = typhonMLInterface.removeCurrentChangeOperator(targetModel);

            //Typhon QL
            try {
                //Select the source entity data
                WorkingSet entityData = typhonQLInterface.selectEntityData(sourceEntityName, entityDO.getAttributes().keySet(), entityDO.getRelations().stream().map(RelationDO::getName).collect(Collectors.toList()), null, null);
                //Manipulate the source entity data (modify the entity name, to the new entity name)
                typhonQLInterface.updateEntityNameInSourceEntityData(entityData, sourceEntityName, targetEntityName);
                //Upload the new XMI to the polystore
                typhonQLInterface.uploadSchema(targetModel);
                //Create the new entity, with its attributes and relations
                typhonQLInterface.createEntity(entityDO, database.getName());
                //Drop entity incoming relations
                if (incomingRelations != null && !incomingRelations.isEmpty()) {
                    for (String entityName : incomingRelations.keySet()) {
                        typhonQLInterface.deleteRelationshipInEntity(incomingRelations.get(entityName), entityName);
                    }
                }
                //Drop the source entity relationships
                //TODO Drop relation is not yet implemented in TyphonQL
                if (entityDO.getRelations() != null && !entityDO.getRelations().isEmpty()) {
                    for (RelationDO relationDO : entityDO.getRelations()) {
                        typhonQLInterface.deleteRelationshipInEntity(relationDO.getName(), sourceEntityName);
                    }
                }
                //Insert the source entity data into the target entity
                typhonQLInterface.insertEntityData(targetEntityName, entityData, entityDO);
                //Delete the source entity
                typhonQLInterface.dropEntity(sourceEntityName);

                //Typhon ML: delete old entity and its mappings
                targetModel = typhonMLInterface.deleteEntityMappings(sourceEntityName, sourceEntityNameInDatabase, targetModel);
                targetModel = typhonMLInterface.deleteEntityType(sourceEntityName, targetModel);
                //Upload the new XMI to the polystore
                typhonQLInterface.uploadSchema(targetModel);
            } catch (Exception exception) {
                logger.error("Error while migrating the entity. Reverting the migration...");
                //Revert Typhon QL operations
                typhonQLInterface.dropEntity(targetEntityName);
                //Reset the source XMI to the polystore
                typhonQLInterface.uploadSchema(model);
                return model;
            }
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ChangeOperatorParameter.ENTITY + ", " + ChangeOperatorParameter.DATABASE + "]");
        }

    }

}
