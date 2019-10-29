package com.typhon.evolutiontool.handlers.entity;

import com.typhon.evolutiontool.entities.ChangeOperatorParameter;
import com.typhon.evolutiontool.entities.SMO;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import typhonml.Model;

import java.util.Arrays;

public class EntitySplitVerticalHandler extends EntitySplitHandler {

    public EntitySplitVerticalHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    /**
     * Partially migrates the instances of sourceEntity to a new entity targetEntity. Only the values
     * of attributes [attributesNames] are migrated. The link between the instances of entity1 and entity2 is
     * kept via a new one-to-one relationship relName.
     *
     * @param smo the change operator to apply
     * @return the updated model, after the change operator has been applied
     * @throws InputParameterException when a mandatory parameter is missing
     */
    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Arrays.asList(ChangeOperatorParameter.ENTITY, ChangeOperatorParameter.FIRST_NEW_ENTITY, ChangeOperatorParameter.SECOND_NEW_ENTITY))) {
            //TyphonML
            Model targetModel = splitEntity(smo, model);

            //TyphonQL
            //TODO Data Manipulation
            //1. retrieve all data from sourceEntity for the firstNewEntity attributes
            //2. retrieve all data from sourceEntity for the secondNewEntity attributes
//            typhonQLInterface.createEntityType(firstNewEntity, targetModel);
//            typhonQLInterface.createEntityType(secondNewEntity, targetModel);
//            typhonQLInterface.createRelationshipType(relationDO, targetModel);

            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ChangeOperatorParameter.ENTITY + ", " + ChangeOperatorParameter.FIRST_NEW_ENTITY + ", " + ChangeOperatorParameter.SECOND_NEW_ENTITY + "]");
        }
    }


}
