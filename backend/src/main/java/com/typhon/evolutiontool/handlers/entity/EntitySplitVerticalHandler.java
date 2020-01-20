package com.typhon.evolutiontool.handlers.entity;

import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import com.typhon.evolutiontool.utils.DataTypeDOFactory;
import com.typhon.evolutiontool.utils.EntityDOFactory;
import com.typhon.evolutiontool.utils.RelationDOFactory;
import com.typhon.evolutiontool.utils.WorkingSetFactory;
import typhonml.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntitySplitVerticalHandler extends BaseHandler {

    public EntitySplitVerticalHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Arrays.asList(ChangeOperatorParameter.ENTITY, ChangeOperatorParameter.NEW_ENTITY_NAME, ChangeOperatorParameter.NEW_ENTITY_ATTRIBUTES, ChangeOperatorParameter.NEW_ENTITY_RELATIONS))) {
            EntityDO firstEntityDO = EntityDOFactory.buildInstance((Entity) smo.getInputParameter().get(ChangeOperatorParameter.ENTITY), false);
            String newEntityName = String.valueOf(smo.getInputParameter().get(ChangeOperatorParameter.NEW_ENTITY_NAME));
            Map<String, DataTypeDO> entityAttributes = buildEntityAttributes((List<Attribute>) smo.getInputParameter().get(ChangeOperatorParameter.NEW_ENTITY_ATTRIBUTES));
            List<RelationDO> entityRelations = ((List<Relation>) smo.getInputParameter().get(ChangeOperatorParameter.NEW_ENTITY_RELATIONS)).stream().map(relation -> RelationDOFactory.buildInstance(relation, false)).collect(Collectors.toList());
            EntityDO secondEntityDO = new EntityDOImpl();
            secondEntityDO.setName(newEntityName);
            secondEntityDO.getAttributes().putAll(entityAttributes);
            secondEntityDO.getRelations().addAll(entityRelations);
            //TyphonML
            //Check entity self referencing relations
            checkEntityRelations(firstEntityDO.getName(), secondEntityDO);
            Model targetModel = typhonMLInterface.createEntityType(model, secondEntityDO);
            RelationDO relationDO = new RelationDOImpl("to_" + firstEntityDO.getName(), firstEntityDO.getName(), secondEntityDO, firstEntityDO, null, false, CardinalityDO.ONE);
            targetModel = typhonMLInterface.createRelationship(relationDO, targetModel);
            for (String attributeName : entityAttributes.keySet()) {
                targetModel = typhonMLInterface.removeAttribute(attributeName, firstEntityDO.getName(), targetModel);
            }
            for (RelationDO relation : entityRelations) {
                targetModel = typhonMLInterface.deleteRelationshipInEntity(relation.getName(), firstEntityDO.getName(), targetModel);
            }
            Database sourceDatabase = typhonMLInterface.getEntityDatabase(firstEntityDO.getName(), targetModel);
            DatabaseType sourceDatabaseType = getDatabaseType(sourceDatabase);
            targetModel = typhonMLInterface.createNewEntityMappingInDatabase(sourceDatabaseType, sourceDatabase.getName(), secondEntityDO.getName(), secondEntityDO.getName(), targetModel);
            targetModel = typhonMLInterface.removeCurrentChangeOperator(targetModel);

            //TyphonQL
            //Create the new entity
            typhonQLInterface.createEntity(secondEntityDO.getName(), sourceDatabase.getName());
            //Create the new entity attributes
            if (secondEntityDO.getAttributes() != null && !secondEntityDO.getAttributes().isEmpty()) {
                for (String attributeName : secondEntityDO.getAttributes().keySet()) {
                    typhonQLInterface.createEntityAttribute(secondEntityDO.getName(), attributeName, secondEntityDO.getAttributes().get(attributeName).getName());
                }
            }
            //Create the new entity relationships
            if (secondEntityDO.getRelations() != null && !secondEntityDO.getRelations().isEmpty()) {
                for (RelationDO secondEntityRelationDO : secondEntityDO.getRelations()) {
                    boolean isRelationSelfReferencing = firstEntityDO.getName().equals(relationDO.getTypeName());
                    typhonQLInterface.createEntityRelation(secondEntityDO.getName(), secondEntityRelationDO.getName(), secondEntityRelationDO.isContainment(), secondEntityRelationDO.getTypeName(), secondEntityRelationDO.getCardinality());
                }
            }
            //Create a new relation between the source entity and the new entity
            typhonQLInterface.createRelationshipType(relationDO);
            //Select the source entity data for the attribute and the value
            WorkingSet dataSource = typhonQLInterface.readEntityDataSelectAttributes(firstEntityDO.getName(), entityAttributes.keySet());
            //Create a working set containing the source entity data adapted for the new entity
            WorkingSet dataTarget = WorkingSetFactory.createEmptyWorkingSet();
            dataTarget.addEntityRows(secondEntityDO.getName(), dataSource.getEntityRows(firstEntityDO.getName()));
            //Insert the adapted data in the new entity
            typhonQLInterface.writeWorkingSetData(dataTarget);
            //Remove the attributes from the source entity, which have been copied to the second entity
            for (String attributeName : secondEntityDO.getAttributes().keySet()) {
                typhonQLInterface.removeAttribute(firstEntityDO.getName(), attributeName);
            }
            //Remove the relations from the source entity, which have been copied to the second entity
            for (RelationDO secondEntityRelation : secondEntityDO.getRelations()) {
                typhonQLInterface.deleteRelationshipInEntity(secondEntityRelation.getName(), firstEntityDO.getName());
            }

            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ChangeOperatorParameter.ENTITY + ", " + ChangeOperatorParameter.NEW_ENTITY_NAME + ", " + ChangeOperatorParameter.NEW_ENTITY_ATTRIBUTES + ", " + ChangeOperatorParameter.NEW_ENTITY_RELATIONS + "]");
        }
    }

    private Map<String, DataTypeDO> buildEntityAttributes(List<Attribute> attributes) {
        Map<String, DataTypeDO> entityAttributes = new HashMap<>();
        attributes.forEach(attribute -> entityAttributes.put(attribute.getName(), DataTypeDOFactory.buildInstance(attribute.getType())));
        return entityAttributes;
    }


}
