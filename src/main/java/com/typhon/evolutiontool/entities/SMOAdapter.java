package com.typhon.evolutiontool.entities;

import com.typhon.evolutiontool.utils.EntityDOFactory;
import com.typhon.evolutiontool.utils.RelationDOFactory;
import typhonml.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class converts ChangeOperatorof TyphonML package object to an intern SMO object used by the EvolutionService class.
 * Implementation is currently naive and non optimal as no attributes allow the detection of the specific operators. Downcasting is the current implementation.
 *
 */
public class SMOAdapter implements SMO {

    private ChangeOperator changeOperator;
    private Map<String, Object> inputParameter;
    private TyphonMLObject typhonMLObject;
    private EvolutionOperator evolutionOperator;

    public SMOAdapter(ChangeOperator changeOperator) {
        this.changeOperator = changeOperator;
        initializeEvolutionOperatorAttribute();
        initializeTyphonObjectAttribute();
        initializeInputParameterAttribute();
    }

    private void initializeTyphonObjectAttribute() {
        if(changeOperator instanceof RemoveEntity || changeOperator instanceof RenameEntity || changeOperator instanceof AddEntity)
            typhonMLObject = TyphonMLObject.ENTITY;
        if(changeOperator instanceof AddRelation)
            typhonMLObject = TyphonMLObject.RELATION;

    }

    private void initializeEvolutionOperatorAttribute() {
        if(changeOperator instanceof RemoveEntity)
            evolutionOperator=EvolutionOperator.REMOVE;
        if(changeOperator instanceof RenameEntity)
            evolutionOperator = EvolutionOperator.RENAME;
        if(changeOperator instanceof AddEntity || changeOperator instanceof AddRelation)
            evolutionOperator = EvolutionOperator.ADD;
        if(changeOperator instanceof MigrateEntity)
            evolutionOperator = EvolutionOperator.MIGRATE;
    }

    private void initializeInputParameterAttribute() {
        inputParameter = new HashMap<>();
        if (typhonMLObject == TyphonMLObject.ENTITY && evolutionOperator == EvolutionOperator.ADD) {
            inputParameter.put(ParametersKeyString.ENTITY, EntityDOFactory.createEntityDOFromEntityML((Entity) changeOperator));
            //TODO Add other parameters
        }
        if(typhonMLObject == TyphonMLObject.ENTITY && evolutionOperator==EvolutionOperator.REMOVE)
            inputParameter.put(ParametersKeyString.ENTITYNAME, ((RemoveEntity) changeOperator).getEntityToRemove().getName());
        if (typhonMLObject == TyphonMLObject.ENTITY && evolutionOperator == EvolutionOperator.RENAME) {
            inputParameter.put(ParametersKeyString.OLDENTITYNAME, ((RenameEntity) changeOperator).getEntityToRename().getName());
            inputParameter.put(ParametersKeyString.NEWENTITYNAME, ((RenameEntity) changeOperator).getNewEntityName());
        }
        if (typhonMLObject == TyphonMLObject.RELATION && evolutionOperator==EvolutionOperator.ADD) {
            inputParameter.put(ParametersKeyString.RELATION, (Relation) changeOperator);
        }
        if (typhonMLObject == TyphonMLObject.ENTITY && evolutionOperator == EvolutionOperator.MIGRATE) {
            inputParameter.put(ParametersKeyString.ENTITYNAME, ((MigrateEntity) changeOperator).getEntity().getName());
            inputParameter.put(ParametersKeyString.DATABASENAME, ((MigrateEntity) changeOperator).getNewDatabase().getName());
            //TODO by TyphonML
//            inputParameter.put(ParametersKeyString.DATABASETYPE,((MigrateEntity)changeOperator).getNewDatabase().getType());
//            inputParameter.put(ParametersKeyString.TARGETLOGICALNAME, ((MigrateEntity) changeOperator).getTargetLogicalName());
        }
    }

    @Override
    public TyphonMLObject getTyphonObject() {
        return typhonMLObject;
    }


    @Override
    public EvolutionOperator getEvolutionOperator() {
        return evolutionOperator;
    }


    @Override
    public Map<String, Object> getInputParameter() {
        return inputParameter;
    }


    @Override
    public boolean inputParametersContainsExpected(List<String> expectedInputParams) {
        for (String expected :
                expectedInputParams) {
            if (!this.inputParameter.containsKey(expected))
                return false;
        }
        return true;
    }


    @Override
    public EntityDO getEntityDOFromInputParameter(String parameterkey) {
        //Because AddEntity Operator exends Enity in TyphonML meta model.
        if(changeOperator instanceof AddEntity)
            return EntityDOFactory.createEntityDOFromEntityML((AddEntity)changeOperator);
        return null;
    }

    @Override
    public RelationDO getRelationDOFromInputParameter(String parameterkey) {
        if(this.getTyphonObject()==TyphonMLObject.RELATION && this.getEvolutionOperator()==EvolutionOperator.ADD)
            return RelationDOFactory.createRelationDOFromRelationML((AddRelation) changeOperator);
        return null;
    }

    @Override
    public <T> T getPOJOFromInputParameter(String key, Class<T> pojoclass) {
        return null;
    }
}
