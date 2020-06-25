package com.typhon.evolutiontool.handlers.entity;

import com.typhon.evolutiontool.datatypes.DataTypeDO;
import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import com.typhon.evolutiontool.utils.EntityDOFactory;
import com.typhon.evolutiontool.utils.RelationDOFactory;
import typhonml.*;

import java.util.Arrays;
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
            Database sourceDatabase = typhonMLInterface.getEntityDatabase(firstEntityDO.getName(), targetModel);
            DatabaseType sourceDatabaseType = getDatabaseType(sourceDatabase);
            targetModel = typhonMLInterface.createNewEntityMappingInDatabase(sourceDatabaseType, sourceDatabase.getName(), secondEntityDO.getName(), secondEntityDO.getName(), targetModel);
            targetModel = typhonMLInterface.removeCurrentChangeOperator(targetModel);

            //TyphonQL
            //Select the source entity data for the attribute and the value
            WorkingSet entityData = typhonQLInterface.selectEntityData(firstEntityDO.getName(), firstEntityDO.getAttributes().keySet(), null, null, null);
            //Manipulate the source entity data (keep only the new entity attributes)
            typhonQLInterface.removeUselessAttributesInSourceEntityData(entityData, firstEntityDO.getName(), entityAttributes.keySet());
            //Manipulate the source entity data (modify the entity name, to the new entity name)
            typhonQLInterface.updateEntityNameInSourceEntityData(entityData, firstEntityDO.getName(), secondEntityDO.getName());
            //Remove the attributes from the source entity, which have been copied to the second entity
            for (String attributeName : secondEntityDO.getAttributes().keySet()) {
                typhonQLInterface.removeAttribute(firstEntityDO.getName(), attributeName);
            }
            //Remove the relations from the source entity, which have been copied to the second entity
            for (RelationDO secondEntityRelation : secondEntityDO.getRelations()) {
                typhonQLInterface.deleteRelationshipInEntity(secondEntityRelation.getName(), firstEntityDO.getName());
            }
            //Typhon ML: delete splitted entity attributes and relations
            for (String attributeName : entityAttributes.keySet()) {
                targetModel = typhonMLInterface.removeAttribute(attributeName, firstEntityDO.getName(), targetModel);
            }
            for (RelationDO relation : entityRelations) {
                targetModel = typhonMLInterface.deleteRelationshipInEntity(relation.getName(), firstEntityDO.getName(), targetModel);
            }
            //Upload the new XMI to the polystore
            typhonQLInterface.uploadSchema(targetModel);
            //Create the new entity, with its attributes and relations
            typhonQLInterface.createEntity(secondEntityDO, sourceDatabase.getName());
            //Create a new relation between the source entity and the new entity
            //TODO check the relation creation works after QL update (actually not working, because relation creation does not create a join table, nor a reference attribute)
            typhonQLInterface.createRelationshipType(relationDO);
            //Insert the adapted data in the new entity
            typhonQLInterface.insertEntityData(secondEntityDO.getName(), entityData, secondEntityDO);

            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ChangeOperatorParameter.ENTITY + ", " + ChangeOperatorParameter.NEW_ENTITY_NAME + ", " + ChangeOperatorParameter.NEW_ENTITY_ATTRIBUTES + ", " + ChangeOperatorParameter.NEW_ENTITY_RELATIONS + "]");
        }
    }


}
