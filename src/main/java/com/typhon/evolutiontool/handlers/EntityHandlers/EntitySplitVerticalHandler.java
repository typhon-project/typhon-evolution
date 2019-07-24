package com.typhon.evolutiontool.handlers.EntityHandlers;

import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.exceptions.EvolutionOperationNotSupported;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.handlers.BaseHandler;
import com.typhon.evolutiontool.utils.RelationDOFactory;
import com.typhon.evolutiontool.utils.WorkingSetFactory;
import typhonml.Model;

import java.util.Arrays;

public class EntitySplitVerticalHandler extends BaseHandler {

    @Override
    public Model handle(SMO smo, Model model) throws InputParameterException, EvolutionOperationNotSupported {

        if(smo.getEvolutionOperator() == EvolutionOperator.SPLITVERTICAL){
            return splitVertical(smo, model);
        }
        else{
            return delegateToNext(smo, model);
        }
    }


    /**
     * Partially migrates the instances of sourceEntity to a new entity targetEntity. Only the values
     * of attributes [attributesNames] are migrated. The link between the instances of entity1 and entity2 is
     * kept via a new one-to-one relationship relName.
     * @param smo
     * @return
     * @throws InputParameterException
     */
    private Model splitVertical(SMO smo, Model model) throws InputParameterException {
        String databasetype, databasename, sourceEntityId;
        RelationDO relation;
        EntityDO sourceEntity, firstNewEntity, secondNewEntity;
        WorkingSet dataSource, dataTarget;
        Model targetModel;

        dataTarget = WorkingSetFactory.createEmptyWorkingSet();


        if (containParameters(smo, Arrays.asList(
                ParametersKeyString.ENTITY,
                ParametersKeyString.FIRSTNEWENTITY,
                ParametersKeyString.SECONDNEWENTITY,
                ParametersKeyString.DATABASENAME,
                ParametersKeyString.DATABASETYPE))) {
            sourceEntity = smo.getEntityDOFromInputParameter(ParametersKeyString.ENTITY);
            firstNewEntity = smo.getEntityDOFromInputParameter(ParametersKeyString.FIRSTNEWENTITY);
            secondNewEntity = smo.getEntityDOFromInputParameter(ParametersKeyString.SECONDNEWENTITY);
            databasetype = smo.getInputParameter().get(ParametersKeyString.DATABASETYPE).toString();
            databasename = smo.getInputParameter().get(ParametersKeyString.DATABASENAME).toString();

            //TyphonDL
            if (!typhonDLInterface.isDatabaseRunning(databasetype, databasename)) {
                typhonDLInterface.createDatabase(databasetype, databasename);
            }

            //TyphonML
            targetModel = typhonMLInterface.createEntityType(model, firstNewEntity);
            targetModel = typhonMLInterface.createEntityType(targetModel, secondNewEntity);
            relation = RelationDOFactory.createRelationDO("splitRelation", firstNewEntity, secondNewEntity, null, false, CardinalityDO.ONE);
            targetModel = typhonMLInterface.createRelationship(relation,targetModel);
            targetModel = typhonMLInterface.deleteEntityType(sourceEntity.getName(), targetModel);

            //TyphonQL
            typhonQLInterface.createEntityType(firstNewEntity, targetModel);
            typhonQLInterface.createEntityType(secondNewEntity, targetModel);
            typhonQLInterface.createRelationshipType(relation, targetModel);
            //TODO Data Manipulation

            return targetModel;
        }

        return null;
    }

}
