package com.typhon.evolutiontool.handlers.EntityHandlers;

import com.typhon.evolutiontool.entities.DatabaseType;
import com.typhon.evolutiontool.entities.ParametersKeyString;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.entities.WorkingSet;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import com.typhon.evolutiontool.utils.WorkingSetFactory;
import typhonml.Model;

import java.util.Arrays;

public class EntitySplitHorizontalHandler extends BaseHandler {

    public EntitySplitHorizontalHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }


    /**
     * Migrates the data instances of sourceEntity that has a given attributeValue of their attribute
     * attributeName to a newly created targetEntity with the same structure.
     * This new entity is mapped to a new table/collection/.. in the same database as sourceEntity.
     *
     * @param smo
     * @return
     * @throws InputParameterException
     */
    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        String sourceEntityName, targetEntityName, targetLogicalName, attributeName, attributeValue, databasename, databasetype;
        WorkingSet dataSource, dataTarget;
        DatabaseType dbtype;
        Model targetModel;

        dataTarget = WorkingSetFactory.createEmptyWorkingSet();
        if (containParameters(smo, Arrays.asList(ParametersKeyString.SOURCEENTITYNAME, ParametersKeyString.TARGETENTITYNAME, ParametersKeyString.TARGETLOGICALNAME, ParametersKeyString.ATTRIBUTENAME, ParametersKeyString.ATTRIBUTEVALUE, ParametersKeyString.DATABASETYPE, ParametersKeyString.DATABASENAME))) {
            sourceEntityName = smo.getInputParameter().get(ParametersKeyString.SOURCEENTITYNAME).toString();
            targetEntityName = smo.getInputParameter().get(ParametersKeyString.TARGETENTITYNAME).toString();
            targetLogicalName = smo.getInputParameter().get(ParametersKeyString.TARGETLOGICALNAME).toString();
            databasetype = smo.getInputParameter().get(ParametersKeyString.DATABASETYPE).toString();
            databasename = smo.getInputParameter().get(ParametersKeyString.DATABASENAME).toString();
            attributeName = smo.getInputParameter().get(ParametersKeyString.ATTRIBUTENAME).toString();
            attributeValue = smo.getInputParameter().get(ParametersKeyString.ATTRIBUTEVALUE).toString();
            dbtype = DatabaseType.valueOf(databasetype.toUpperCase());
            targetModel = typhonMLInterface.copyEntityType(sourceEntityName, targetEntityName, model);
            targetModel = typhonMLInterface.createDatabase(dbtype, databasename, targetModel);
            // Create a new logical mapping for the created EntityDO type.
            targetModel = typhonMLInterface.createNewEntityMappingInDatabase(dbtype, databasename, targetLogicalName, targetEntityName, targetModel);
            dataSource = typhonQLInterface.readEntityDataEqualAttributeValue(sourceEntityName, attributeName, attributeValue, model);
            dataTarget.setEntityRows(targetEntityName, dataSource.getEntityInstanceRows(sourceEntityName));
            typhonQLInterface.writeWorkingSetData(dataTarget, targetModel);
            typhonQLInterface.deleteWorkingSetData(dataSource, model);

            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ParametersKeyString.SOURCEENTITYNAME + ", " + ParametersKeyString.TARGETENTITYNAME + ", " + ParametersKeyString.TARGETLOGICALNAME + ", " + ParametersKeyString.ATTRIBUTENAME + ", " + ParametersKeyString.ATTRIBUTEVALUE + ", " + ParametersKeyString.DATABASETYPE + ", " + ParametersKeyString.DATABASENAME + "]");
        }

    }


}
