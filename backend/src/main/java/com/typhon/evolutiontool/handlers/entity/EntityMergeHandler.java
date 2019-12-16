package com.typhon.evolutiontool.handlers.entity;

import com.typhon.evolutiontool.dummy.WorkingSetDummyImpl;
import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import com.typhon.evolutiontool.utils.EntityDOFactory;
import org.eclipse.emf.ecore.util.EcoreUtil;
import typhonml.Database;
import typhonml.Entity;
import typhonml.Model;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class EntityMergeHandler extends BaseHandler {

    public EntityMergeHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Arrays.asList(ChangeOperatorParameter.FIRST_ENTITY_TO_MERGE, ChangeOperatorParameter.SECOND_ENTITY_TO_MERGE, ChangeOperatorParameter.NEW_ENTITY_NAME))) {
            //TyphonML
            /*
                - Check cardinality between the two entities (one_to_many only)
                - Delete relation between the two
                - Check that second entity is not any relationship. If yes, cancel.
                - Copy attribute of secondentity
                - Rename entity.
             */
            EntityDO firstNewEntity = EntityDOFactory.buildInstance((Entity) smo.getInputParameter().get(ChangeOperatorParameter.FIRST_ENTITY_TO_MERGE), false);
            EntityDO secondNewEntity = EntityDOFactory.buildInstance((Entity) smo.getInputParameter().get(ChangeOperatorParameter.SECOND_ENTITY_TO_MERGE), false);
            String newEntityName = String.valueOf(smo.getInputParameter().get(ChangeOperatorParameter.NEW_ENTITY_NAME));

            //Remove relations between the two entities to merge
            removeRelationsBetweenEntities(firstNewEntity, secondNewEntity);
            //Build the merged entity, based on entities to merge
            EntityDO newEntityDO = buildMergedEntity(newEntityName, firstNewEntity, secondNewEntity);
            //Copy the source model
            Model targetModel = EcoreUtil.copy(model);
            //Retrieve source database information
            Database sourceDatabase = typhonMLInterface.getEntityDatabase(firstNewEntity.getName(), targetModel);
            DatabaseType sourceDatabaseType = getDatabaseType(sourceDatabase);
            //Delete mappings of the entities to merge from the databases
            targetModel = typhonMLInterface.deleteEntityMappings(firstNewEntity.getName(), typhonMLInterface.getEntityNameInDatabase(firstNewEntity.getName(), targetModel), targetModel);
            targetModel = typhonMLInterface.deleteEntityMappings(secondNewEntity.getName(), typhonMLInterface.getEntityNameInDatabase(secondNewEntity.getName(), targetModel), targetModel);
            //Delete entities to merge
            targetModel = typhonMLInterface.deleteEntityType(firstNewEntity.getName(), targetModel);
            targetModel = typhonMLInterface.deleteEntityType(secondNewEntity.getName(), targetModel);
            //Create the merged entity in the model
            targetModel = typhonMLInterface.createEntityType(targetModel, newEntityDO);
            //Create the merged entity mapping in the database
            targetModel = typhonMLInterface.createNewEntityMappingInDatabase(sourceDatabaseType, sourceDatabase.getName(), newEntityName, newEntityName, targetModel);

            //Upload the new XMI to the polystore
            typhonQLInterface.uploadSchema(targetModel);

            //TyphonQL
            typhonQLInterface.createEntity(newEntityName, sourceDatabase.getName(), targetModel);
            //Create the entity attributes
            if (newEntityDO.getAttributes() != null && !newEntityDO.getAttributes().isEmpty()) {
                for (String attributeName : newEntityDO.getAttributes().keySet()) {
                    typhonQLInterface.createEntityAttribute(newEntityDO.getName(), attributeName, newEntityDO.getAttributes().get(attributeName).getName(), model);
                }
            }
            //Create the entity relationships
            if (newEntityDO.getRelations() != null && !newEntityDO.getRelations().isEmpty()) {
                for (RelationDO relationDO : newEntityDO.getRelations()) {
                    typhonQLInterface.createEntityRelation(newEntityDO.getName(), relationDO.getName(), relationDO.isContainment(), relationDO.getTypeName(), relationDO.getCardinality(), model);
                }
            }
            //Select the source entity data
            WorkingSet firstWs = typhonQLInterface.selectEntityData(firstNewEntity.getName(), targetModel);
            WorkingSet secondWs = typhonQLInterface.selectEntityData(secondNewEntity.getName(), targetModel);
            WorkingSet mergedWs = mergeWs(firstWs, secondWs);
            //Insert the source entity data into the target entity
            typhonQLInterface.insertEntityData(newEntityName, newEntityDO.getAttributes().keySet(), mergedWs, targetModel);
            //Delete the 2 source entities
            typhonQLInterface.dropEntity(firstNewEntity.getName(), targetModel);
            typhonQLInterface.dropEntity(secondNewEntity.getName(), targetModel);

            return targetModel;
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ChangeOperatorParameter.FIRST_ENTITY_TO_MERGE + ", " + ChangeOperatorParameter.SECOND_ENTITY_TO_MERGE + ", " + ChangeOperatorParameter.NEW_ENTITY_NAME + "]");
        }
    }

    private void removeRelationsBetweenEntities(EntityDO firstNewEntity, EntityDO secondNewEntity) {
        if (firstNewEntity != null && secondNewEntity != null) {
            List<RelationDO> firstEntityRelations = firstNewEntity.getRelations();
            if (firstEntityRelations != null) {
                for (RelationDO firstRelationDO : firstEntityRelations) {
                    if (firstRelationDO.getTypeName().equals(secondNewEntity.getName())) {
                        firstEntityRelations.remove(firstRelationDO);
                        break;
                    }
                }
            }
            List<RelationDO> secondEntityRelations = secondNewEntity.getRelations();
            if (secondEntityRelations != null) {
                for (RelationDO secondRelationDO : secondEntityRelations) {
                    if (secondRelationDO.getTypeName().equals(firstNewEntity.getName())) {
                        secondEntityRelations.remove(secondRelationDO);
                        break;
                    }
                }
            }
        }
    }

    private EntityDO buildMergedEntity(String mergeEntityName, EntityDO firstNewEntity, EntityDO secondNewEntity) {
        EntityDO mergedEntity = new EntityDOImpl();
        mergedEntity.setName(mergeEntityName);
        mergedEntity.getAttributes().putAll(firstNewEntity.getAttributes());
        mergedEntity.getAttributes().putAll(secondNewEntity.getAttributes());
        mergedEntity.getRelations().clear();
        mergedEntity.getRelations().addAll(firstNewEntity.getRelations());
        mergedEntity.getRelations().addAll(secondNewEntity.getRelations());
        return mergedEntity;
    }

    private WorkingSet mergeWs(WorkingSet firstWs, WorkingSet secondWs) {
        WorkingSet ws = new WorkingSetDummyImpl();
        if (firstWs == null || firstWs.getRows().isEmpty()) {
            return secondWs;
        }
        if (secondWs == null || secondWs.getRows().isEmpty()) {
            return firstWs;
        }
        for (String entityName : firstWs.getRows().keySet()) {
            List<EntityInstance> entityInstances = firstWs.getRows().get(entityName);
            if (secondWs.getRows().containsKey(entityName)) {
                entityInstances.addAll(secondWs.getEntityInstanceRows(entityName));
            }
            ws.getRows().put(entityName, entityInstances);
        }
        return ws;
    }

}
