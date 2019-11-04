package com.typhon.evolutiontool.handlers.entity;

import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import com.typhon.evolutiontool.utils.EntityDOFactory;
import typhonml.Database;
import typhonml.Entity;
import typhonml.Model;

import java.util.Arrays;
import java.util.List;

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
            //Create the merged entity in the model
            Model targetModel = typhonMLInterface.createEntityType(model, newEntityDO);
            //Retrieve source database information
            Database sourceDatabase = typhonMLInterface.getEntityDatabase(firstNewEntity.getName(), targetModel);
            DatabaseType sourceDatabaseType = getDatabaseType(sourceDatabase);
            //Delete mappings of the entities to merge from the databases
            targetModel = typhonMLInterface.deleteEntityMappings(firstNewEntity.getName(), typhonMLInterface.getEntityNameInDatabase(firstNewEntity.getName(), model), targetModel);
            targetModel = typhonMLInterface.deleteEntityMappings(secondNewEntity.getName(), typhonMLInterface.getEntityNameInDatabase(secondNewEntity.getName(), model), targetModel);
            //Create the merged entity mapping in the database
            targetModel = typhonMLInterface.createNewEntityMappingInDatabase(sourceDatabaseType, sourceDatabase.getName(), newEntityName, newEntityName, targetModel);
            //Delete entities to merge
            targetModel = typhonMLInterface.deleteEntityType(firstNewEntity.getName(), targetModel);
            targetModel = typhonMLInterface.deleteEntityType(secondNewEntity.getName(), targetModel);

            //TyphonQL

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

}
