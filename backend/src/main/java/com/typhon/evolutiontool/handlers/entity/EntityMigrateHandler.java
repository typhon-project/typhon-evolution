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
            DatabaseType targetDatabaseType = getDatabaseType(database);

            //Typhon ML
            Model targetModel = typhonMLInterface.deleteEntityMappings(entityDO.getName(), sourceEntityNameInDatabase, model);
            targetModel = typhonMLInterface.createDatabase(targetDatabaseType, database.getName(), targetModel);
            targetModel = typhonMLInterface.createNewEntityMappingInDatabase(targetDatabaseType, database.getName(), sourceEntityNameInDatabase, entityDO.getName(), targetModel);

            //Typhon QL
            //Create the entity
            typhonQLInterface.createEntity(entityDO.getName(), database.getName(), model);
            //Create the entity attributes
            if (entityDO.getAttributes() != null && !entityDO.getAttributes().isEmpty()) {
                for (String attributeName : entityDO.getAttributes().keySet()) {
                    typhonQLInterface.createEntityAttribute(entityDO.getName(), attributeName, entityDO.getAttributes().get(attributeName).getName(), model);
                }
            }
            //Create the entity relationships
            if (entityDO.getRelations() != null && !entityDO.getRelations().isEmpty()) {
                for (RelationDO relationDO : entityDO.getRelations()) {
                    typhonQLInterface.createEntityRelation(entityDO.getName(), relationDO.getName(), relationDO.isContainment(), relationDO.getTypeName(), relationDO.getCardinality(), model);
                }
            }
            //Select the source entity data
            typhonQLInterface.selectEntityData(entityDO.getName(), model);
            //WorkingSet data = typhonQLInterface.readAllEntityData(entityDO.getName(), model);
            //Insert the source entity data into the target entity
            typhonQLInterface.insertEntityData(entityDO.getName(), entityDO.getAttributes().keySet(), model);
            //typhonQLInterface.writeWorkingSetData(data, targetModel);
            //Delete the source entity
            typhonQLInterface.dropEntity(entityDO.getName(), model);
            //typhonQLInterface.deleteWorkingSetData(data, model);
            //typhonQLInterface.deleteEntityStructure(entityDO.getName(), model);
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ChangeOperatorParameter.ENTITY + ", " + ChangeOperatorParameter.DATABASE + "]");
        }

    }

}
