package com.typhon.evolutiontool.handlers.EntityHandlers;

import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import org.springframework.beans.factory.annotation.Autowired;
import typhonml.Model;

import java.util.Arrays;

public class EntityMigrateHandler extends BaseHandler {

    @Autowired
    public EntityMigrateHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }


    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException, EvolutionOperationNotSupported {

        if(smo.getEvolutionOperator() == EvolutionOperator.MIGRATE){
            return migrateEntity(smo, model);
        }
        else{
            return delegateToNext(smo, model);
        }
    }


    /**
     * Migrates data of entity in sourceModel (read) to entity in targetModel (write).
     * Data is then deleted from sourceModel.
     * @param smo
     * @return
     * @throws InputParameterException
     */
    private Model migrateEntity(SMO smo, Model model) throws InputParameterException {
        typhonml.Entity entity;
        String entityname, databasetype, databasename, targetLogicalName;
        DatabaseType dbtype;
        WorkingSet data;
        Model targetModel;

        if (containParameters(smo, Arrays.asList(ParametersKeyString.ENTITYNAME, ParametersKeyString.DATABASENAME, ParametersKeyString.DATABASETYPE, ParametersKeyString.TARGETLOGICALNAME))) {
            entityname = smo.getInputParameter().get(ParametersKeyString.ENTITYNAME).toString();
            databasetype = smo.getInputParameter().get(ParametersKeyString.DATABASETYPE).toString();
            dbtype = DatabaseType.valueOf(databasetype.toUpperCase());
            databasename = smo.getInputParameter().get(ParametersKeyString.DATABASENAME).toString();
            targetLogicalName = smo.getInputParameter().get(ParametersKeyString.TARGETLOGICALNAME).toString();
            entity = typhonMLInterface.getEntityTypeFromName(entityname, model);
            // Verify that an instance of the underlying database is running in the TyphonDL.
            if (!typhonDLInterface.isDatabaseRunning(databasetype, databasename)) {
                typhonDLInterface.createDatabase(databasetype, databasename);
            }
            targetModel = typhonMLInterface.deleteEntityMappings(entityname, model);
            targetModel = typhonMLInterface.createDatabase(dbtype, databasename, targetModel);
            targetModel = typhonMLInterface.createNewEntityMappingInDatabase(dbtype,databasename, targetLogicalName, entityname, targetModel);
            typhonQLInterface.createEntityType(entity, targetModel);
            data = typhonQLInterface.readAllEntityData(entityname,model);
            typhonQLInterface.writeWorkingSetData(data,targetModel);
            typhonQLInterface.deleteWorkingSetData(data, model);
            typhonQLInterface.deleteEntityStructure(entityname, model);
            return targetModel;
        } else{
            throw new InputParameterException("Missing parameters. Needed ["+ParametersKeyString.ENTITY+", "+ParametersKeyString.FIRSTNEWENTITY+", "+ParametersKeyString.SECONDNEWENTITY+", "+ParametersKeyString.DATABASENAME+", "+ParametersKeyString.DATABASETYPE+"]");
        }

    }
}
