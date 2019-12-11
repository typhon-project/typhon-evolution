package com.typhon.evolutiontool.handlers.entity;

import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import com.typhon.evolutiontool.utils.EntityDOFactory;
import com.typhon.evolutiontool.utils.WorkingSetFactory;
import typhonml.Database;
import typhonml.Entity;
import typhonml.Model;

import java.util.Arrays;

public class EntitySplitHorizontalHandler extends BaseHandler {

    public EntitySplitHorizontalHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Arrays.asList(ChangeOperatorParameter.ENTITY, ChangeOperatorParameter.NEW_ENTITY_NAME, ChangeOperatorParameter.ENTITY_SPLIT_ATTRIBUTE, ChangeOperatorParameter.ENTITY_SPLIT_EXPRESSION))) {
            EntityDO firstEntityDO = EntityDOFactory.buildInstance((Entity) smo.getInputParameter().get(ChangeOperatorParameter.ENTITY), false);
            String newEntityName = String.valueOf(smo.getInputParameter().get(ChangeOperatorParameter.NEW_ENTITY_NAME));
            String splitAttributeName = String.valueOf(smo.getInputParameter().get(ChangeOperatorParameter.ENTITY_SPLIT_ATTRIBUTE));
            String splitAttributeValue = String.valueOf(smo.getInputParameter().get(ChangeOperatorParameter.ENTITY_SPLIT_EXPRESSION));
            EntityDO secondEntityDO = new EntityDOImpl();
            secondEntityDO.setName(newEntityName);
            secondEntityDO.getAttributes().putAll(firstEntityDO.getAttributes());
            secondEntityDO.getRelations().addAll(firstEntityDO.getRelations());

            //TyphonML
            //Check entity self referencing relations
            checkEntityRelations(firstEntityDO.getName(), secondEntityDO);
            Model targetModel = typhonMLInterface.createEntityType(model, secondEntityDO);
            RelationDO relationDO = new RelationDOImpl("to_" + firstEntityDO.getName(), firstEntityDO.getName(), secondEntityDO, firstEntityDO, null, false, CardinalityDO.ONE);
            targetModel = typhonMLInterface.createRelationship(relationDO, targetModel);
            Database sourceDatabase = typhonMLInterface.getEntityDatabase(firstEntityDO.getName(), targetModel);
            DatabaseType sourceDatabaseType = getDatabaseType(sourceDatabase);
            targetModel = typhonMLInterface.createNewEntityMappingInDatabase(sourceDatabaseType, sourceDatabase.getName(), secondEntityDO.getName(), secondEntityDO.getName(), targetModel);
            targetModel = typhonMLInterface.removeCurrentChangeOperator(targetModel);

            //TyphonQL
            //Create the new entity
            typhonQLInterface.createEntity(secondEntityDO.getName(), sourceDatabase.getName(), targetModel);
            //Create the new entity attributes
            if (secondEntityDO.getAttributes() != null && !secondEntityDO.getAttributes().isEmpty()) {
                for (String attributeName : secondEntityDO.getAttributes().keySet()) {
                    typhonQLInterface.createEntityAttribute(secondEntityDO.getName(), attributeName, secondEntityDO.getAttributes().get(attributeName).getName(), model);
                }
            }
            //Create the new entity relationships
            if (secondEntityDO.getRelations() != null && !secondEntityDO.getRelations().isEmpty()) {
                for (RelationDO secondEntityRelationDO : secondEntityDO.getRelations()) {
                    boolean isRelationSelfReferencing = firstEntityDO.getName().equals(relationDO.getTypeName());
                    typhonQLInterface.createEntityRelation(secondEntityDO.getName(), secondEntityRelationDO.getName(), secondEntityRelationDO.isContainment(), secondEntityRelationDO.getTypeName(), secondEntityRelationDO.getCardinality(), model);
                }
            }
            //Create a new relation between the source entity and the new entity
            typhonQLInterface.createRelationshipType(relationDO, targetModel);
            //Select the source entity data for the attribute and the value
            WorkingSet dataSource = typhonQLInterface.readEntityDataEqualAttributeValue(firstEntityDO.getName(), splitAttributeName, splitAttributeValue, model);
            //Create a working set containing the source entity data adapted for the new entity
            WorkingSet dataTarget = WorkingSetFactory.createEmptyWorkingSet();
            dataTarget.setEntityRows(secondEntityDO.getName(), dataSource.getEntityInstanceRows(firstEntityDO.getName()));
            //Insert the adapted data in the new entity
            typhonQLInterface.writeWorkingSetData(dataTarget, targetModel);
            //Delete the source entity data concerned by the attribute and the value
            typhonQLInterface.deleteWorkingSetData(dataSource, targetModel);

            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ChangeOperatorParameter.ENTITY + ", " + ChangeOperatorParameter.NEW_ENTITY_NAME + ", " + ChangeOperatorParameter.ENTITY_SPLIT_ATTRIBUTE + ", " + ChangeOperatorParameter.ENTITY_SPLIT_EXPRESSION + "]");
        }

    }


}
