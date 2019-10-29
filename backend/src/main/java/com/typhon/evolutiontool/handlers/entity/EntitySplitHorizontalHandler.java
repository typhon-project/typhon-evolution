package com.typhon.evolutiontool.handlers.entity;

import com.typhon.evolutiontool.entities.ParametersKeyString;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import typhonml.Model;

import java.util.Arrays;

public class EntitySplitHorizontalHandler extends EntitySplitHandler {

    public EntitySplitHorizontalHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }


    /**
     * Migrates the data instances of sourceEntity that has a given attributeValue of their attribute
     * attributeName to a newly created targetEntity with the same structure.
     * This new entity is mapped to a new table/collection/.. in the same database as sourceEntity.
     */
    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Arrays.asList(ParametersKeyString.ENTITY, ParametersKeyString.FIRSTNEWENTITY, ParametersKeyString.SECONDNEWENTITY, ParametersKeyString.ATTRIBUTENAME, ParametersKeyString.ATTRIBUTEVALUE))) {
            //TyphonML
            Model targetModel = splitEntity(smo, model);

            //TyphonQL
//            String attributeName = smo.getInputParameter().get(ParametersKeyString.ATTRIBUTENAME).toString();
//            String attributeValue = smo.getInputParameter().get(ParametersKeyString.ATTRIBUTEVALUE).toString();
//            WorkingSet dataTarget = WorkingSetFactory.createEmptyWorkingSet();
//            WorkingSet dataSource = typhonQLInterface.readEntityDataEqualAttributeValue(sourceEntityName, attributeName, attributeValue, model);
//            dataTarget.setEntityRows(targetEntityName, dataSource.getEntityInstanceRows(sourceEntityName));
//            typhonQLInterface.writeWorkingSetData(dataTarget, targetModel);
//            typhonQLInterface.deleteWorkingSetData(dataSource, model);

            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ParametersKeyString.ENTITY + ", " + ParametersKeyString.FIRSTNEWENTITY + ", " + ParametersKeyString.SECONDNEWENTITY + ", " + ParametersKeyString.ATTRIBUTENAME + ", " + ParametersKeyString.ATTRIBUTEVALUE + "]");
        }

    }


}
