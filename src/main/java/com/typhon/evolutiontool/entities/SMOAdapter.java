package com.typhon.evolutiontool.entities;

import com.typhon.evolutiontool.utils.AttributeDOFactory;
import com.typhon.evolutiontool.utils.EntityDOFactory;
import com.typhon.evolutiontool.utils.RelationDOFactory;
import typhonml.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class converts ChangeOperator of TyphonML package object to an intern SMO object used by the EvolutionService class.
 * Implementation is currently naive and non optimal as no attributes allow the detection of the specific operators. Downcasting is the current implementation.
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
        if (changeOperator instanceof RemoveEntity
                || changeOperator instanceof RenameEntity
                || changeOperator instanceof AddEntity
                || changeOperator instanceof MigrateEntity)
            typhonMLObject = TyphonMLObject.ENTITY;
        if (changeOperator instanceof AddRelation
                || changeOperator instanceof RemoveRelation
                || changeOperator instanceof EnableBidirectionalRelation
                || changeOperator instanceof DisableBidirectionalRelation
                || changeOperator instanceof RenameRelation
                || changeOperator instanceof ChangeRelationCardinality)
            typhonMLObject = TyphonMLObject.RELATION;
    }

    private void initializeEvolutionOperatorAttribute() {
        if (changeOperator instanceof RemoveEntity || changeOperator instanceof RemoveRelation)
            evolutionOperator = EvolutionOperator.REMOVE;
        if (changeOperator instanceof RenameEntity || changeOperator instanceof RenameRelation)
            evolutionOperator = EvolutionOperator.RENAME;
        if (changeOperator instanceof AddEntity || changeOperator instanceof AddRelation)
            evolutionOperator = EvolutionOperator.ADD;
        if (changeOperator instanceof MigrateEntity)
            evolutionOperator = EvolutionOperator.MIGRATE;
        if (changeOperator instanceof EnableBidirectionalRelation)
            evolutionOperator = EvolutionOperator.ENABLEOPPOSITE;
        if (changeOperator instanceof DisableBidirectionalRelation)
            evolutionOperator = EvolutionOperator.DISABLEOPPOSITE;
        if (changeOperator instanceof ChangeRelationCardinality)
            evolutionOperator = EvolutionOperator.CHANGECARDINALITY;
    }

    private void initializeInputParameterAttribute() {
        inputParameter = new HashMap<>();
        if (typhonMLObject == TyphonMLObject.ENTITY && evolutionOperator == EvolutionOperator.ADD) {
            inputParameter.put(ParametersKeyString.ENTITY, EntityDOFactory.buildInstance((Entity) changeOperator));
            //TODO Add other parameters
        }
        if (typhonMLObject == TyphonMLObject.ENTITY && evolutionOperator == EvolutionOperator.REMOVE)
            inputParameter.put(ParametersKeyString.ENTITYNAME, ((RemoveEntity) changeOperator).getEntityToRemove().getName());
        if (typhonMLObject == TyphonMLObject.ENTITY && evolutionOperator == EvolutionOperator.RENAME) {
            inputParameter.put(ParametersKeyString.ENTITYNAME, ((RenameEntity) changeOperator).getEntityToRename().getName());
            inputParameter.put(ParametersKeyString.NEWENTITYNAME, ((RenameEntity) changeOperator).getNewEntityName());
        }
        if (typhonMLObject == TyphonMLObject.RELATION && evolutionOperator == EvolutionOperator.ADD) {
            //TODO by TyphonML : either add sourceEntity in Relation, or sourceentity in the operator.
            inputParameter.put(ParametersKeyString.RELATION, changeOperator);
        }
        if (typhonMLObject == TyphonMLObject.ENTITY && evolutionOperator == EvolutionOperator.MIGRATE) {
            inputParameter.put(ParametersKeyString.ENTITYNAME, ((MigrateEntity) changeOperator).getEntity().getName());
            inputParameter.put(ParametersKeyString.DATABASENAME, ((MigrateEntity) changeOperator).getNewDatabase().getName());
            //TODO by TyphonML
//            inputParameter.put(ParametersKeyString.DATABASETYPE,((MigrateEntity)changeOperator).getNewDatabase().getType());
//            inputParameter.put(ParametersKeyString.TARGETLOGICALNAME, ((MigrateEntity) changeOperator).getTargetLogicalName());
        }
        if (typhonMLObject == TyphonMLObject.RELATION && evolutionOperator == EvolutionOperator.REMOVE) {
            //TODO by TyphonML
//            inputParameter.put(ParametersKeyString.ENTITYNAME),((RemoveRelation) changeOperator).getRelationToRemove().getSourceEntity().getName();
            inputParameter.put(ParametersKeyString.RELATIONNAME, ((RemoveRelation) changeOperator).getRelationToRemove().getName());
        }
        if (typhonMLObject == TyphonMLObject.RELATION && evolutionOperator == EvolutionOperator.ENABLEOPPOSITE) {
            inputParameter.put(ParametersKeyString.RELATION, ((EnableBidirectionalRelation) changeOperator).getRelation());
            //TODO by TyphonML: missing relationname parameter
//            inputParameter.put(ParametersKeyString.RELATIONNAME), ((EnableBidirectionalRelation) changeOperator).getRelationName());
        }
        if (typhonMLObject == TyphonMLObject.RELATION && evolutionOperator == EvolutionOperator.DISABLEOPPOSITE) {
            inputParameter.put(ParametersKeyString.RELATION, ((DisableBidirectionalRelation) changeOperator).getRelation());
        }
        if (typhonMLObject == TyphonMLObject.RELATION && evolutionOperator == EvolutionOperator.RENAME) {
            inputParameter.put(ParametersKeyString.RELATION, ((RenameRelation) changeOperator).getRelationToRename());
            inputParameter.put(ParametersKeyString.RELATIONNAME, ((RenameRelation) changeOperator).getNewRelationName());
        }
        if (typhonMLObject == TyphonMLObject.RELATION && evolutionOperator == EvolutionOperator.CHANGECARDINALITY) {
            inputParameter.put(ParametersKeyString.RELATION, ((ChangeRelationCardinality) changeOperator).getRelation());
            inputParameter.put(ParametersKeyString.CARDINALITY, ((ChangeRelationCardinality) changeOperator).getNewCardinality());
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
        //Because AddEntity Operator extends Entity in TyphonML meta model.
        if (changeOperator instanceof AddEntity)
            return EntityDOFactory.buildInstance((AddEntity) changeOperator);
        return null;
    }

    @Override
    public RelationDO getRelationDOFromInputParameter(String parameterkey) {
        if (this.getTyphonObject() == TyphonMLObject.RELATION) {
            if (this.getEvolutionOperator() == EvolutionOperator.ADD) {
                return RelationDOFactory.buildInstance((AddRelation) changeOperator);
            }
            if (this.getEvolutionOperator() == EvolutionOperator.ENABLEOPPOSITE) {
                return RelationDOFactory.buildInstance(((EnableBidirectionalRelation) changeOperator).getRelation());
            }
            if (this.getEvolutionOperator() == EvolutionOperator.DISABLEOPPOSITE) {
                return RelationDOFactory.buildInstance(((DisableBidirectionalRelation) changeOperator).getRelation());
            }
        }
        return null;
    }

    @Override
    public AttributeDO getAttributeDOFromInputParameter(String parameterkey) {
        if (this.getTyphonObject() == TyphonMLObject.ATTRIBUTE && this.getEvolutionOperator() == EvolutionOperator.ADD) {
            return AttributeDOFactory.buildInstance((AddAttribute) changeOperator);
        }
        return null;
    }

    @Override
    public <T> T getPOJOFromInputParameter(String key, Class<T> pojoclass) {
        return null;
    }
}
