package com.typhon.evolutiontool.entities;

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
    private Map<ChangeOperatorParameter, Object> inputParameter;
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
                || changeOperator instanceof MigrateEntity
                || changeOperator instanceof SplitEntityHorizontal
                || changeOperator instanceof SplitEntityVertical
                || changeOperator instanceof MergeEntity) {
            typhonMLObject = TyphonMLObject.ENTITY;
        }
        if (changeOperator instanceof AddRelation
                || changeOperator instanceof RemoveRelation
                || changeOperator instanceof EnableRelationContainment
                || changeOperator instanceof DisableRelationContainment
                || changeOperator instanceof ChangeRelationContainement
                || changeOperator instanceof EnableBidirectionalRelation
                || changeOperator instanceof DisableBidirectionalRelation
                || changeOperator instanceof RenameRelation
                || changeOperator instanceof ChangeRelationCardinality) {
            typhonMLObject = TyphonMLObject.RELATION;
        }
        if (changeOperator instanceof AddAttribute
                || changeOperator instanceof RemoveAttribute
                || changeOperator instanceof RenameAttribute
                || changeOperator instanceof ChangeAttributeType) {
            typhonMLObject = TyphonMLObject.ATTRIBUTE;
        }
    }

    private void initializeEvolutionOperatorAttribute() {
        if (changeOperator instanceof RemoveEntity || changeOperator instanceof RemoveRelation || changeOperator instanceof RemoveAttribute)
            evolutionOperator = EvolutionOperator.REMOVE;
        if (changeOperator instanceof RenameEntity || changeOperator instanceof RenameRelation || changeOperator instanceof RenameAttribute)
            evolutionOperator = EvolutionOperator.RENAME;
        if (changeOperator instanceof AddEntity || changeOperator instanceof AddRelation || changeOperator instanceof AddAttribute)
            evolutionOperator = EvolutionOperator.ADD;
        if (changeOperator instanceof MigrateEntity)
            evolutionOperator = EvolutionOperator.MIGRATE;
        if (changeOperator instanceof SplitEntityVertical)
            evolutionOperator = EvolutionOperator.SPLITVERTICAL;
        if (changeOperator instanceof SplitEntityHorizontal)
            evolutionOperator = EvolutionOperator.SPLITHORIZONTAL;
        if (changeOperator instanceof MergeEntity)
            evolutionOperator = EvolutionOperator.MERGE;
        if (changeOperator instanceof EnableRelationContainment)
            evolutionOperator = EvolutionOperator.ENABLECONTAINMENT;
        if (changeOperator instanceof DisableRelationContainment)
            evolutionOperator = EvolutionOperator.DISABLECONTAINMENT;
        if (changeOperator instanceof ChangeRelationContainement)
            evolutionOperator = EvolutionOperator.CHANGECONTAINMENT;
        if (changeOperator instanceof EnableBidirectionalRelation)
            evolutionOperator = EvolutionOperator.ENABLEOPPOSITE;
        if (changeOperator instanceof DisableBidirectionalRelation)
            evolutionOperator = EvolutionOperator.DISABLEOPPOSITE;
        if (changeOperator instanceof ChangeRelationCardinality)
            evolutionOperator = EvolutionOperator.CHANGECARDINALITY;
        if (changeOperator instanceof ChangeAttributeType)
            evolutionOperator = EvolutionOperator.CHANGETYPE;
    }

    private void initializeInputParameterAttribute() {
        inputParameter = new HashMap<>();

        //ENTITY
        if (typhonMLObject == TyphonMLObject.ENTITY) {
            if (evolutionOperator == EvolutionOperator.ADD) {
                inputParameter.put(ChangeOperatorParameter.ENTITY, changeOperator);
            }
            if (evolutionOperator == EvolutionOperator.REMOVE) {
                inputParameter.put(ChangeOperatorParameter.ENTITY_NAME, ((RemoveEntity) changeOperator).getEntityToRemove().getName());
            }
            if (evolutionOperator == EvolutionOperator.RENAME) {
                inputParameter.put(ChangeOperatorParameter.ENTITY_NAME, ((RenameEntity) changeOperator).getEntityToRename().getName());
                inputParameter.put(ChangeOperatorParameter.NEW_ENTITY_NAME, ((RenameEntity) changeOperator).getNewEntityName());
            }
            if (evolutionOperator == EvolutionOperator.MIGRATE) {
                inputParameter.put(ChangeOperatorParameter.ENTITY, ((MigrateEntity) changeOperator).getEntity());
                inputParameter.put(ChangeOperatorParameter.DATABASE, ((MigrateEntity) changeOperator).getNewDatabase());
            }
            if (evolutionOperator == EvolutionOperator.SPLITVERTICAL) {
                inputParameter.put(ChangeOperatorParameter.ENTITY, ((SplitEntityVertical) changeOperator).getEntity1());
                inputParameter.put(ChangeOperatorParameter.NEW_ENTITY_NAME, ((SplitEntityVertical) changeOperator).getEntity2name());
                //The list of attributes and relations to move from entity 1 to the new entity
                inputParameter.put(ChangeOperatorParameter.NEW_ENTITY_ATTRIBUTES, ((SplitEntityVertical) changeOperator).getAttributeList());
                inputParameter.put(ChangeOperatorParameter.NEW_ENTITY_RELATIONS, ((SplitEntityVertical) changeOperator).getRelationList());
            }
            if (evolutionOperator == EvolutionOperator.SPLITHORIZONTAL) {
                inputParameter.put(ChangeOperatorParameter.ENTITY, ((SplitEntityHorizontal) changeOperator).getEntity1());
                inputParameter.put(ChangeOperatorParameter.NEW_ENTITY_NAME, ((SplitEntityHorizontal) changeOperator).getEntity2name());
                inputParameter.put(ChangeOperatorParameter.ENTITY_SPLIT_ATTRIBUTE, ((SplitEntityHorizontal) changeOperator).getAttribute());
                inputParameter.put(ChangeOperatorParameter.ENTITY_SPLIT_EXPRESSION, ((SplitEntityHorizontal) changeOperator).getExpression());
            }
            if (evolutionOperator == EvolutionOperator.MERGE) {
                inputParameter.put(ChangeOperatorParameter.FIRST_ENTITY_TO_MERGE, ((MergeEntity) changeOperator).getFirstEntityToMerge());
                inputParameter.put(ChangeOperatorParameter.SECOND_ENTITY_TO_MERGE, ((MergeEntity) changeOperator).getSecondEntityToMerge());
                inputParameter.put(ChangeOperatorParameter.NEW_ENTITY_NAME, ((MergeEntity) changeOperator).getNewEntityName());
            }
        }

        //RELATION
        if (typhonMLObject == TyphonMLObject.RELATION) {
            if (evolutionOperator == EvolutionOperator.ADD) {
                inputParameter.put(ChangeOperatorParameter.RELATION, changeOperator);
            }
            if (evolutionOperator == EvolutionOperator.REMOVE) {
                inputParameter.put(ChangeOperatorParameter.RELATION, ((RemoveRelation) changeOperator).getRelationToRemove());
            }
            if (evolutionOperator == EvolutionOperator.ENABLECONTAINMENT) {
                inputParameter.put(ChangeOperatorParameter.RELATION, ((EnableRelationContainment) changeOperator).getRelation());
            }
            if (evolutionOperator == EvolutionOperator.DISABLECONTAINMENT) {
                inputParameter.put(ChangeOperatorParameter.RELATION, ((DisableRelationContainment) changeOperator).getRelation());
            }
            if (evolutionOperator == EvolutionOperator.CHANGECONTAINMENT) {
                inputParameter.put(ChangeOperatorParameter.RELATION, ((ChangeRelationContainement) changeOperator).getRelation());
                inputParameter.put(ChangeOperatorParameter.NEW_CONTAINMENT, ((ChangeRelationContainement) changeOperator).getNewContainment());
            }
            if (evolutionOperator == EvolutionOperator.ENABLEOPPOSITE) {
                inputParameter.put(ChangeOperatorParameter.RELATION, ((EnableBidirectionalRelation) changeOperator).getRelation());
            }
            if (evolutionOperator == EvolutionOperator.DISABLEOPPOSITE) {
                inputParameter.put(ChangeOperatorParameter.RELATION, ((DisableBidirectionalRelation) changeOperator).getRelation());
            }
            if (evolutionOperator == EvolutionOperator.RENAME) {
                inputParameter.put(ChangeOperatorParameter.RELATION, ((RenameRelation) changeOperator).getRelationToRename());
                inputParameter.put(ChangeOperatorParameter.RELATION_NAME, ((RenameRelation) changeOperator).getNewRelationName());
            }
            if (evolutionOperator == EvolutionOperator.CHANGECARDINALITY) {
                inputParameter.put(ChangeOperatorParameter.RELATION, ((ChangeRelationCardinality) changeOperator).getRelation());
                inputParameter.put(ChangeOperatorParameter.CARDINALITY, ((ChangeRelationCardinality) changeOperator).getNewCardinality().getValue());
            }
        }
        //ATTRIBUTE
        if (typhonMLObject == TyphonMLObject.ATTRIBUTE) {
            if (evolutionOperator == EvolutionOperator.ADD) {
                inputParameter.put(ChangeOperatorParameter.ATTRIBUTE, changeOperator);
            }
            if (evolutionOperator == EvolutionOperator.REMOVE) {
                inputParameter.put(ChangeOperatorParameter.ATTRIBUTE, ((RemoveAttribute) changeOperator).getAttributeToRemove());
            }
            if (evolutionOperator == EvolutionOperator.RENAME) {
                inputParameter.put(ChangeOperatorParameter.ATTRIBUTE, ((RenameAttribute) changeOperator).getAttributeToRename());
                inputParameter.put(ChangeOperatorParameter.NEW_ATTRIBUTE_NAME, ((RenameAttribute) changeOperator).getNewName());
            }
            if (evolutionOperator == EvolutionOperator.CHANGETYPE) {
                inputParameter.put(ChangeOperatorParameter.ATTRIBUTE, ((ChangeAttributeType) changeOperator).getAttributeToChange());
                inputParameter.put(ChangeOperatorParameter.ATTRIBUTE_TYPE, ((ChangeAttributeType) changeOperator).getNewType());
            }
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
    public Map<ChangeOperatorParameter, Object> getInputParameter() {
        return inputParameter;
    }


    @Override
    public boolean inputParametersContainsExpected(List<ChangeOperatorParameter> expectedInputParams) {
        for (ChangeOperatorParameter expected : expectedInputParams) {
            if (!this.inputParameter.containsKey(expected))
                return false;
        }
        return true;
    }
}
