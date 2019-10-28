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
        if (containParameters(smo, Arrays.asList(ParametersKeyString.ENTITY, ParametersKeyString.DATABASE))) {
            EntityDO entityDO = EntityDOFactory.buildInstance((Entity) smo.getInputParameter().get(ParametersKeyString.ENTITY), false);
            Database database = (Database) smo.getInputParameter().get(ParametersKeyString.DATABASE);
            String sourceEntityNameInDatabase = typhonMLInterface.getEntityNameInDatabase(entityDO.getName(), model);
            DatabaseType targetDatabaseType = getDatabaseType(database);
            // Verify that an instance of the underlying database is running in the TyphonDL.
            if (!typhonDLInterface.isDatabaseRunning(targetDatabaseType.name(), database.getName())) {
                typhonDLInterface.createDatabase(targetDatabaseType.name(), database.getName());
            }
            Model targetModel = typhonMLInterface.deleteEntityMappings(entityDO.getName(), sourceEntityNameInDatabase, model);
            targetModel = typhonMLInterface.createDatabase(targetDatabaseType, database.getName(), targetModel);
            targetModel = typhonMLInterface.createNewEntityMappingInDatabase(targetDatabaseType, database.getName(), sourceEntityNameInDatabase, entityDO.getName(), targetModel);
            typhonQLInterface.createEntityType(entityDO, targetModel);
            WorkingSet data = typhonQLInterface.readAllEntityData(entityDO.getName(), model);
            typhonQLInterface.writeWorkingSetData(data, targetModel);
            typhonQLInterface.deleteWorkingSetData(data, model);
            typhonQLInterface.deleteEntityStructure(entityDO.getName(), model);
            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ParametersKeyString.ENTITY + ", " + ParametersKeyString.DATABASE + "]");
        }

    }

}
