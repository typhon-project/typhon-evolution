package com.typhon.evolutiontool.services.typhonQL;


import com.typhon.evolutiontool.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import typhonml.Model;

import java.util.List;
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

    private Logger logger = LoggerFactory.getLogger(TyphonQLInterfaceImpl.class);

    public TyphonQLInterfaceImpl() {
    }

    @Override
    public String createEntity(String entityName, String databaseName) {
        logger.info("Create entity [{}] TyphonQL query", entityName);
        return CREATE.concat(entityName).concat(AS).concat(databaseName);
    }

    @Override
    public String createEntityAttribute(String entityName, String attributeName, String attributeTypeName) {
        logger.info("Create attribute [{}: {}] for entity [{}] TyphonQL query", attributeName, attributeTypeName, entityName);
        return CREATE.concat(entityName).concat(DOT).concat(attributeName).concat(TYPE).concat(attributeTypeName);
    }

    @Override
    public String createEntityRelation(String entityName, String relationName, boolean containment, String relationTargetTypeName, CardinalityDO cardinality) {
        logger.info("Create relation [{}" + (containment ? CONTAINMENT : RELATION) + "{} [{}]] for entity [{}] TyphonQL query", relationName, relationTargetTypeName, cardinality.getName(), entityName);
        String cardinalityValue = "";
        switch (cardinality.getValue()) {
            case CardinalityDO.ZERO_ONE_VALUE : cardinalityValue = "[0..1]"; break;
            case CardinalityDO.ONE_VALUE : cardinalityValue = "[1..1]"; break;
            case CardinalityDO.ZERO_MANY_VALUE : cardinalityValue = "[0..*]"; break;
            case CardinalityDO.ONE_MANY_VALUE : cardinalityValue = "[1..*]"; break;
        }
        return CREATE.concat(entityName).concat(DOT).concat(relationName).concat(containment ? CONTAINMENT : RELATION).concat(relationTargetTypeName).concat(cardinalityValue);
    }

    @Override
    public String selectEntityData(String entityName) {
        logger.info("Select data for entity [{}] TyphonQL query", entityName);
        return FROM.concat(entityName).concat(BLANK).concat(entityName.toLowerCase()).concat(BLANK).concat(SELECT).concat(entityName.toLowerCase());
    }

    private TyphonQLConnection getTyphonQLConnection(Model model) {
        //TODO Model vs TyphonMLSchema specif?
        TyphonQLConnection typhonQLConnection = new TyphonQLConnectionImpl();
        typhonQLConnection.setSchema(model);
        return typhonQLConnection;
    }

    @Override
    public String createEntityType(EntityDO newEntity, Model model) {
        //TODO Handling of Identifier, index, etc...
        String tql;
        logger.info("Create entity [{}] via TyphonQL DDL query on TyphonML model [{}] ", newEntity.getName(), model);
        tql = "CREATE ENTITY " + newEntity.getName() + " {" + newEntity.getAttributes().entrySet().stream().map(entry -> entry.getKey() + " " + entry.getValue()).collect(Collectors.joining(",")) + "}";
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
        return tql;
    }

    @Override
    public String createEntityType(typhonml.Entity newEntity, Model model) {
        //TODO Handling of Identifier, index, etc...
        String tql;
        logger.info("Create entity [{}] via TyphonQL DDL query on TyphonML model [{}] ", newEntity.getName(), model);
        tql = "TQLDDL CREATE ENTITY " + newEntity.getName() + " {" + newEntity.getAttributes().stream().map(attribute -> attribute.getName() + " " + attribute.getType()).collect(Collectors.joining(",")) + "}";
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
        return tql;
    }

    @Override
    public void renameEntity(String oldEntityName, String newEntityName, Model model) {
        logger.info("Rename EntityDO [{}] to [{}] via TyphonQL on TyphonML model [{}]", oldEntityName, newEntityName, model);
        String tql = "TQL DDL RENAME ENTITY " + oldEntityName + " TO " + newEntityName;
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
        String tql = "TQL DDL DELETE RELATION " + relationname + " IN " + entityname;
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void enableContainment(String relationName, String entityname, Model model) {
        logger.info("Enabling containment Relationship [{}] in [{}] via TyphonQL on TyphonML model [{}]", relationName, entityname, model);
        String tql = "dummy enable containment TQL DDL";
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void disableContainment(String relationName, String entityname, Model model) {
        logger.info("Disabling containment Relationship [{}] in [{}] via TyphonQL on TyphonML model [{}]", relationName, entityname, model);
        String tql = "dummy disable containment TQL DDL";
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void changeCardinalityInRelation(String relationname, String entityname, CardinalityDO cardinality, Model model) {
        logger.info("Change cardinality of Relationship [{}] in [{}] via TyphonQL on TyphonML model [{}]", relationname, entityname, model);
        String tql = "TQL DDL dummy change relation cardinality";
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void addAttribute(AttributeDO attributeDO, String entityname, Model model) {
        logger.info("Add attribute [{}] to entity [{}]  via TyphonQL on TyphonML model [{}]", attributeDO.getName(), entityname, model);
        String tql = "TQL DDL ADD ATTRIBUTE " + attributeDO.getName() + " IN " + entityname;
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void renameRelation(String relationName, String newRelationName, Model model) {
        logger.info("Rename Relation [{}] to [{}] via TyphonQL on TyphonML model [{}]", relationName, newRelationName, model);
        String tql = "TQL DDL RENAME RELATION " + relationName + " TO " + newRelationName;
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void renameAttribute(String oldAttributeName, String newAttributeName, String entityName, Model model) {
        logger.info("Rename attribute [from '{}' to '{}'] in entity [{}]  via TyphonQL on TyphonML model [{}]", oldAttributeName, newAttributeName, entityName, model);
        String tql = "TQL DDL RENAME ATTRIBUTE " + oldAttributeName + " TO " + newAttributeName + " IN " + entityName;
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void changeTypeAttribute(AttributeDO attribute, String entityName, Model model) {
        logger.info("Change type attribute ['{}' to '{}' type] in entity [{}]  via TyphonQL on TyphonML model [{}]", attribute.getName(), attribute.getDataTypeDO().getName(), entityName, model);
        String tql = "TQL DDL UPDATE ATTRIBUTE " + attribute.getName() + " SET TYPE TO " + attribute.getDataTypeDO().getName() + " IN " + entityName;
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void deleteEntityStructure(String entityname, Model model) {
        String tql = "TQLDDL DELETE ENTITY " + entityname + " on TyphonML [" + model + "]";
        logger.info("Delete entity [{}] via TyphonQL DDL on TyphonML model [{}] ", entityname, model);
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }

    @Override
    public void removeAttributes(String entityName, List<String> attributes, Model model) {
        //TODO Separate deletion of data and structure.
        // Delete data
        getTyphonQLConnection(model).delete(this.readEntityDataSelectAttributes(entityName, attributes, model));

        //Delete Structure
        String tql = "TQLDDL DELETE ATTRIBUTES " + entityName + ", " + attributes.stream().map("e."::concat).collect(Collectors.joining(",")) + " on TyphonML [" + model + "]";
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
        tql = "TQLDDL CREATE RELATIONSHIP " + relation;
        getTyphonQLConnection(model).executeTyphonQLDDL(tql);
    }


}
