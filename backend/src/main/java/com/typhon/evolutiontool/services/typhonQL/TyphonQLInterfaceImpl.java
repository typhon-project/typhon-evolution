package com.typhon.evolutiontool.services.typhonQL;

import com.typhon.evolutiontool.dummy.WorkingSetDummyImpl;
import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import typhonml.Model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private TyphonQLConnection typhonQLConnection;

    public TyphonQLInterfaceImpl() {
        this.typhonQLConnection = new TyphonQLConnectionImpl();
    }

    @Override
    public String uploadSchema(Model model) {
        //The web service needs the model as a Json object: we write the Model into a temporary XMI file, and then read the XMI file as a String
        try {
            TyphonMLUtils.saveModel(model, "tempXMI.xmi");
            StringBuilder contentBuilder = new StringBuilder();
            try (Stream<String> stream = Files.lines(Paths.get("tempXMI.xmi"), StandardCharsets.UTF_8)) {
                stream.forEach(contentBuilder::append);
            }
            //Upload the new version of the model
            getTyphonQLConnection().uploadModel(contentBuilder.toString());
            //Delete the temporary file
            File xmiFile = new File("tempXMI.xmi");
            xmiFile.deleteOnExit();
        } catch (IOException e) {
            logger.warn("A temporary file 'tempXMI.xmi' has not been deleted, do it manually");
        }
        return "success";
    }

    @Override
    public String createEntity(String entityName, String databaseName) {
        logger.debug("Create entity [{}] TyphonQL query", entityName);
        String tql = new StringBuilder(CREATE).append(entityName).append(AT).append(databaseName).toString();
        getTyphonQLConnection().query(tql);
        return tql;
    }

    @Override
    public String createEntityAttribute(String entityName, String attributeName, String attributeTypeName) {
        logger.debug("Create attribute [{}: {}] for entity [{}] TyphonQL query", attributeName, attributeTypeName, entityName);
        String tql = new StringBuilder(CREATE).append(entityName).append(DOT).append(attributeName).append(COLON).append(attributeTypeName).toString();
        getTyphonQLConnection().query(tql);
        return tql;
    }

    @Override
    public String createEntityRelation(String entityName, String relationName, boolean containment, String relationTargetTypeName, CardinalityDO cardinality) {
        logger.debug("Create relation [{}" + (containment ? CONTAINMENT : RELATION) + "{} [{}]] for entity [{}] TyphonQL query", relationName, relationTargetTypeName, cardinality.getName(), entityName);
        String tql = new StringBuilder(CREATE).append(entityName).append(DOT).append(relationName).append(containment ? CONTAINMENT : RELATION).append(relationTargetTypeName).append(getCardinalityValue(cardinality.getValue())).toString();
        getTyphonQLConnection().query(tql);
        return tql;
    }

    @Override
    public WorkingSet selectEntityData(String entityName) {
        logger.debug("Select data for entity [{}] TyphonQL query", entityName);
        String tql = new StringBuilder(FROM).append(entityName).append(BLANK).append(entityName.toLowerCase()).append(BLANK).append(SELECT).append(entityName.toLowerCase()).toString();
        getTyphonQLConnection().query(tql);
        //TODO return the WorkingSet data
        return new WorkingSetDummyImpl();
    }

    @Override
    public String insertEntityData(String entityName, Set<String> entityAttributes, WorkingSet ws) {
        logger.debug("Insert working set data for entity [{}] TyphonQL query", entityName);
        String tql = new StringBuilder(INSERT).append(entityName).append(OPENING_CURLY_BRACE).append(String.join(":, ", entityAttributes)).append(":").append(CLOSING_CURLY_BRACE).toString();
        getTyphonQLConnection().query(tql);
        return tql;
    }

    @Override
    public String dropEntity(String entityName) {
        logger.debug("Drop entity [{}] TyphonQL query", entityName);
        String tql = new StringBuilder(DROP).append(entityName).toString();
        getTyphonQLConnection().query(tql);
        return tql;
    }

    @Override
    public void renameEntity(String oldEntityName, String newEntityName) {
        logger.debug("Rename EntityDO [{}] to [{}] via TyphonQL on TyphonML model [{}]", oldEntityName, newEntityName);
        String tql = new StringBuilder(RENAME).append(oldEntityName).append(TO).append(newEntityName).toString();
        getTyphonQLConnection().executeTyphonQLDDL(tql);
    }

    @Override
    public WorkingSet readAllEntityData(EntityDO entity) {
        return getTyphonQLConnection().query(new StringBuilder(FROM).append(entity.getName()).append(BLANK).append(entity.getName().toLowerCase()).append(BLANK).append(SELECT).append(entity.getName().toLowerCase()).toString());
    }

    @Override
    public WorkingSet readAllEntityData(String entityId) {
        return getTyphonQLConnection().query(new StringBuilder(FROM).append(entityId).append(BLANK).append(entityId.toLowerCase()).append(BLANK).append(SELECT).append(entityId.toLowerCase()).toString(), entityId);
    }

    @Override
    public WorkingSet readEntityDataEqualAttributeValue(String sourceEntityName, String attributeName, String attributeValue) {
        return getTyphonQLConnection().query(new StringBuilder(FROM).append(sourceEntityName).append(BLANK).append(sourceEntityName.toLowerCase()).append(BLANK).append(SELECT).append(sourceEntityName.toLowerCase()).append(BLANK).append(WHERE).append(attributeName).append(EQUALS).append(attributeValue).toString());
    }

    @Override
    public WorkingSet readEntityDataSelectAttributes(String sourceEntityName, Set<String> attributes) {
        return getTyphonQLConnection().query(new StringBuilder(FROM).append(sourceEntityName).append(BLANK).append(sourceEntityName.toLowerCase()).append(BLANK).append(SELECT).toString() + attributes.stream().map(sourceEntityName.toLowerCase().concat(DOT)::concat).collect(Collectors.joining(COMMA)));
    }

    @Override
    public void deleteAllEntityData(String entityid) {
        getTyphonQLConnection().delete(this.readAllEntityData(entityid));
    }

    @Override
    public void writeWorkingSetData(WorkingSet workingSetData) {
        getTyphonQLConnection().insert(workingSetData);
    }

    @Override
    public void deleteForeignKey(EntityDO sourceEntity, EntityDO targetEntity) {

    }

    @Override
    public WorkingSet readRelationship(RelationDO relation) {
        return getTyphonQLConnection().query(new StringBuilder(FROM).append("%s s , %s t ").append(SELECT).append("s, t ").append(WHERE).append("s.%s==%s").toString(), relation.getSourceEntity().getName(), relation.getTargetEntity().getName(), relation.getName(), relation.getTargetEntity().getIdentifier());
    }

    @Override
    public void deleteRelationship(RelationDO relation, boolean datadelete) {
        this.deleteForeignKey(relation.getSourceEntity(), relation.getTargetEntity());
        if (datadelete) {
            //For nosql document db
            //this.deleteAttributes(relation.getSourceEntity().getName(), Arrays.asList(relation.getName()));
            // for relational?
            getTyphonQLConnection().delete(this.readRelationship(relation));
        }
    }

    @Override
    public void deleteRelationshipInEntity(String relationname, String entityname) {
        logger.debug("Delete Relationship [{}] in [{}] via TyphonQL on TyphonML model", relationname, entityname);
        String tql = new StringBuilder(DROP).append(entityname).append(DOT).append(relationname).toString();
        getTyphonQLConnection().executeTyphonQLDDL(tql);
    }

    @Override
    public void enableContainment(String relationName, String entityname) {
        logger.debug("Enabling containment Relationship [{}] in [{}] via TyphonQL on TyphonML model", relationName, entityname);
        String tql = new StringBuilder(CHANGE).append(entityname).append(DOT).append(relationName).append(CONTAINMENT).toString();
        getTyphonQLConnection().executeTyphonQLDDL(tql);
    }

    @Override
    public void disableContainment(String relationName, String entityname) {
        logger.debug("Disabling containment Relationship [{}] in [{}] via TyphonQL on TyphonML model", relationName, entityname);
        String tql = new StringBuilder(CHANGE).append(entityname).append(DOT).append(relationName).append(RELATION).toString();
        getTyphonQLConnection().executeTyphonQLDDL(tql);
    }

    @Override
    public void changeCardinalityInRelation(String relationname, String entityname, CardinalityDO cardinality) {
        logger.debug("Change cardinality of Relationship [{}] in [{}] via TyphonQL on TyphonML model", relationname, entityname);
        String tql = new StringBuilder(CHANGE).append(entityname).append(DOT).append(relationname).append(BLANK).append(getCardinalityValue(cardinality.getValue())).toString();
        getTyphonQLConnection().executeTyphonQLDDL(tql);
    }

    @Override
    public void addAttribute(AttributeDO attributeDO, String entityname) {
        logger.debug("Add attribute [{}] to entity [{}]  via TyphonQL on TyphonML model", attributeDO.getName(), entityname);
        String tql = createEntityAttribute(entityname, attributeDO.getName(), attributeDO.getDataTypeDO().getName());
        getTyphonQLConnection().executeTyphonQLDDL(tql);
    }

    @Override
    public void renameRelation(String entityName, String relationName, String newRelationName) {
        logger.debug("Rename Relation [{}] to [{}] via TyphonQL on TyphonML model", relationName, newRelationName);
        String tql = new StringBuilder(RENAME).append(entityName).append(DOT).append(relationName).append(TO).append(newRelationName).toString();
        getTyphonQLConnection().executeTyphonQLDDL(tql);
    }

    @Override
    public void renameAttribute(String oldAttributeName, String newAttributeName, String entityName) {
        logger.debug("Rename attribute [from '{}' to '{}'] in entity [{}]  via TyphonQL on TyphonML model", oldAttributeName, newAttributeName, entityName);
        String tql = new StringBuilder(RENAME).append(entityName).append(DOT).append(oldAttributeName).append(TO).append(newAttributeName).toString();
        getTyphonQLConnection().executeTyphonQLDDL(tql);
    }

    @Override
    public void changeTypeAttribute(AttributeDO attribute, String entityName) {
        logger.debug("Change type attribute ['{}' to '{}' type] in entity [{}]  via TyphonQL on TyphonML model", attribute.getName(), attribute.getDataTypeDO().getName(), entityName);
        String tql = new StringBuilder(CHANGE).append(entityName).append(DOT).append(attribute.getName()).append(COLON).append(attribute.getDataTypeDO().getName()).toString();
        getTyphonQLConnection().executeTyphonQLDDL(tql);
    }

    @Override
    public void removeAttribute(String entityName, String attribute) {
        //TODO Separate deletion of data and structure.
        // Delete data
//        getTyphonQLConnection(model).delete(this.readEntityDataSelectAttributes(entityName, attribute));

        //Delete Structure
        String tql = new StringBuilder(DROP).append(entityName).append(DOT).append(attribute).toString();
        logger.debug("Delete attributes [{}] via TyphonQL DDL on TyphonML model", entityName);
        getTyphonQLConnection().executeTyphonQLDDL(tql);
    }

    @Override
    public void deleteWorkingSetData(WorkingSet dataToDelete) {
        getTyphonQLConnection().delete(dataToDelete);
    }

    @Override
    public void createRelationshipType(RelationDO relation) {
        String tql;
        logger.debug("Create relationship [{}] via TyphonQL DDL query on TyphonML model", relation.getName());
        tql = new StringBuilder(CREATE).append(relation.getSourceEntity().getName()).append(DOT).append(relation.getName()).append(RELATION).append(relation.getTargetEntity().getName()).append(getCardinalityValue(relation.getCardinality().getValue())).toString();
        getTyphonQLConnection().executeTyphonQLDDL(tql);
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

    private TyphonQLConnection getTyphonQLConnection() {
        return typhonQLConnection;
    }

}
