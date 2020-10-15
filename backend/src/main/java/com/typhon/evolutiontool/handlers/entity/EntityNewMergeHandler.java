package com.typhon.evolutiontool.handlers.entity;

import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.services.typhonDL.TyphonDLInterface;
import com.typhon.evolutiontool.services.typhonML.TyphonMLInterface;
import com.typhon.evolutiontool.services.typhonQL.TyphonQLInterface;
import com.typhon.evolutiontool.utils.EntityDOFactory;
import typhonml.Entity;
import typhonml.Model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EntityNewMergeHandler extends BaseHandler {

    public EntityNewMergeHandler(TyphonDLInterface tdl, TyphonMLInterface tml, TyphonQLInterface tql) {
        super(tdl, tml, tql);
    }

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException {
        if (containParameters(smo, Arrays.asList(ChangeOperatorParameter.FIRST_ENTITY_TO_MERGE, ChangeOperatorParameter.SECOND_ENTITY_TO_MERGE, ChangeOperatorParameter.RELATION_NAME))) {
            /*Merge if:
                - 1-1 and 0-* (only in DocumentDatabase) relations
                - merged entity has no relation
            */
            EntityDO firstNewEntity = EntityDOFactory.buildInstance((Entity) smo.getInputParameter().get(ChangeOperatorParameter.FIRST_ENTITY_TO_MERGE), false);
            EntityDO secondNewEntity = EntityDOFactory.buildInstance((Entity) smo.getInputParameter().get(ChangeOperatorParameter.SECOND_ENTITY_TO_MERGE), false);
            RelationDO mergeRelation = null;
            Boolean firstOrSecondEntityRelation = null;
            //The name of the relation through which the merge is done
            String mergeRelationName = String.valueOf(smo.getInputParameter().get(ChangeOperatorParameter.RELATION_NAME));
            //Find the relation concerned by the merge
            if (mergeRelationName != null && mergeRelationName.contains(".")) {
                String entityName = mergeRelationName.substring(0, mergeRelationName.indexOf("."));
                String relationName = mergeRelationName.substring(mergeRelationName.indexOf(".") + 1);
                if (firstNewEntity.getName().equals(entityName)) {
                    List<RelationDO> relations = firstNewEntity.getRelations().stream().filter(relation -> relation.getName().equals(relationName)).collect(Collectors.toList());
                    mergeRelation = relations.size() == 1 ? relations.get(0) : null;
                    firstOrSecondEntityRelation = true;
                } else if (secondNewEntity.getName().equals(entityName)) {
                    List<RelationDO> relations = secondNewEntity.getRelations().stream().filter(relation -> relation.getName().equals(relationName)).collect(Collectors.toList());
                    mergeRelation = relations.size() == 1 ? relations.get(0) : null;
                    firstOrSecondEntityRelation = false;
                }
            }

            if (mergeRelation != null) {
                //TyphonQL
                //Select the source entity data
                WorkingSet firstWs = typhonQLInterface.selectEntityData(firstNewEntity.getName(), firstNewEntity.getAttributes().keySet(), firstOrSecondEntityRelation ? Collections.singletonList(mergeRelation.getName()) : null, null, null);
                WorkingSet secondWs = typhonQLInterface.selectEntityData(secondNewEntity.getName(), secondNewEntity.getAttributes().keySet(), !firstOrSecondEntityRelation ? Collections.singletonList(mergeRelation.getName()) : null, null, null);
                //Manipulate the source entities data (modify the entities names, to the new entity name)
                typhonQLInterface.updateEntityNameInSourceEntityData(secondWs, secondNewEntity.getName(), firstNewEntity.getName());

                //Merge the second entity attributes and relations into the first entity
                Model targetModel = typhonMLInterface.mergeEntities(firstNewEntity.getName(), secondNewEntity.getName(), model);
                //Delete the second entity mappings from the database
                String secondEntityNameInDatabase = typhonMLInterface.getEntityNameInDatabase(secondNewEntity.getName(), targetModel);
                targetModel = typhonMLInterface.deleteEntityMappings(secondNewEntity.getName(), secondEntityNameInDatabase, targetModel);
                //Remove the second entity from the model
                targetModel = typhonMLInterface.deleteEntityType(secondNewEntity.getName(), targetModel);
                //Remove the change operator from the model
                targetModel = typhonMLInterface.removeCurrentChangeOperator(targetModel);

                //Remove the relation used to merge the entities (and the opposite if it exists)
                typhonQLInterface.deleteRelationshipInEntity(mergeRelation.getName(), mergeRelation.getSourceEntity().getName());
                if (mergeRelation.getOpposite() != null && mergeRelation.getOpposite().getSourceEntity() != null) {
                    typhonQLInterface.deleteRelationshipInEntity(mergeRelation.getOpposite().getName(), mergeRelation.getOpposite().getSourceEntity().getName());
                }
                //Remove the second entity from the database
                typhonQLInterface.dropEntity(secondNewEntity.getName());

                //Upload the new XMI to the polystore
                typhonQLInterface.uploadSchema(targetModel);

                //Merge the second entity attributes and relations into the first entity
                if (secondNewEntity.getAttributes() != null) {
                    for (String attributeName : secondNewEntity.getAttributes().keySet()) {
                        typhonQLInterface.addAttribute(attributeName, firstNewEntity.getName(), secondNewEntity.getAttributes().get(attributeName));
                    }
                }
                //For the moment, the second entity cannot have outgoing relations
//                if (secondNewEntity.getRelations() != null) {
//                    for (RelationDO relationDO : secondNewEntity.getRelations()) {
//                        typhonQLInterface.createRelationshipType(relationDO);
//                    }
//                }
                //Insert the second entity data into the first entity
                typhonQLInterface.updateEntityData(firstNewEntity.getName(), firstWs, secondWs, secondNewEntity, mergeRelation.getName(), firstOrSecondEntityRelation);

                return targetModel;
            } else {
                throw new InputParameterException("Cannot find the relation: " + mergeRelationName + "; aborting the merge...");
            }
        } else {
            throw new InputParameterException("Missing parameters. Needed [" + ChangeOperatorParameter.FIRST_ENTITY_TO_MERGE + ", " + ChangeOperatorParameter.SECOND_ENTITY_TO_MERGE + ", " + ChangeOperatorParameter.RELATION_NAME + "]");
        }
    }

}
