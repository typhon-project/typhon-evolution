package main.java.com.typhon.evolutiontool.entities;
import typhonml.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.com.typhon.evolutiontool.utils.AttributeDOFactory;
import main.java.com.typhon.evolutiontool.utils.EntityDOFactory;
import main.java.com.typhon.evolutiontool.utils.RelationDOFactory;


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
                || changeOperator instanceof MigrateEntity) {
            typhonMLObject = TyphonMLObject.ENTITY;
        }
        if (changeOperator instanceof AddRelation
                || changeOperator instanceof RemoveRelation
                || changeOperator instanceof EnableRelationContainment
                || changeOperator instanceof DisableRelationContainment
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
        if (changeOperator instanceof EnableRelationContainment)
            evolutionOperator = EvolutionOperator.ENABLECONTAINMENT;
        if (changeOperator instanceof DisableRelationContainment)
            evolutionOperator = EvolutionOperator.DISABLECONTAINMENT;
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
                inputParameter.put(ParametersKeyString.ENTITY, EntityDOFactory.buildInstance((Entity) changeOperator));
                //TODO Add other parameters
            }
            if (evolutionOperator == EvolutionOperator.REMOVE) {
                inputParameter.put(ParametersKeyString.ENTITYNAME, ((RemoveEntity) changeOperator).getEntityToRemove().getName());
            }
            if (evolutionOperator == EvolutionOperator.RENAME) {
                inputParameter.put(ParametersKeyString.ENTITYNAME, ((RenameEntity) changeOperator).getEntityToRename().getName());
                inputParameter.put(ParametersKeyString.NEWENTITYNAME, ((RenameEntity) changeOperator).getNewEntityName());
            }
            if (evolutionOperator == EvolutionOperator.MIGRATE) {
                inputParameter.put(ParametersKeyString.ENTITY, ((MigrateEntity) changeOperator).getEntity());
                inputParameter.put(ParametersKeyString.DATABASE, ((MigrateEntity) changeOperator).getNewDatabase());
            }
        }

        //RELATION
        if (typhonMLObject == TyphonMLObject.RELATION) {
            if (evolutionOperator == EvolutionOperator.ADD) {
                inputParameter.put(ParametersKeyString.RELATION, changeOperator);
                inputParameter.put(ParametersKeyString.ENTITY, ((AddRelation) changeOperator).getOwnerEntity());
            }
            if (evolutionOperator == EvolutionOperator.REMOVE) {
                inputParameter.put(ParametersKeyString.RELATION, ((RemoveRelation) changeOperator).getRelationToRemove());
            }
            if (evolutionOperator == EvolutionOperator.ENABLECONTAINMENT) {
                inputParameter.put(ParametersKeyString.RELATION, ((EnableRelationContainment) changeOperator).getRelation());
            }
            if (evolutionOperator == EvolutionOperator.DISABLECONTAINMENT) {
                inputParameter.put(ParametersKeyString.RELATION, ((DisableRelationContainment) changeOperator).getRelation());
            }
            if (evolutionOperator == EvolutionOperator.ENABLEOPPOSITE) {
                inputParameter.put(ParametersKeyString.RELATION, ((EnableBidirectionalRelation) changeOperator).getRelation());
                //TODO by TyphonML: missing relationname parameter
//            inputParameter.put(ParametersKeyString.RELATIONNAME), ((EnableBidirectionalRelation) changeOperator).getRelationName());
            }
            if (evolutionOperator == EvolutionOperator.DISABLEOPPOSITE) {
                inputParameter.put(ParametersKeyString.RELATION, ((DisableBidirectionalRelation) changeOperator).getRelation());
            }
            if (evolutionOperator == EvolutionOperator.RENAME) {
                inputParameter.put(ParametersKeyString.RELATION, ((RenameRelation) changeOperator).getRelationToRename());
                inputParameter.put(ParametersKeyString.RELATIONNAME, ((RenameRelation) changeOperator).getNewRelationName());
            }
            if (evolutionOperator == EvolutionOperator.CHANGECARDINALITY) {
                inputParameter.put(ParametersKeyString.RELATION, ((ChangeRelationCardinality) changeOperator).getRelation());
                inputParameter.put(ParametersKeyString.CARDINALITY, ((ChangeRelationCardinality) changeOperator).getNewCardinality());
            }
        }
        //ATTRIBUTE
        if (typhonMLObject == TyphonMLObject.ATTRIBUTE) {
            if (evolutionOperator == EvolutionOperator.ADD) {
                inputParameter.put(ParametersKeyString.ATTRIBUTENAME, ((AddAttribute) changeOperator).getName());
                inputParameter.put(ParametersKeyString.ATTRIBUTEIMPORTEDNAMESPACE, ((AddAttribute) changeOperator).getImportedNamespace());
                inputParameter.put(ParametersKeyString.ATTRIBUTETYPE, ((AddAttribute) changeOperator).getType());
                inputParameter.put(ParametersKeyString.ENTITY, ((AddAttribute) changeOperator).getOwnerEntity());
            }
            if (evolutionOperator == EvolutionOperator.REMOVE) {
                inputParameter.put(ParametersKeyString.ATTRIBUTE, ((RemoveAttribute) changeOperator).getAttributeToRemove());
                //TODO by TyphonML: missing entityname parameter
//                inputParameter.put(ParametersKeyString.ENTITYNAME, ((RemoveAttribute) changeOperator).getEntityName());
            }
            if (evolutionOperator == EvolutionOperator.RENAME) {
                inputParameter.put(ParametersKeyString.ATTRIBUTE, ((RenameAttribute) changeOperator).getAttributeToRename());
                inputParameter.put(ParametersKeyString.NEWATTRIBUTENAME, ((RenameAttribute) changeOperator).getNewName());
                //TODO by TyphonML: missing entityname parameter
//                inputParameter.put(ParametersKeyString.ENTITYNAME, ((RenameAttribute) changeOperator).getEntityName());
            }
            if (evolutionOperator == EvolutionOperator.CHANGETYPE) {
                inputParameter.put(ParametersKeyString.ATTRIBUTE, ((ChangeAttributeType) changeOperator).getAttributeToChange());
                inputParameter.put(ParametersKeyString.ATTRIBUTETYPE, ((ChangeAttributeType) changeOperator).getNewType());
                //TODO by TyphonML: missing entityname parameter
//                inputParameter.put(ParametersKeyString.ENTITYNAME, ((ChangeAttributeType) changeOperator).getEntityName());
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
                return RelationDOFactory.buildInstance((AddRelation) changeOperator, false);
            }
            if (this.getEvolutionOperator() == EvolutionOperator.ENABLECONTAINMENT) {
                return RelationDOFactory.buildInstance(((EnableRelationContainment) changeOperator).getRelation(), false);
            }
            if (this.getEvolutionOperator() == EvolutionOperator.DISABLECONTAINMENT) {
                return RelationDOFactory.buildInstance(((DisableRelationContainment) changeOperator).getRelation(), false);
            }
            if (this.getEvolutionOperator() == EvolutionOperator.ENABLEOPPOSITE) {
                return RelationDOFactory.buildInstance(((EnableBidirectionalRelation) changeOperator).getRelation(), false);
            }
            if (this.getEvolutionOperator() == EvolutionOperator.DISABLEOPPOSITE) {
                return RelationDOFactory.buildInstance(((DisableBidirectionalRelation) changeOperator).getRelation(), false);
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
