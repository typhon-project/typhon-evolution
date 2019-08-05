package com.typhon.evolutiontool.handlers.EntityHandlers;

import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import typhonml.Model;

import java.util.Arrays;

@Component("entityadd")
public class EntityAddHandler extends BaseHandler {

    public EntityAddHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException, EvolutionOperationNotSupported {

        if(smo.getEvolutionOperator() == EvolutionOperator.ADD){
            return addEntityType(smo, model);
        }
        else{
            return delegateToNext(smo, model);
        }
    }

    private Model addEntityType(SMO smo, Model model) throws InputParameterException {
        EntityDO newEntity;
        String databasetype, databasename, logicalname;
        DatabaseType dbtype;
        Model targetModel;

        // Verify ParametersKeyString
        if(this.containParameters(smo, Arrays.asList(ParametersKeyString.ENTITY,ParametersKeyString.DATABASENAME,ParametersKeyString.DATABASETYPE, ParametersKeyString.TARGETLOGICALNAME))){
            databasetype = smo.getInputParameter().get(ParametersKeyString.DATABASETYPE).toString();
            dbtype = DatabaseType.valueOf(databasetype.toUpperCase());
            databasename = smo.getInputParameter().get(ParametersKeyString.DATABASENAME).toString();
            logicalname = smo.getInputParameter().get(ParametersKeyString.TARGETLOGICALNAME).toString();
            // Verify that an instance of the underlying database is running in the TyphonDL.
            if (!typhonDLInterface.isDatabaseRunning(databasetype, databasename)) {
                typhonDLInterface.createDatabase(databasetype, databasename);
            }
            //Executing evolution operations
//            newEntity = smo.getPOJOFromInputParameter(ParametersKeyString.ENTITY, EntityDOJsonImpl.class);
            newEntity = smo.getEntityDOFromInputParameter(ParametersKeyString.ENTITY);
            targetModel = typhonMLInterface.createEntityType(model, newEntity);
            targetModel = typhonMLInterface.createDatabase(dbtype, databasename, targetModel);
            targetModel = typhonMLInterface.createNewEntityMappingInDatabase(dbtype, databasename, logicalname, newEntity.getName(), targetModel);
            typhonQLInterface.createEntityType(newEntity,targetModel);
            return targetModel;
        }
        else
            throw new InputParameterException("Missing parameters. Needed ["+ParametersKeyString.ENTITY+", "+ParametersKeyString.DATABASENAME+", "+ParametersKeyString.DATABASETYPE+", "+ ParametersKeyString.TARGETLOGICALNAME+"]");

    }
}
