package com.typhon.evolutiontool.services.typhonQL;

import com.typhon.evolutiontool.client.TyphonQLWebServiceClient;
import com.typhon.evolutiontool.client.TyphonQLWebServiceClientImpl;
import com.typhon.evolutiontool.datatypes.*;
import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.entities.Collection;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import typhonml.*;

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
    private static final String COLON = ":";
    private static final String EQUALS = "=";
    private static final String OPENING_CURLY_BRACE = "{";
    private static final String CLOSING_CURLY_BRACE = "}";
    private static final String OPENING_SQUARE_BRACKET = "[";
    private static final String CLOSING_SQUARE_BRACKET = "]";
    private static final String RELATION = " -> ";
    private static final String CONTAINMENT = " :-> ";
    private static final String FROM = "from ";
    private static final String SELECT = "select ";
    private static final String DELETE = "delete ";
    private static final String WHERE = "where ";
    private static final String INSERT = "insert ";
    private static final String UPDATE = "update ";
    private static final String SET = "set ";
    private static final String CREATE = "create ";
    private static final String CREATE_INDEX = CREATE + "index ";
    private static final String RENAME = "rename ";
    private static final String RENAME_RELATION = RENAME + "relation ";
    private static final String RENAME_ATTRIBUTE = RENAME + "attribute ";
    private static final String CHANGE = "change ";
    private static final String DROP = "drop ";
    private static final String DROP_RELATION = DROP + "relation ";
    private static final String DROP_ATTRIBUTE = DROP + "attribute ";
    private static final String AT = " at ";
    private static final String TO = " to ";
    private static final String FOR = " for ";
    private static final String CARDINALITY_ZERO_ONE = "[0..1]";
    private static final String CARDINALITY_ONE_ONE = "[1..1]";
    private static final String CARDINALITY_ZERO_MANY = "[0..*]";
    private static final String CARDINALITY_ONE_MANY = "[1..*]";
    private static final String UUID_ATTRIBUTE = "@id";

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
    public Model getCurrentModel() {
        return getTyphonQLWebServiceClient().getModel(-1);
    }

    @Override
    public void createEntity(EntityDO entity, String databaseName) {
        logger.debug("Create entity, attributes and relations [{}] TyphonQL query", entity.getName());
        //Create the new entity
        createEntity(entity.getName(), databaseName);
        //Create the new entity attributes
        if (!databaseName.equals("DocumentDatabase")) {
            if (entity.getAttributes() != null && !entity.getAttributes().isEmpty()) {
                for (String attributeName : entity.getAttributes().keySet()) {
                    createEntityAttribute(entity.getName(), attributeName, entity.getAttributes().get(attributeName));
                }
            }
        }
        //Create the new entity relationships
        if (entity.getRelations() != null && !entity.getRelations().isEmpty()) {
            for (RelationDO secondEntityRelationDO : entity.getRelations()) {
                createEntityRelation(entity.getName(), secondEntityRelationDO.getName(), secondEntityRelationDO.isContainment(), secondEntityRelationDO.getTypeName(), secondEntityRelationDO.getCardinality());
            }
        }
    }

    @Override
    public String createEntity(String entityName, String databaseName) {
        logger.debug("Create entity [{}] TyphonQL query", entityName);
        String tql = new StringBuilder(CREATE).append(entityName).append(AT).append(databaseName).toString();
        getTyphonQLWebServiceClient().update(tql);
        return tql;
    }

    @Override
    public String createEntityAttribute(String entityName, String attributeName, DataTypeDO dataType) {
        logger.debug("Create attribute [{}: {}] for entity [{}] TyphonQL query", attributeName, dataType, entityName);
        String tql = new StringBuilder(CREATE).append(entityName).append(DOT).append(attributeName).append(COLON).append(covertMLTypeToQLType(dataType)).toString();
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
    public WorkingSet selectEntityData(String entityName, Set<String> attributesToSelect, List<String> relationsToSelect, String attributeToFilterOn, String attributeToFilterOnValue) {
        logger.debug("Select data for entity [{}] TyphonQL query", entityName);
        StringBuilder query = new StringBuilder(FROM).append(entityName).append(BLANK).append(entityName.toLowerCase()).append(BLANK).append(SELECT).append(entityName.toLowerCase()).append(DOT).append(UUID_ATTRIBUTE);
        if (attributesToSelect != null && !attributesToSelect.isEmpty()) {
            query.append(attributesToSelect.stream().map(attribute -> entityName.toLowerCase() + DOT + attribute).collect(Collectors.joining(COMMA, COMMA, "")));
        }
        if (relationsToSelect != null && !relationsToSelect.isEmpty()) {
            query.append(relationsToSelect.stream().map(relation -> entityName.toLowerCase() + DOT + relation).collect(Collectors.joining(COMMA, COMMA, "")));
        }
        if (attributeToFilterOn != null && attributeToFilterOnValue != null) {
            query.append(BLANK).append(WHERE).append(entityName.toLowerCase()).append(DOT).append(attributeToFilterOn).append(EQUALS).append("\"").append(attributeToFilterOnValue).append("\"");
        }
        String tql = query.toString();
        JSONObject result = new JSONObject(getTyphonQLWebServiceClient().query(tql));
        logger.info("Result: {}", result);
        JSONArray attributes = result.getJSONArray("columnNames");
        JSONArray attributesValues = result.getJSONArray("values");
        logger.info("Selected attributes: {}", attributes);
        logger.info("Selected entities values: {}", attributesValues);
        WorkingSet ws = new WorkingSetImpl();
        if (attributesValues != null && !attributesValues.isEmpty()) {
            List<EntityInstance> instances = new ArrayList<>();
            for (int index = 0; index < attributesValues.length(); index++) {
                JSONArray entity = (JSONArray) attributesValues.get(index);
                EntityInstance instance = new EntityInstance((String) entity.get(0));
                for (int attributeIndex = 1; attributeIndex < entity.length(); attributeIndex++) {
                    String attributeName = (String) attributes.get(attributeIndex);
                    attributeName = attributeName.substring(attributeName.indexOf(".") + 1);
                    Object attributeValue = entity.get(attributeIndex);
                    //For relations, the field value is a Map containing the key "uuid" and its value
                    if (attributeValue instanceof Map) {
                        instance.addAttribute(attributeName, "#" + ((Map) attributeValue).get("uuid"));
                    } else {
                        instance.addAttribute(attributeName, attributeValue);
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
    public void removeUselessAttributesInSourceEntityData(WorkingSet entityData, String entityName, Set<String> attributesToKeep) {
        if (entityData != null && attributesToKeep != null && !attributesToKeep.isEmpty()) {
            List<EntityInstance> instances = entityData.getEntityRows(entityName);
            if (instances != null && !instances.isEmpty()) {
                for (EntityInstance instance : instances) {
                    Map<String, Object> attributes = instance.getAttributes();
                    Map<String, Object> keptAttributes = new HashMap<>();
                    for (String attributeName : attributes.keySet()) {
                        if (attributesToKeep.contains(attributeName)) {
                            keptAttributes.put(attributeName, attributes.get(attributeName));
                        }
                    }
                    instance.setAttributes(keptAttributes);
                }
            }
        }
    }

    @Override
    public String insertEntityData(String entityName, WorkingSet ws, EntityDO entityDO) {
        logger.debug("Insert working set data for entity [{}] TyphonQL query", entityName);
        List<EntityInstance> instances = ws.getEntityRows(entityName);
        String tql = "";
        if (instances != null && !instances.isEmpty()) {
            Map<String, DataTypeDO> attributes = entityDO.getAttributes();
            Set<String> attributesNames = attributes.keySet();
            List<String> attributesTypes = getQLAttributesTypes(attributes);
            StringBuilder query = new StringBuilder("\"query\"").append(COLON).append("\"").append(INSERT).append(entityName).append(OPENING_CURLY_BRACE).append(attributesNames.stream().map(name -> name + COLON.trim() + "??" + name).collect(Collectors.joining(COMMA))).append(CLOSING_CURLY_BRACE).append("\"").append(COMMA);
            StringBuilder parameterNames = new StringBuilder("\"parameterNames\"").append(COLON).append(OPENING_SQUARE_BRACKET).append(attributesNames.stream().map(name -> "\"" + name + "\"").collect(Collectors.joining(COMMA))).append(CLOSING_SQUARE_BRACKET).append(COMMA);
            StringBuilder parameterTypes = new StringBuilder("\"parameterTypes\"").append(COLON).append(OPENING_SQUARE_BRACKET).append(attributesTypes.stream().map(type -> "\"" + type + "\"").collect(Collectors.joining(COMMA))).append(CLOSING_SQUARE_BRACKET).append(COMMA);
            StringBuilder boundRows = new StringBuilder("\"boundRows\"").append(COLON).append(instances.stream().map(inst -> inst.getAttributes().values().stream().map(value -> "\"" + value + "\"").collect(Collectors.joining(COMMA, OPENING_SQUARE_BRACKET, CLOSING_SQUARE_BRACKET))).collect(Collectors.joining(COMMA, OPENING_SQUARE_BRACKET, CLOSING_SQUARE_BRACKET)));
            logger.info("Query: {}", query.toString());
            logger.info("Parameter names: {}", parameterNames.toString());
            logger.info("Parameter types: {}", parameterTypes.toString());
            logger.info("Rows: {}", boundRows.toString());
            //One single insert statement code (not working anymore and worse performance):
//            List<String> insertQueries = new ArrayList<>();
//            for (EntityInstance instance : instances) {
//                Map<String, Object> attributes = instance.getAttributes();
//                insertQueries.add(attributes.entrySet().stream().map(entrySet -> entrySet.getKey() + COLON + getAttributeValueByType(entrySet, entityDO)).collect(Collectors.joining(COMMA, entityName + OPENING_CURLY_BRACE, CLOSING_CURLY_BRACE.trim())));
//            }
            tql = new StringBuilder(OPENING_CURLY_BRACE).append(query.toString()).append(parameterNames.toString()).append(parameterTypes.toString()).append(boundRows.toString()).append(CLOSING_CURLY_BRACE).toString();
            logger.info("QL query: {}", tql);
            getTyphonQLWebServiceClient().update(tql);
        }
        return tql;
    }

    private List<String> getQLAttributesTypes(Map<String, DataTypeDO> attributes) {
        List<String> attributesTypes = new ArrayList<>();
        if (attributes != null && !attributes.isEmpty()) {
            for (String attributeName: attributes.keySet()) {
                DataTypeDO attributeType = attributes.get(attributeName);
                if (attributeType instanceof IntTypeDO) { attributesTypes.add("int"); continue; }
                if (attributeType instanceof BigIntTypeDO) { attributesTypes.add("bigint"); continue; }
                if (attributeType instanceof FloatTypeDO) { attributesTypes.add("float"); continue; }
                if (attributeType instanceof StringTypeDO) { attributesTypes.add("string"); continue; }
                if (attributeType instanceof BoolTypeDO) { attributesTypes.add("bool"); continue; }
                if (attributeType instanceof TextTypeDO) { attributesTypes.add("text"); continue; }
                if (attributeType instanceof DateTypeDO) { attributesTypes.add("date"); continue; }
                if (attributeType instanceof DatetimeTypeDO) { attributesTypes.add("datetime"); continue; }
                if (attributeType instanceof PointTypeDO) { attributesTypes.add("point"); continue; }
                if (attributeType instanceof PolygonTypeDO) { attributesTypes.add("polygon"); continue; }
                if (attributeType instanceof BlobTypeDO) { attributesTypes.add("blob"); }
            }
        }
        return attributesTypes;
    }

//    insert Products_migrated { Discontinued : "0",UnitPrice : 18.0,ProductName : "Chai",QuantityPerUnit : "10 boxes x 20 bags",UnitsOnOrder : 0,ProductId : 0,ReorderLevel : 10,CategoriesID : 1,UnitsInStock : 39,SuppliersID : 1},
//    Products_migrated { Discontinued : "0",UnitPrice : 31.0,ProductName : "Ikura",QuantityPerUnit : "12 - 200 ml jars",UnitsOnOrder : 0,ProductId : 0,ReorderLevel : 0,CategoriesID : 8,UnitsInStock : 31,SuppliersID : 4},
//    Products_migrated { Discontinued : "0",UnitPrice : 21.0,ProductName : "Queso Cabrales",QuantityPerUnit : "1 kg pkg.",UnitsOnOrder : 30,ProductId : 0,ReorderLevel : 30,CategoriesID : 4,UnitsInStock : 22,SuppliersID : 5},
//    Products_migrated { Discontinued : "0",UnitPrice : 38.0,ProductName : "Queso Manchego La Pastora",QuantityPerUnit : "10 - 500 g pkgs.",UnitsOnOrder : 0,ProductId : 0,ReorderLevel : 0,CategoriesID : 4,UnitsInStock : 86,SuppliersID : 5},
//    Products_migrated { Discontinued : "0",UnitPrice : 6.0,ProductName : "Konbu",QuantityPerUnit : "2 kg box",UnitsOnOrder : 0,ProductId : 0,ReorderLevel : 5,CategoriesID : 8,UnitsInStock : 24,SuppliersID : 6}

//    {
//    "query":"insert Products{ProductId:??ProductId,ProductName:??ProductName,QuantityPerUnit:??QuantityPerUnit,UnitPrice:??UnitPrice,UnitsInStock:??UnitsInStock,UnitsOnOrder:??UnitsOnOrder,ReorderLevel:??ReorderLevel,Discontinued:??Discontinued,CategoriesID:??CategoriesID,SuppliersID:??SuppliersID}",
//    "parameterNames":["ProductId", "ProductName","SuppliersID","CategoriesID", "QuantityPerUnit", "UnitPrice", "UnitsInStock", "UnitsOnOrder", "ReorderLevel", "Discontinued"],
//    "parameterTypes": ["int","string","int","int","string","float","int","int","int","string"],
//    "boundRows":[["1","Chai","1","1","10 boxes x 20 bags","18.0000","39","0","10","0"],["2","Chang","1","1","24 - 12 oz bottles","19.0000","17","40","25","0"]
//    }

    @Override
    public void deleteEntityData(String entityName, String attributeName, String attributeValue) {
        logger.debug("Delete entity data [{}] (where '{}' == '{}') TyphonQL query", entityName, attributeName, attributeValue);
        StringBuilder query = new StringBuilder(DELETE).append(entityName).append(BLANK).append(entityName.toLowerCase());
        if (attributeName != null && attributeValue != null) {
            query.append(BLANK).append(WHERE).append(entityName.toLowerCase()).append(DOT).append(attributeName).append(EQUALS).append("\"").append(attributeValue).append("\"");
        }
        String tql = query.toString();
        getTyphonQLWebServiceClient().update(tql);
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
    public void deleteRelationshipInEntity(String relationname, String entityname) {
        logger.debug("Delete Relationship [{}] in [{}] via TyphonQL on TyphonML model", relationname, entityname);
        String tql = new StringBuilder(DROP_RELATION).append(entityname).append(DOT).append(relationname).toString();
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
        String tql = createEntityAttribute(entityname, attributeDO.getName(), attributeDO.getDataTypeDO());
        getTyphonQLWebServiceClient().update(tql);
    }

    @Override
    public void addAttribute(String attributeName, String entityname, DataTypeDO dataType) {
        logger.debug("Add attribute [{}] to entity [{}]  via TyphonQL on TyphonML model", attributeName, entityname);
        String tql = createEntityAttribute(entityname, attributeName, dataType);
        getTyphonQLWebServiceClient().update(tql);
    }

    @Override
    public void renameRelation(String entityName, String relationName, String newRelationName) {
        logger.debug("Rename Relation [{}] to [{}] via TyphonQL on TyphonML model", relationName, newRelationName);
        String tql = new StringBuilder(RENAME_RELATION).append(entityName).append(DOT).append(relationName).append(TO).append(newRelationName).toString();
        getTyphonQLWebServiceClient().update(tql);
    }

    @Override
    public void renameAttribute(String oldAttributeName, String newAttributeName, String entityName) {
        logger.debug("Rename attribute [from '{}' to '{}'] in entity [{}]  via TyphonQL on TyphonML model", oldAttributeName, newAttributeName, entityName);
        String tql = new StringBuilder(RENAME_ATTRIBUTE).append(entityName).append(DOT).append(oldAttributeName).append(TO).append(newAttributeName).toString();
        getTyphonQLWebServiceClient().update(tql);
    }

    @Override
    public void changeTypeAttribute(String attributeName, DataTypeDO attributeType, String entityName) {
        logger.debug("Change type of attribute '{}' to '{}' type] in entity [{}]  via TyphonQL on TyphonML model", attributeName, covertMLTypeToQLType(attributeType), entityName);
        String tql = new StringBuilder(CHANGE).append(entityName).append(DOT).append(attributeName).append(COLON).append(covertMLTypeToQLType(attributeType)).toString();
        getTyphonQLWebServiceClient().update(tql);
    }

    @Override
    public void updateEntityData(String entityName, WorkingSet firstWs, WorkingSet secondWs, EntityDO secondEntity, String relationName, Boolean firstOrSecondEntityRelation) {
        logger.debug("Update '{}' entity data through '{}' relation", entityName, relationName);
        List<EntityInstance> entityInstances = firstWs.getEntityRows(entityName);
        List<EntityInstance> secondEntityInstances = secondWs.getEntityRows(entityName);
        String tql = "";
        if (firstOrSecondEntityRelation) {
            if (entityInstances != null && !entityInstances.isEmpty()) {
                for (EntityInstance instance : entityInstances) {
                    String relationValue = (String) instance.getAttribute(relationName);
                    for (EntityInstance secondInstance : secondEntityInstances) {
                        if (secondInstance.getUuid().equals(relationValue)) {
                            EntityInstance referencedInstance = secondInstance;
                                String updateQuery = referencedInstance.getAttributes().entrySet().stream()
                                        .filter(attribute -> !attribute.getKey().equals(relationName))
                                        .map(entrySet -> entrySet.getKey() + COLON + getAttributeValueByType(entrySet, secondEntity)).collect(Collectors.joining(COMMA, OPENING_CURLY_BRACE, CLOSING_CURLY_BRACE.trim()));
                                tql = new StringBuilder(UPDATE).append(entityName).append(BLANK).append(entityName.toLowerCase()).append(BLANK)
                                        .append(WHERE).append(entityName.toLowerCase()).append(DOT).append(UUID_ATTRIBUTE).append(EQUALS).append("#").append(instance.getUuid()).append(BLANK)
                                        .append(SET).append(updateQuery).toString();
                            getTyphonQLWebServiceClient().update(tql);
                            break;
                        }
                    }
                }
            }
        } else {
            if (secondEntityInstances != null && !secondEntityInstances.isEmpty()) {
                for (EntityInstance instance : secondEntityInstances) {
                    String relationValue = (String) instance.getAttribute(relationName);
                    for (EntityInstance entityInstance : entityInstances) {
                        if (entityInstance.getUuid().equals(relationValue)) {
                            EntityInstance referencedInstance = entityInstance;
                            String updateQuery = instance.getAttributes().entrySet().stream()
                                    .filter(attribute -> !attribute.getKey().equals(relationName))
                                    .map(entrySet -> entrySet.getKey() + COLON + getAttributeValueByType(entrySet, secondEntity)).collect(Collectors.joining(COMMA, OPENING_CURLY_BRACE, CLOSING_CURLY_BRACE.trim()));
                            tql = new StringBuilder(UPDATE).append(entityName).append(BLANK).append(entityName.toLowerCase()).append(BLANK)
                                    .append(WHERE).append(entityName.toLowerCase()).append(DOT).append(UUID_ATTRIBUTE).append(EQUALS).append("#").append(referencedInstance.getUuid()).append(BLANK)
                                    .append(SET).append(updateQuery).toString();
                            getTyphonQLWebServiceClient().update(tql);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void removeAttribute(String entityName, String attribute) {
        String tql = new StringBuilder(DROP_ATTRIBUTE).append(entityName).append(DOT).append(attribute).toString();
        logger.debug("Delete attributes [{}] via TyphonQL DDL on TyphonML model", entityName);
        getTyphonQLWebServiceClient().update(tql);
    }

    @Override
    public void createRelationshipType(RelationDO relation) {
        String tql;
        logger.debug("Create relationship [{}] via TyphonQL DDL query on TyphonML model", relation.getName());
        tql = new StringBuilder(CREATE).append(relation.getSourceEntity().getName()).append(DOT).append(relation.getName()).append(RELATION).append(relation.getTargetEntity().getName()).append(getCardinalityValue(relation.getCardinality().getValue())).toString();
        getTyphonQLWebServiceClient().update(tql);
    }

    @Override
    public void addTableIndex(String entityName, Set<String> attributesNames) {
        logger.debug("Add/update table index for entity '{}' and attributes: {}", attributesNames, entityName);
        String tql = new StringBuilder(CREATE_INDEX).append(entityName).append("Index").append(FOR).append(entityName).append(DOT).append(OPENING_CURLY_BRACE).append(String.join(COMMA, attributesNames)).append(CLOSING_CURLY_BRACE).toString();
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

    private String covertMLTypeToQLType(DataTypeDO dataType) {
        if (dataType != null) {
            if (dataType instanceof BigIntTypeDO) {
                return "bigint";
            }
            if (dataType instanceof BlobTypeDO) {
                return "blob";
            }
            if (dataType instanceof BoolTypeDO) {
                return "bool";
            }
            if (dataType instanceof DatetimeTypeDO) {
                return "datetime";
            }
            if (dataType instanceof DateTypeDO) {
                return "date";
            }
            if (dataType instanceof FloatTypeDO) {
                return "float";
            }
            if (dataType instanceof FreetextTypeDO) {
                return "freetext[" + ((FreetextTypeDO) dataType).getTasks() + "]";
            }
            if (dataType instanceof IntTypeDO) {
                return "int";
            }
            if (dataType instanceof PointTypeDO) {
                return "point";
            }
            if (dataType instanceof PolygonTypeDO) {
                return "polygon";
            }
            if (dataType instanceof StringTypeDO) {
                return "string(" + ((StringTypeDO) dataType).getMaxSize() + ")";
            }
            if (dataType instanceof TextTypeDO) {
                return "text";
            }
        }
        return null;
    }

    private String getAttributeValueByType(Map.Entry attribute, EntityDO entityDO) {
        Map<String, DataTypeDO> attributes = entityDO.getAttributes();
        Optional<DataTypeDO> attributeDataType = attributes.keySet().stream().filter(attributeType -> attributeType.equals(attribute.getKey())).map(attributes::get).findFirst();
        if (attributeDataType.isPresent()) {
            if (attributeDataType.get() instanceof IntTypeDO || attributeDataType.get() instanceof BigIntTypeDO || attributeDataType.get() instanceof FloatTypeDO) {
                return attribute.getValue().toString();
            }
            if (attributeDataType.get() instanceof StringTypeDO || attributeDataType.get() instanceof TextTypeDO || attributeDataType.get() instanceof FreetextTypeDO) {
                return "\"" + attribute.getValue() + "\"";
            }
            //TODO: check how to return the value according to the following DataType instance classes
            if (attributeDataType.get() instanceof BlobTypeDO) {
                return attribute.getValue().toString();
            }
            if (attributeDataType.get() instanceof BoolTypeDO) {
                return attribute.getValue().toString();
            }
            if (attributeDataType.get() instanceof DatetimeTypeDO) {
                return "\"" + attribute.getValue() + "\"";
            }
            if (attributeDataType.get() instanceof DateTypeDO) {
                return "\"" + attribute.getValue() + "\"";
            }
            if (attributeDataType.get() instanceof PointTypeDO) {
                return attribute.getValue().toString();
            }
            if (attributeDataType.get() instanceof PolygonTypeDO) {
                return attribute.getValue().toString();
            }
        }
        return attribute.getValue().toString();
    }

    private TyphonQLWebServiceClient getTyphonQLWebServiceClient() {
        return typhonQLWebServiceClient;
    }

}
