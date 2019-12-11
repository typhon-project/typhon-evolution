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

            //Typhon ML
            //Check entity self referencing relations
            checkEntityRelations(sourceEntityName, entityDO);
            Model targetModel = typhonMLInterface.createEntityType(model, entityDO);
            targetModel = typhonMLInterface.deleteEntityMappings(sourceEntityName, sourceEntityNameInDatabase, targetModel);
            targetModel = typhonMLInterface.deleteEntityType(sourceEntityName, targetModel);
//            targetModel = typhonMLInterface.createDatabase(targetDatabaseType, database.getName(), targetModel);
            targetModel = typhonMLInterface.createNewEntityMappingInDatabase(targetDatabaseType, database.getName(), sourceEntityNameInDatabase, targetEntityName, targetModel);
            targetModel = typhonMLInterface.removeCurrentChangeOperator(targetModel);

            //Upload the new XMI to the polystore
            typhonQLInterface.uploadSchema(targetModel);

            //Typhon QL
            try {
                //Create the entity
                typhonQLInterface.createEntity(targetEntityName, database.getName(), targetModel);
                //Create the entity attributes
                if (entityDO.getAttributes() != null && !entityDO.getAttributes().isEmpty()) {
                    for (String attributeName : entityDO.getAttributes().keySet()) {
                        typhonQLInterface.createEntityAttribute(targetEntityName, attributeName, entityDO.getAttributes().get(attributeName).getName(), targetModel);
                    }
                }
                //Create the entity relationships
                if (entityDO.getRelations() != null && !entityDO.getRelations().isEmpty()) {
                    for (RelationDO relationDO : entityDO.getRelations()) {
                        typhonQLInterface.createEntityRelation(targetEntityName, relationDO.getName(), relationDO.isContainment(), relationDO.getTypeName(), relationDO.getCardinality(), targetModel);
                    }
                }
                //Select the source entity data
                WorkingSet ws = typhonQLInterface.selectEntityData(sourceEntityName, targetModel);
                //Insert the source entity data into the target entity
                typhonQLInterface.insertEntityData(targetEntityName, entityDO.getAttributes().keySet(), ws, targetModel);
                //Delete the source entity
                typhonQLInterface.dropEntity(sourceEntityName, targetModel);
            } catch (Exception exception) {
                //Revert Typhon QL operations
                typhonQLInterface.dropEntity(targetEntityName, targetModel);
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
