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
    private static final String CREATE = "create ";
    private static final String AS = " as ";
    private static final String DOT = ".";
    private static final String TYPE = " : ";
    private static final String RELATION = " -> ";
    private static final String CONTAINMENT = " :-> ";
    private static final String FROM = "from ";
    private static final String SELECT = "select ";
    private static final String WHERE = "where ";
    private static final String INSERT = "insert ";
    private static final String DROP = "drop ";
    private static final String OPENING_CURLY_BRACE = " { ";
    private static final String CLOSING_CURLY_BRACE = " } ";

    private Logger logger = LoggerFactory.getLogger(TyphonQLInterfaceImpl.class);

    public TyphonQLInterfaceImpl() {
    }

    @Override
    public String createEntity(String entityName, String databaseName, Model model) {
        logger.info("Create entity [{}] TyphonQL query", entityName);
        String tql = CREATE.concat(entityName).concat(AS).concat(databaseName);
        getTyphonQLConnection(model).query(tql);
        return tql;
    }

    @Override
    public String createEntityAttribute(String entityName, String attributeName, String attributeTypeName, Model model) {
        logger.info("Create attribute [{}: {}] for entity [{}] TyphonQL query", attributeName, attributeTypeName, entityName);
        String tql = CREATE.concat(entityName).concat(DOT).concat(attributeName).concat(TYPE).concat(attributeTypeName);
        getTyphonQLConnection(model).query(tql);
        return tql;
    }

    @Override
    public String createEntityRelation(String entityName, String relationName, boolean containment, String relationTargetTypeName, CardinalityDO cardinality, Model model) {
        logger.info("Create relation [{}" + (containment ? CONTAINMENT : RELATION) + "{} [{}]] for entity [{}] TyphonQL query", relationName, relationTargetTypeName, cardinality.getName(), entityName);
        String cardinalityValue = "";
        switch (cardinality.getValue()) {
            case CardinalityDO.ZERO_ONE_VALUE:
                cardinalityValue = "[0..1]";
                break;
            case CardinalityDO.ONE_VALUE:
                cardinalityValue = "[1..1]";
                break;
            case CardinalityDO.ZERO_MANY_VALUE:
                cardinalityValue = "[0..*]";
                break;
            case CardinalityDO.ONE_MANY_VALUE:
                cardinalityValue = "[1..*]";
                break;
        }
        String tql = CREATE.concat(entityName).concat(DOT).concat(relationName).concat(containment ? CONTAINMENT : RELATION).concat(relationTargetTypeName).concat(cardinalityValue);
        getTyphonQLConnection(model).query(tql);
        return tql;
    }

    @Override
    public String selectEntityData(String entityName, Model model) {
        logger.info("Select data for entity [{}] TyphonQL query", entityName);
        String tql = FROM.concat(entityName).concat(BLANK).concat(entityName.toLowerCase()).concat(BLANK).concat(SELECT).concat(entityName.toLowerCase());
        getTyphonQLConnection(model).query(tql);
        return tql;
    }

    @Override
    public String insertEntityData(String entityName, Set<String> entityAttributes, Model model) {
        logger.info("Insert working set data for entity [{}] TyphonQL query", entityName);
        String tql = INSERT.concat(entityName).concat(OPENING_CURLY_BRACE).concat(String.join(":, ", entityAttributes).concat(":")).concat(CLOSING_CURLY_BRACE);
        getTyphonQLConnection(model).query(tql);
        return tql;
    }

    @Override
    public String dropEntity(String entityName, Model model) {
        logger.info("Drop entity [{}] TyphonQL query", entityName);
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
        logger.info("Rename EntityDO [{}] to [{}] via TyphonQL on TyphonML model [{}]", oldEntityName, newEntityName, model);
        String tql = "rename " + oldEntityName + " to " + newEntityName;
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public WorkingSet readAllEntityData(EntityDO entity, Model model) {
        return getTyphonQLConnection(model).query(FROM.concat("%s e ").concat(SELECT).concat("e"), entity.getName());
    }

    @Override
    public WorkingSet readAllEntityData(String entityId, Model model) {
        return getTyphonQLConnection(model).query(FROM.concat("%s e ").concat(SELECT).concat("e"), entityId);
    }

    @Override
    public WorkingSet readEntityDataEqualAttributeValue(String sourceEntityName, String attributeName, String attributeValue, Model model) {
        return getTyphonQLConnection(model).query(FROM.concat("%s e ").concat(SELECT).concat("e ").concat(WHERE).concat("%s = %s"), sourceEntityName, attributeName, attributeValue);
    }

    @Override
    public WorkingSet readEntityDataSelectAttributes(String sourceEntityName, List<String> attributes, Model model) {
        return getTyphonQLConnection(model).query(FROM.concat("%s e ").concat(SELECT) + attributes.stream().map("e."::concat).collect(Collectors.joining(",")), sourceEntityName);
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
        logger.info("Delete Relationship [{}] in [{}] via TyphonQL on TyphonML model [{}]", relationname, entityname, model);
        String tql = "drop " + entityname + "." + relationname;
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void enableContainment(String relationName, String entityname, Model model) {
        logger.info("Enabling containment Relationship [{}] in [{}] via TyphonQL on TyphonML model [{}]", relationName, entityname, model);
        String tql = "change " + entityname + "." + relationName + " :->";
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void disableContainment(String relationName, String entityname, Model model) {
        logger.info("Disabling containment Relationship [{}] in [{}] via TyphonQL on TyphonML model [{}]", relationName, entityname, model);
        String tql = "change " + entityname + "." + relationName + " ->";
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void changeCardinalityInRelation(String relationname, String entityname, CardinalityDO cardinality, Model model) {
        logger.info("Change cardinality of Relationship [{}] in [{}] via TyphonQL on TyphonML model [{}]", relationname, entityname, model);
        String cardinalityValue = "";
        switch (cardinality.getValue()) {
            case CardinalityDO.ZERO_ONE_VALUE:
                cardinalityValue = "[0..1]";
                break;
            case CardinalityDO.ONE_VALUE:
                cardinalityValue = "[1..1]";
                break;
            case CardinalityDO.ZERO_MANY_VALUE:
                cardinalityValue = "[0..*]";
                break;
            case CardinalityDO.ONE_MANY_VALUE:
                cardinalityValue = "[1..*]";
                break;
        }
        String tql = "change " + entityname + "." + relationname + " " + cardinalityValue;
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void addAttribute(AttributeDO attributeDO, String entityname, Model model) {
        logger.info("Add attribute [{}] to entity [{}]  via TyphonQL on TyphonML model [{}]", attributeDO.getName(), entityname, model);
        String tql = createEntityAttribute(entityname, attributeDO.getName(), attributeDO.getDataTypeDO().getName(), model);
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void renameRelation(String entityName, String relationName, String newRelationName, Model model) {
        logger.info("Rename Relation [{}] to [{}] via TyphonQL on TyphonML model [{}]", relationName, newRelationName, model);
        String tql = "rename " + entityName + "." + relationName + " to " + newRelationName;
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void renameAttribute(String oldAttributeName, String newAttributeName, String entityName, Model model) {
        logger.info("Rename attribute [from '{}' to '{}'] in entity [{}]  via TyphonQL on TyphonML model [{}]", oldAttributeName, newAttributeName, entityName, model);
        String tql = "rename " + entityName + "." + oldAttributeName + " to " + newAttributeName;
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void changeTypeAttribute(AttributeDO attribute, String entityName, Model model) {
        logger.info("Change type attribute ['{}' to '{}' type] in entity [{}]  via TyphonQL on TyphonML model [{}]", attribute.getName(), attribute.getDataTypeDO().getName(), entityName, model);
        String tql = "change " + entityName + "." + attribute.getName() + " : " + attribute.getDataTypeDO().getName();
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void removeAttribute(String entityName, String attribute, Model model) {
        //TODO Separate deletion of data and structure.
        // Delete data
//        getTyphonQLConnection(model).delete(this.readEntityDataSelectAttributes(entityName, attribute, model));

        //Delete Structure
        String tql = "drop " + entityName + "." + attribute;
        logger.info("Delete attributes [{}] via TyphonQL DDL on TyphonML model [{}] ", entityName, model);
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void deleteWorkingSetData(WorkingSet dataToDelete, Model model) {
        getTyphonQLConnection(model).delete(dataToDelete);
    }

    @Override
    public void createRelationshipType(RelationDO relation, Model model) {
        String tql;
        logger.info("Create relationship [{}] via TyphonQL DDL query on TyphonML model [{}] ", relation.getName(), model);
        String cardinalityValue = "";
        switch (relation.getCardinality().getValue()) {
            case CardinalityDO.ZERO_ONE_VALUE:
                cardinalityValue = "[0..1]";
                break;
            case CardinalityDO.ONE_VALUE:
                cardinalityValue = "[1..1]";
                break;
            case CardinalityDO.ZERO_MANY_VALUE:
                cardinalityValue = "[0..*]";
                break;
            case CardinalityDO.ONE_MANY_VALUE:
                cardinalityValue = "[1..*]";
                break;
        }
        tql = "create " + relation.getSourceEntity().getName() + "." + relation.getName() + " -> " + relation.getTargetEntity().getName() + cardinalityValue;
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }


}
