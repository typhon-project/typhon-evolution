package com.typhon.evolutiontool.services.typhonQL;


import com.typhon.evolutiontool.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import typhonml.Model;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class TyphonQLInterfaceImpl implements TyphonQLInterface {

    private static final String BLANK = " ";
    private static final String DOT = ".";
    private static final String COMMA = ",";
    private static final String COLON = " : ";
    private static final String EQUALS = " = ";
    private static final String OPENING_CURLY_BRACE = " { ";
    private static final String CLOSING_CURLY_BRACE = " } ";
    private static final String RELATION = " -> ";
    private static final String CONTAINMENT = " :-> ";
    private static final String FROM = "from ";
    private static final String SELECT = "select ";
    private static final String WHERE = "where ";
    private static final String INSERT = "insert ";
    private static final String CREATE = "create ";
    private static final String RENAME = "rename ";
    private static final String CHANGE = "change ";
    private static final String DROP = "drop ";
    private static final String AT = " at ";
    private static final String TO = " to ";
    private static final String CARDINALITY_ZERO_ONE = "[0..1]";
    private static final String CARDINALITY_ONE_ONE = "[1..1]";
    private static final String CARDINALITY_ZERO_MANY = "[0..*]";
    private static final String CARDINALITY_ONE_MANY = "[1..*]";

    private Logger logger = LoggerFactory.getLogger(TyphonQLInterfaceImpl.class);

    public TyphonQLInterfaceImpl() {
    }

    @Override
    public String createEntity(String entityName, String databaseName, Model model) {
        logger.debug("Create entity [{}] TyphonQL query", entityName);
        String tql = CREATE.concat(entityName).concat(AT).concat(databaseName);
        getTyphonQLConnection(model).query(tql);
        return tql;
    }

    @Override
    public String createEntityAttribute(String entityName, String attributeName, String attributeTypeName, Model model) {
        logger.debug("Create attribute [{}: {}] for entity [{}] TyphonQL query", attributeName, attributeTypeName, entityName);
        String tql = CREATE.concat(entityName).concat(DOT).concat(attributeName).concat(COLON).concat(attributeTypeName);
        getTyphonQLConnection(model).query(tql);
        return tql;
    }

    @Override
    public String createEntityRelation(String entityName, String relationName, boolean containment, String relationTargetTypeName, CardinalityDO cardinality, Model model) {
        logger.debug("Create relation [{}" + (containment ? CONTAINMENT : RELATION) + "{} [{}]] for entity [{}] TyphonQL query", relationName, relationTargetTypeName, cardinality.getName(), entityName);
        String tql = CREATE.concat(entityName).concat(DOT).concat(relationName).concat(containment ? CONTAINMENT : RELATION).concat(relationTargetTypeName).concat(getCardinalityValue(cardinality.getValue()));
        getTyphonQLConnection(model).query(tql);
        return tql;
    }

    @Override
    public String selectEntityData(String entityName, Model model) {
        logger.debug("Select data for entity [{}] TyphonQL query", entityName);
        String tql = FROM.concat(entityName).concat(BLANK).concat(entityName.toLowerCase()).concat(BLANK).concat(SELECT).concat(entityName.toLowerCase());
        getTyphonQLConnection(model).query(tql);
        return tql;
    }

    @Override
    public String insertEntityData(String entityName, Set<String> entityAttributes, Model model) {
        logger.debug("Insert working set data for entity [{}] TyphonQL query", entityName);
        String tql = INSERT.concat(entityName).concat(OPENING_CURLY_BRACE).concat(String.join(":, ", entityAttributes).concat(":")).concat(CLOSING_CURLY_BRACE);
        getTyphonQLConnection(model).query(tql);
        return tql;
    }

    @Override
    public String dropEntity(String entityName, Model model) {
        logger.debug("Drop entity [{}] TyphonQL query", entityName);
        String tql = DROP.concat(entityName);
        getTyphonQLConnection(model).query(tql);
        return tql;
    }

    private TyphonQLConnection getTyphonQLConnection(Model model) {
        //TODO Model vs TyphonMLSchema specif?
        TyphonQLConnection typhonQLConnection = new TyphonQLConnectionImpl();
        typhonQLConnection.setSchema(model);
        return typhonQLConnection;
    }

    @Override
    public void renameEntity(String oldEntityName, String newEntityName, Model model) {
        logger.debug("Rename EntityDO [{}] to [{}] via TyphonQL on TyphonML model [{}]", oldEntityName, newEntityName, model);
        String tql = RENAME.concat(oldEntityName).concat(TO).concat(newEntityName);
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public WorkingSet readAllEntityData(EntityDO entity, Model model) {
        return getTyphonQLConnection(model).query(FROM.concat(entity.getName()).concat(BLANK).concat(entity.getName().toLowerCase()).concat(SELECT).concat(entity.getName().toLowerCase()));
    }

    @Override
    public WorkingSet readAllEntityData(String entityId, Model model) {
        return getTyphonQLConnection(model).query(FROM.concat(entityId).concat(BLANK).concat(entityId.toLowerCase()).concat(SELECT).concat(entityId.toLowerCase()), entityId);
    }

    @Override
    public WorkingSet readEntityDataEqualAttributeValue(String sourceEntityName, String attributeName, String attributeValue, Model model) {
        return getTyphonQLConnection(model).query(FROM.concat(sourceEntityName).concat(BLANK).concat(sourceEntityName.toLowerCase()).concat(SELECT).concat(sourceEntityName.toLowerCase()).concat(BLANK).concat(WHERE).concat(attributeName).concat(EQUALS).concat(attributeValue));
    }

    @Override
    public WorkingSet readEntityDataSelectAttributes(String sourceEntityName, List<String> attributes, Model model) {
        return getTyphonQLConnection(model).query(FROM.concat(sourceEntityName).concat(BLANK).concat(sourceEntityName.toLowerCase()).concat(SELECT) + attributes.stream().map(sourceEntityName.toLowerCase().concat(DOT)::concat).collect(Collectors.joining(COMMA)));
    }

    @Override
    public void deleteAllEntityData(String entityid, Model model) {
        getTyphonQLConnection(model).delete(this.readAllEntityData(entityid, model));
    }

    @Override
    public void writeWorkingSetData(WorkingSet workingSetData, Model model) {
        getTyphonQLConnection(model).insert(workingSetData);
    }

    @Override
    public void deleteForeignKey(EntityDO sourceEntity, EntityDO targetEntity) {

    }

    @Override
    public WorkingSet readRelationship(RelationDO relation, Model model) {
        return getTyphonQLConnection(model).query(FROM.concat("%s s , %s t ").concat(SELECT).concat("s, t ").concat(WHERE).concat("s.%s==%s"), relation.getSourceEntity().getName(), relation.getTargetEntity().getName(), relation.getName(), relation.getTargetEntity().getIdentifier());
    }

    @Override
    public void deleteRelationship(RelationDO relation, boolean datadelete, Model model) {
        this.deleteForeignKey(relation.getSourceEntity(), relation.getTargetEntity());
        if (datadelete) {
            //For nosql document db
            //this.deleteAttributes(relation.getSourceEntity().getName(), Arrays.asList(relation.getName()), model);
            // for relational?
            getTyphonQLConnection(model).delete(this.readRelationship(relation, model));
        }
    }

    @Override
    public void deleteRelationshipInEntity(String relationname, String entityname, Model model) {
        logger.debug("Delete Relationship [{}] in [{}] via TyphonQL on TyphonML model [{}]", relationname, entityname, model);
        String tql = DROP.concat(entityname).concat(DOT).concat(relationname);
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void enableContainment(String relationName, String entityname, Model model) {
        logger.debug("Enabling containment Relationship [{}] in [{}] via TyphonQL on TyphonML model [{}]", relationName, entityname, model);
        String tql = CHANGE.concat(entityname).concat(DOT).concat(relationName).concat(CONTAINMENT);
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void disableContainment(String relationName, String entityname, Model model) {
        logger.debug("Disabling containment Relationship [{}] in [{}] via TyphonQL on TyphonML model [{}]", relationName, entityname, model);
        String tql = CHANGE.concat(entityname).concat(DOT).concat(relationName).concat(RELATION);
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void changeCardinalityInRelation(String relationname, String entityname, CardinalityDO cardinality, Model model) {
        logger.debug("Change cardinality of Relationship [{}] in [{}] via TyphonQL on TyphonML model [{}]", relationname, entityname, model);
        String tql = CHANGE.concat(entityname).concat(DOT).concat(relationname).concat(BLANK).concat(getCardinalityValue(cardinality.getValue()));
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void addAttribute(AttributeDO attributeDO, String entityname, Model model) {
        logger.debug("Add attribute [{}] to entity [{}]  via TyphonQL on TyphonML model [{}]", attributeDO.getName(), entityname, model);
        String tql = createEntityAttribute(entityname, attributeDO.getName(), attributeDO.getDataTypeDO().getName(), model);
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void renameRelation(String entityName, String relationName, String newRelationName, Model model) {
        logger.debug("Rename Relation [{}] to [{}] via TyphonQL on TyphonML model [{}]", relationName, newRelationName, model);
        String tql = RENAME.concat(entityName).concat(DOT).concat(relationName).concat(TO).concat(newRelationName);
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void renameAttribute(String oldAttributeName, String newAttributeName, String entityName, Model model) {
        logger.debug("Rename attribute [from '{}' to '{}'] in entity [{}]  via TyphonQL on TyphonML model [{}]", oldAttributeName, newAttributeName, entityName, model);
        String tql = RENAME.concat(entityName).concat(DOT).concat(oldAttributeName).concat(TO).concat(newAttributeName);
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void changeTypeAttribute(AttributeDO attribute, String entityName, Model model) {
        logger.debug("Change type attribute ['{}' to '{}' type] in entity [{}]  via TyphonQL on TyphonML model [{}]", attribute.getName(), attribute.getDataTypeDO().getName(), entityName, model);
        String tql = CHANGE.concat(entityName).concat(DOT).concat(attribute.getName()).concat(COLON).concat(attribute.getDataTypeDO().getName());
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void removeAttribute(String entityName, String attribute, Model model) {
        //TODO Separate deletion of data and structure.
        // Delete data
//        getTyphonQLConnection(model).delete(this.readEntityDataSelectAttributes(entityName, attribute, model));

        //Delete Structure
        String tql = DROP.concat(entityName).concat(DOT).concat(attribute);
        logger.debug("Delete attributes [{}] via TyphonQL DDL on TyphonML model [{}] ", entityName, model);
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void deleteWorkingSetData(WorkingSet dataToDelete, Model model) {
        getTyphonQLConnection(model).delete(dataToDelete);
    }

    @Override
    public void createRelationshipType(RelationDO relation, Model model) {
        String tql;
        logger.debug("Create relationship [{}] via TyphonQL DDL query on TyphonML model [{}] ", relation.getName(), model);
        tql = CREATE.concat(relation.getSourceEntity().getName()).concat(DOT).concat(relation.getName()).concat(RELATION).concat(relation.getTargetEntity().getName()).concat(getCardinalityValue(relation.getCardinality().getValue()));
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    private String getCardinalityValue(int cardinality) {
        switch (cardinality) {
            case CardinalityDO.ZERO_ONE_VALUE:
                return CARDINALITY_ZERO_ONE;
            case CardinalityDO.ONE_VALUE:
                return CARDINALITY_ONE_ONE;
            case CardinalityDO.ZERO_MANY_VALUE:
                return CARDINALITY_ZERO_MANY;
            case CardinalityDO.ONE_MANY_VALUE:
                return CARDINALITY_ONE_MANY;
        }
        return "";
    }

}
