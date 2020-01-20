package com.typhon.evolutiontool.services.typhonQL;

import com.typhon.evolutiontool.client.TyphonQLWebServiceClient;
import com.typhon.evolutiontool.client.TyphonQLWebServiceClientImpl;
import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import typhonml.Model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
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
    private static final String DELETE = "delete ";
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

    private TyphonQLWebServiceClient typhonQLWebServiceClient;

    public TyphonQLInterfaceImpl() {
        this.typhonQLWebServiceClient = new TyphonQLWebServiceClientImpl();
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
            getTyphonQLWebServiceClient().uploadModel(contentBuilder.toString());
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
        getTyphonQLWebServiceClient().update(tql);
        return tql;
    }

    @Override
    public String createEntityAttribute(String entityName, String attributeName, String attributeTypeName) {
        logger.debug("Create attribute [{}: {}] for entity [{}] TyphonQL query", attributeName, attributeTypeName, entityName);
        String tql = new StringBuilder(CREATE).append(entityName).append(DOT).append(attributeName).append(COLON).append(covertMLTypeToQLType(attributeTypeName)).toString();
        getTyphonQLWebServiceClient().update(tql);
        return tql;
    }

    @Override
    public String createEntityRelation(String entityName, String relationName, boolean containment, String relationTargetTypeName, CardinalityDO cardinality) {
        logger.debug("Create relation [{}" + (containment ? CONTAINMENT : RELATION) + "{} [{}]] for entity [{}] TyphonQL query", relationName, relationTargetTypeName, cardinality.getName(), entityName);
        String tql = new StringBuilder(CREATE).append(entityName).append(DOT).append(relationName).append(containment ? CONTAINMENT : RELATION).append(relationTargetTypeName).append(getCardinalityValue(cardinality.getValue())).toString();
        getTyphonQLWebServiceClient().update(tql);
        return tql;
    }

    @Override
    public WorkingSet selectEntityData(String entityName) {
        logger.debug("Select data for entity [{}] TyphonQL query", entityName);
        String tql = new StringBuilder(FROM).append(entityName).append(BLANK).append(entityName.toLowerCase()).append(BLANK).append(SELECT).append(entityName.toLowerCase()).toString();
        String result = getTyphonQLWebServiceClient().query(tql);
        String data = result.substring(15, result.length() - 3);
        JSONArray entities = new JSONObject(data).getJSONArray(entityName);
        System.out.println(entities);
        WorkingSet ws = new WorkingSetImpl();
        if (entities != null && !entities.isEmpty()) {
            List<EntityInstance> instances = new ArrayList<>();
            for (int index = 0; index < entities.length(); index++) {
                JSONObject entity = entities.getJSONObject(index);
                EntityInstance instance = new EntityInstance((String) entity.get("uuid"));
                Map<String, Object> fields = entity.getJSONObject("fields").toMap();
                if (fields != null && !fields.isEmpty()) {
                    for (String fieldName : fields.keySet()) {
                        Object fieldValue = fields.get(fieldName);
                        //For relations, the field value is a Map containing the key "uuid" and its value
                        if (fieldValue instanceof Map) {
                            instance.addAttribute(fieldName, "#" + ((Map) fieldValue).get("uuid"));
                        } else {
                            instance.addAttribute(fieldName, fieldValue);
                        }
                    }
                }
                instances.add(instance);
            }
            ws.addEntityRows(entityName, instances);
        }
        return ws;
    }

    @Override
    public void updateEntityNameInSourceEntityData(WorkingSet sourceEntityData, String sourceEntityName, String targetEntityName) {
        if (sourceEntityData != null) {
            sourceEntityData.addEntityRows(targetEntityName, sourceEntityData.getEntityRows(sourceEntityName));
            sourceEntityData.deleteEntityRows(sourceEntityName);
        }
    }

    @Override
    public String insertEntityData(String entityName, WorkingSet ws, EntityDO entityDO) {
        logger.debug("Insert working set data for entity [{}] TyphonQL query", entityName);
        List<EntityInstance> instances = ws.getEntityRows(entityName);
        String tql = "";
        if (instances != null && !instances.isEmpty()) {
            List<String> insertQueries = new ArrayList<>();
            for (EntityInstance instance : instances) {
                Map<String, Object> attributes = instance.getAttributes();
                insertQueries.add(attributes.entrySet().stream().map(entrySet -> entrySet.getKey() + COLON + getAttributeValueByType(entrySet, entityDO)).collect(Collectors.joining(COMMA, entityName + OPENING_CURLY_BRACE, CLOSING_CURLY_BRACE.trim())));
            }
            tql = new StringBuilder(INSERT).append(String.join(COMMA, insertQueries)).toString();
            getTyphonQLWebServiceClient().update(tql);
        }
        return tql;
    }

    private String getAttributeValueByType(Map.Entry attribute, EntityDO entityDO) {
        Map<String, DataTypeDO> attributes = entityDO.getAttributes();
        Optional<DataTypeDO> attributeDataType = attributes.keySet().stream().filter(attributeType -> attributeType.equals(attribute.getKey())).map(attributes::get).findFirst();
        if (attributeDataType.isPresent()) {
            switch (attributeDataType.get().getName()) {
                case "String":
                case "Date":
                    return "\"" + attribute.getValue() + "\"";
                case "int":
                case "Real":
                    return attribute.getValue().toString();
            }
        }
        return attribute.getValue().toString();
    }

    @Override
    public String dropEntity(String entityName) {
        logger.debug("Drop entity [{}] TyphonQL query", entityName);
        String tql = new StringBuilder(DROP).append(entityName).toString();
        getTyphonQLWebServiceClient().update(tql);
        return tql;
    }

    @Override
    public void renameEntity(String oldEntityName, String newEntityName) {
        logger.debug("Rename entity (from '{}' to '{}') TyphonQL query", oldEntityName, newEntityName);
        String tql = new StringBuilder(RENAME).append(oldEntityName).append(TO).append(newEntityName).toString();
        getTyphonQLWebServiceClient().update(tql);
    }

    @Override
    public WorkingSet readAllEntityData(String entityId) {
        String result = getTyphonQLWebServiceClient().query(new StringBuilder(FROM).append(entityId).append(BLANK).append(entityId.toLowerCase()).append(BLANK).append(SELECT).append(entityId.toLowerCase()).toString());
        //TODO
        return new WorkingSetImpl();
    }

    @Override
    public WorkingSet readEntityDataEqualAttributeValue(String sourceEntityName, String attributeName, String attributeValue) {
        String result = getTyphonQLWebServiceClient().query(new StringBuilder(FROM).append(sourceEntityName).append(BLANK).append(sourceEntityName.toLowerCase()).append(BLANK).append(SELECT).append(sourceEntityName.toLowerCase()).append(BLANK).append(WHERE).append(attributeName).append(EQUALS).append(attributeValue).toString());
        //TODO
        return new WorkingSetImpl();
    }

    @Override
    public WorkingSet readEntityDataSelectAttributes(String sourceEntityName, Set<String> attributes) {
        String result = getTyphonQLWebServiceClient().query(new StringBuilder(FROM).append(sourceEntityName).append(BLANK).append(sourceEntityName.toLowerCase()).append(BLANK).append(SELECT).toString() + attributes.stream().map(sourceEntityName.toLowerCase().concat(DOT)::concat).collect(Collectors.joining(COMMA)));
        //TODO
        return new WorkingSetImpl();
    }

    @Override
    public void deleteAllEntityData(String entityid) {
        WorkingSet workingSet = this.readAllEntityData(entityid);
        //TODO check the query
        getTyphonQLWebServiceClient().update(new StringBuilder(FROM).append(entityid).append(BLANK).append(entityid.toLowerCase()).append(BLANK).append(DELETE).append(BLANK).append(entityid.toLowerCase()).toString());
    }

    @Override
    public void writeWorkingSetData(WorkingSet workingSetData) {
        if (workingSetData != null) {
            LinkedHashMap<String, List<EntityInstance>> rows = workingSetData.getRows();
            if (rows != null && !rows.isEmpty()) {
                for (String entityName : rows.keySet()) {
                    List<EntityInstance> instances = rows.get(entityName);
                    //TODO
//                    getTyphonQLWebServiceClient().update(workingSetData);
                }
            }
        }
    }

    @Override
    public void deleteRelationshipInEntity(String relationname, String entityname) {
        logger.debug("Delete Relationship [{}] in [{}] via TyphonQL on TyphonML model", relationname, entityname);
        String tql = new StringBuilder(DROP).append(entityname).append(DOT).append(relationname).toString();
        getTyphonQLWebServiceClient().update(tql);
    }

    @Override
    public void enableContainment(String relationName, String entityname) {
        logger.debug("Enabling containment Relationship [{}] in [{}] via TyphonQL on TyphonML model", relationName, entityname);
        String tql = new StringBuilder(CHANGE).append(entityname).append(DOT).append(relationName).append(CONTAINMENT).toString();
        getTyphonQLWebServiceClient().update(tql);
    }

    @Override
    public void disableContainment(String relationName, String entityname) {
        logger.debug("Disabling containment Relationship [{}] in [{}] via TyphonQL on TyphonML model", relationName, entityname);
        String tql = new StringBuilder(CHANGE).append(entityname).append(DOT).append(relationName).append(RELATION).toString();
        getTyphonQLWebServiceClient().update(tql);
    }

    @Override
    public void changeCardinalityInRelation(String relationname, String entityname, CardinalityDO cardinality) {
        logger.debug("Change cardinality of Relationship [{}] in [{}] via TyphonQL on TyphonML model", relationname, entityname);
        String tql = new StringBuilder(CHANGE).append(entityname).append(DOT).append(relationname).append(BLANK).append(getCardinalityValue(cardinality.getValue())).toString();
        getTyphonQLWebServiceClient().update(tql);
    }

    @Override
    public void addAttribute(AttributeDO attributeDO, String entityname) {
        logger.debug("Add attribute [{}] to entity [{}]  via TyphonQL on TyphonML model", attributeDO.getName(), entityname);
        String tql = createEntityAttribute(entityname, attributeDO.getName(), attributeDO.getDataTypeDO().getName());
        getTyphonQLWebServiceClient().update(tql);
    }

    @Override
    public void renameRelation(String entityName, String relationName, String newRelationName) {
        logger.debug("Rename Relation [{}] to [{}] via TyphonQL on TyphonML model", relationName, newRelationName);
        String tql = new StringBuilder(RENAME).append(entityName).append(DOT).append(relationName).append(TO).append(newRelationName).toString();
        getTyphonQLWebServiceClient().update(tql);
    }

    @Override
    public void renameAttribute(String oldAttributeName, String newAttributeName, String entityName) {
        logger.debug("Rename attribute [from '{}' to '{}'] in entity [{}]  via TyphonQL on TyphonML model", oldAttributeName, newAttributeName, entityName);
        String tql = new StringBuilder(RENAME).append(entityName).append(DOT).append(oldAttributeName).append(TO).append(newAttributeName).toString();
        getTyphonQLWebServiceClient().update(tql);
    }

    @Override
    public void changeTypeAttribute(AttributeDO attribute, String entityName) {
        logger.debug("Change type attribute ['{}' to '{}' type] in entity [{}]  via TyphonQL on TyphonML model", attribute.getName(), attribute.getDataTypeDO().getName(), entityName);
        String tql = new StringBuilder(CHANGE).append(entityName).append(DOT).append(attribute.getName()).append(COLON).append(attribute.getDataTypeDO().getName()).toString();
        getTyphonQLWebServiceClient().update(tql);
    }

    @Override
    public void removeAttribute(String entityName, String attribute) {
        //TODO Separate deletion of data and structure.
        // Delete data
//        getTyphonQLConnection(model).delete(this.readEntityDataSelectAttributes(entityName, attribute));

        //Delete Structure
        String tql = new StringBuilder(DROP).append(entityName).append(DOT).append(attribute).toString();
        logger.debug("Delete attributes [{}] via TyphonQL DDL on TyphonML model", entityName);
        getTyphonQLWebServiceClient().update(tql);
    }

    @Override
    public void deleteWorkingSetData(WorkingSet dataToDelete) {
        //TODO
//        getTyphonQLWebServiceClient().update(dataToDelete);
    }

    @Override
    public void createRelationshipType(RelationDO relation) {
        String tql;
        logger.debug("Create relationship [{}] via TyphonQL DDL query on TyphonML model", relation.getName());
        tql = new StringBuilder(CREATE).append(relation.getSourceEntity().getName()).append(DOT).append(relation.getName()).append(RELATION).append(relation.getTargetEntity().getName()).append(getCardinalityValue(relation.getCardinality().getValue())).toString();
        getTyphonQLWebServiceClient().update(tql);
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

    private String covertMLTypeToQLType(String mlType) {
        if (mlType != null && !mlType.isEmpty()) {
            switch (mlType) {
                case "String":
                    return "str";
                case "Date":
                    return "str";
                case "int":
                    return "int";
                case "Real":
                    return "float";
            }
        }
        return "";
    }

    private TyphonQLWebServiceClient getTyphonQLWebServiceClient() {
        return typhonQLWebServiceClient;
    }

}