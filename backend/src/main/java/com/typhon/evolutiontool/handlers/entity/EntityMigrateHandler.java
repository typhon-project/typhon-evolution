package com.typhon.evolutiontool.handlers.entity;

import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import com.typhon.evolutiontool.utils.EntityDOFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import typhonml.Database;
import typhonml.Entity;
import typhonml.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityMigrateHandler extends BaseHandler {

    private Logger logger = LoggerFactory.getLogger(EntityMigrateHandler.class);

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
            List<String> queries = new ArrayList<>();
            //Create the entity
            queries.add(typhonQLInterface.createEntity(entityDO.getName(), database.getName()));
            //Create the entity attributes
            if (entityDO.getAttributes() != null && !entityDO.getAttributes().isEmpty()) {
                for (String attributeName : entityDO.getAttributes().keySet()) {
                    queries.add(typhonQLInterface.createEntityAttribute(entityDO.getName(), attributeName, entityDO.getAttributes().get(attributeName).getName()));
                }
            }
            //Create the entity relationships
            if (entityDO.getRelations() != null && !entityDO.getRelations().isEmpty()) {
                for (RelationDO relationDO : entityDO.getRelations()) {
                    queries.add(typhonQLInterface.createEntityRelation(entityDO.getName(), relationDO.getName(), relationDO.isContainment(), relationDO.getTypeName(), relationDO.getCardinality()));
                }
            }
            //Select the source entity data
            queries.add(typhonQLInterface.selectEntityData(entityDO.getName()));
            //WorkingSet data = typhonQLInterface.readAllEntityData(entityDO.getName(), model);
            //Insert the source entity data into the target entity
            //typhonQLInterface.writeWorkingSetData(data, targetModel);
            //Delete the source entity data
            //typhonQLInterface.deleteWorkingSetData(data, model);
            //Drop the source entity relationships

            //Drop the source entity
            //typhonQLInterface.deleteEntityStructure(entityDO.getName(), model);
            //Log the QL queries
            logger.info("\nMIGRATE ENTITY QL queries:\n");
            for (String query : queries) {
                logger.info(query);
            }
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ChangeOperatorParameter.ENTITY + ", " + ChangeOperatorParameter.DATABASE + "]");
        }

    }

}
