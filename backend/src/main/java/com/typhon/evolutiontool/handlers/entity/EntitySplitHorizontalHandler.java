package com.typhon.evolutiontool.handlers.entity;

import com.typhon.evolutiontool.entities.ChangeOperatorParameter;
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
        if (containParameters(smo, Arrays.asList(ChangeOperatorParameter.ENTITY, ChangeOperatorParameter.FIRST_NEW_ENTITY, ChangeOperatorParameter.SECOND_NEW_ENTITY, ChangeOperatorParameter.ATTRIBUTE_NAME, ChangeOperatorParameter.ATTRIBUTE_VALUE))) {
            //TyphonML
            Model targetModel = splitEntity(smo, model);

            //TyphonQL
//            String attributeName = smo.getInputParameter().get(ChangeOperatorParameter.ATTRIBUTENAME).toString();
//            String attributeValue = smo.getInputParameter().get(ChangeOperatorParameter.ATTRIBUTEVALUE).toString();
//            WorkingSet dataTarget = WorkingSetFactory.createEmptyWorkingSet();
//            WorkingSet dataSource = typhonQLInterface.readEntityDataEqualAttributeValue(sourceEntityName, attributeName, attributeValue, model);
//            dataTarget.setEntityRows(targetEntityName, dataSource.getEntityInstanceRows(sourceEntityName));
//            typhonQLInterface.writeWorkingSetData(dataTarget, targetModel);
//            typhonQLInterface.deleteWorkingSetData(dataSource, model);

            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ChangeOperatorParameter.ENTITY + ", " + ChangeOperatorParameter.FIRST_NEW_ENTITY + ", " + ChangeOperatorParameter.SECOND_NEW_ENTITY + ", " + ChangeOperatorParameter.ATTRIBUTE_NAME + ", " + ChangeOperatorParameter.ATTRIBUTE_VALUE + "]");
        }

    }


}
