package com.typhon.evolutiontool.services.typhonQL;

import com.google.common.collect.Sets;
import com.typhon.evolutiontool.client.TyphonQLWebServiceClient;
import com.typhon.evolutiontool.client.TyphonQLWebServiceClientImpl;
import com.typhon.evolutiontool.datatypes.*;
import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import com.typhon.evolutiontool.utils.UUIDGenerator;
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
    private static final String COLON = ":";
    private static final String EQUALS = "=";
    private static final String DOUBLE_EQUALS = "==";
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
    private static final String UUID_TYPE = "uuid";
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
//                    createEntityAttribute(entity.getName(), attributeName, entity.getAttributes().get(attributeName));
                    addAttribute(attributeName, entity.getName(), entity.getAttributes().get(attributeName));
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
        logger.debug("Result: {}", result);
        JSONArray attributes = result.getJSONArray("columnNames");
        JSONArray attributesValues = result.getJSONArray("values");
        logger.debug("Selected attributes: {}", attributes);
        logger.debug("Selected entities values: {}", attributesValues);
        WorkingSet ws = new WorkingSetImpl();
        if (attributesValues != null && !attributesValues.isEmpty()) {
            LinkedList<EntityInstance> instances = new LinkedList<>();
            for (int index = 0; index < attributesValues.length(); index++) {
                JSONArray entity = (JSONArray) attributesValues.get(index);
                EntityInstance instance = new EntityInstance((String) entity.get(0));
                for (int attributeIndex = 1; attributeIndex < entity.length(); attributeIndex++) {
                    String attributeName = (String) attributes.get(attributeIndex);
                    attributeName = attributeName.substring(attributeName.indexOf(".") + 1);
                    Object attributeValue = entity.get(attributeIndex);
                    //For relations, the field value is a Map containing the key "uuid" and its value
                    if (attributeValue instanceof Map) {
                        instance.addAttribute(attributeName, "#" + ((Map) attributeValue).get(UUID_TYPE));
                    } else {
                        if (attributeValue instanceof String) {
                            attributeValue = ((String) attributeValue).replaceAll("\r\n", "\\\\n").replaceAll("\r|\n","\\\\n");
                        }
                        instance.addAttribute(attributeName, attributeValue);
                    }
                }
                instances.add(instance);
            }
            ws.addEntityRows(entityName, instances);
            logger.info("# of selected instances for entity '{}': {}", entityName, instances.size());
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
            Set<String> relationsNames = entityDO.getRelations().stream().map(RelationDO::getName).collect(Collectors.toSet());
            StringBuilder query = new StringBuilder("\"query\"").append(COLON).append("\"").append(INSERT).append(entityName).append(OPENING_CURLY_BRACE)
                    .append(UUID_ATTRIBUTE).append(COLON.trim()).append("??").append(UUID_TYPE.toUpperCase()).append(COMMA)
                    .append(attributesNames.stream().map(name -> name + COLON.trim() + "??" + name).collect(Collectors.joining(COMMA)))
//                    .append(!relationsNames.isEmpty() ? "," : "")
//                    .append(!relationsNames.isEmpty() ? relationsNames.stream().map(name -> name + COLON.trim() + "??" + name).collect(Collectors.joining(COMMA)) : "")
                    .append(CLOSING_CURLY_BRACE).append("\"").append(COMMA);
            StringBuilder parameterNames = new StringBuilder("\"parameterNames\"").append(COLON).append(OPENING_SQUARE_BRACKET)
                    .append("\"").append(UUID_TYPE.toUpperCase()).append("\"").append(COMMA)
                    .append(attributesNames.stream().map(name -> "\"" + name + "\"").collect(Collectors.joining(COMMA)))
//                    .append(!relationsNames.isEmpty() ? "," : "")
//                    .append(!relationsNames.isEmpty() ? relationsNames.stream().map(name -> "\"" + name + "\"").collect(Collectors.joining(COMMA)) : "")
                    .append(CLOSING_SQUARE_BRACKET).append(COMMA);
            StringBuilder parameterTypes = new StringBuilder("\"parameterTypes\"").append(COLON).append(OPENING_SQUARE_BRACKET)
                    .append("\"").append(UUID_TYPE).append("\"").append(COMMA)
                    .append(attributesTypes.stream().map(type -> "\"" + type + "\"").collect(Collectors.joining(COMMA)))
//                    .append(!relationsNames.isEmpty() ? "," : "")
//                    .append(!relationsNames.isEmpty() ? relationTypes.stream().map(type -> "\"" + type + "\"").collect(Collectors.joining(COMMA)) : "")
                    .append(CLOSING_SQUARE_BRACKET).append(COMMA);
            Set<EntityInstance> orderedInstances = orderInstancesByAttributesAndRelationsNames(entityName, instances, attributesNames, relationsNames);
            StringBuilder boundRows = new StringBuilder("\"boundRows\"").append(COLON)
                    .append(orderedInstances.stream().map(inst -> inst.getAttributes().values().stream().map(value -> value.toString().equals("null") ? null : "\"" + value + "\"").collect(Collectors.joining(COMMA, OPENING_SQUARE_BRACKET, CLOSING_SQUARE_BRACKET))).collect(Collectors.joining(COMMA, OPENING_SQUARE_BRACKET, CLOSING_SQUARE_BRACKET)));
            logger.debug("Query: {}", query.toString());
            logger.debug("Parameter names: {}", parameterNames.toString());
            logger.debug("Parameter types: {}", parameterTypes.toString());
            logger.debug("Rows: {}", boundRows.toString());
            //One single insert statement code (not working anymore and worse performance):
//            List<String> insertQueries = new ArrayList<>();
//            for (EntityInstance instance : instances) {
//                Map<String, Object> attributes = instance.getAttributes();
//                insertQueries.add(attributes.entrySet().stream().map(entrySet -> entrySet.getKey() + COLON + getAttributeValueByType(entrySet, entityDO)).collect(Collectors.joining(COMMA, entityName + OPENING_CURLY_BRACE, CLOSING_CURLY_BRACE.trim())));
//            }
            logger.info("# of inserted instances for entity '{}': {}", entityName, instances.size());
            tql = OPENING_CURLY_BRACE + query.toString() + parameterNames.toString() + parameterTypes.toString() + boundRows.toString() + CLOSING_CURLY_BRACE;
            logger.debug("QL insert query: {}", tql);
            getTyphonQLWebServiceClient().update(tql);

            if (!orderedInstances.isEmpty()) {
                for (RelationDO relationDO : entityDO.getRelations()) {
                    String relationName = relationDO.getName();
                    String updateTql;
                    switch (relationDO.getCardinality().getValue()) {
                        case 0:
                        case 1: updateTql = "{\"query\":\"" + UPDATE + entityName + " x where x.@id == ??UUID set {" + relationName + ": ??UUID2}\",\"parameterNames\":[\"UUID\", \"UUID2\"],\"parameterTypes\":[\"uuid\", \"uuid\"],\"boundRows\":"; break;
                        case 2:
                        case 3:
                        default: updateTql = "{\"query\":\"" + UPDATE + entityName + " x where x.@id == ??UUID set {" + relationName + "+: [??UUID2]}\",\"parameterNames\":[\"UUID\", \"UUID2\"],\"parameterTypes\":[\"uuid\", \"uuid\"],\"boundRows\":"; break;
                    }
//                    boundRows\":[[\"018ca4a2-956a-3af8-bb52-4d6fb4095512\", \"3a1b6956-801f-3856-8324-cbb2e1c73571\"]";
                    List<String> totalBoundRows = new ArrayList<>();
                    boolean empty = true;
                    for (EntityInstance instance : orderedInstances) {
                        String instanceUuid = (String) instance.getAttributes().get(UUID_TYPE);
                        Set<String> uuids = instance.getRelations().get(relationName);
                        //[[instanceUUid, uuid1], [instanceUUid, uuid2]]
                        if (uuids != null) {
                            String currentBoundRows = uuids.stream().map(uuid -> OPENING_SQUARE_BRACKET + "\"" + instanceUuid + "\"" + COMMA + "\"" + uuid + "\"" + CLOSING_SQUARE_BRACKET).collect(Collectors.joining(COMMA));
                            totalBoundRows.add(currentBoundRows);
                            if (!uuids.isEmpty()) {
                                empty = false;
                            }
                        }
                    }
                    String finalBoundRows = totalBoundRows.stream().collect(Collectors.joining(COMMA, OPENING_SQUARE_BRACKET, CLOSING_SQUARE_BRACKET));
                    updateTql = updateTql + finalBoundRows + CLOSING_CURLY_BRACE;
                    logger.info("# of updated instances for relation '{}': {}", relationName, totalBoundRows.size());
                    if (!empty) {
                        getTyphonQLWebServiceClient().update(updateTql);
                    }
                }
            }
        }
        return tql;
    }

    /**
     * Orders the entity instances attributes and relations according to the ordered list of the parameters and relations names from the insert query
     *
     * @param entityName      the name of the entity containing the attributes and the relations
     * @param instances       the entity instances to order
     * @param attributesNames the ordered set of parameters names
     * @param relationsNames  the ordered set of relations names
     * @return the ordered entity instances list
     */
    private Set<EntityInstance> orderInstancesByAttributesAndRelationsNames(String entityName, List<EntityInstance> instances, Set<String> attributesNames, Set<String> relationsNames) {
        Set<EntityInstance> orderedInstances = new HashSet<>();
        Set<String> uuids = new HashSet<>();
        if (instances != null && !instances.isEmpty()) {
            for (EntityInstance instance : instances) {
                String newUuid = UUIDGenerator.buildUUID(entityName, Collections.singletonList(instance.getUuid()));
                if (!uuids.contains(newUuid)) {
                    uuids.add(newUuid);
                    EntityInstance orderedInstance = new EntityInstance(newUuid);
                    Map<String, Object> instanceAttributes = instance.getAttributes();
                    LinkedHashMap<String, Object> orderedAttributes = new LinkedHashMap<>();
                    orderedAttributes.put(UUID_TYPE, newUuid);
                    LinkedHashMap<String, Set<String>> orderedRelations = new LinkedHashMap<>();
                    if (instanceAttributes != null && !instanceAttributes.isEmpty()) {
                        if (attributesNames != null && !attributesNames.isEmpty()) {
                            for (String attributeName : attributesNames) {
                                orderedAttributes.put(attributeName, instanceAttributes.get(attributeName));
                            }
                        }
                        if (relationsNames != null && !relationsNames.isEmpty()) {
                            for (String relationsName : relationsNames) {
                                String value = instanceAttributes.get(relationsName).toString();
                                if (!value.equals("null")) {
                                    orderedRelations.put(relationsName, Sets.newHashSet(instanceAttributes.get(relationsName).toString()));
                                }
                            }
                        }
                    }
                    orderedInstance.setAttributes(orderedAttributes);
                    orderedInstance.setRelations(orderedRelations);
                    orderedInstances.add(orderedInstance);
                } else {
                    for (EntityInstance existingInstance : orderedInstances) {
                        if (existingInstance.getUuid().equals(newUuid)) {
                            Map<String, Object> instanceAttributes = instance.getAttributes();
                            if (instanceAttributes != null && !instanceAttributes.isEmpty()) {
                                if (relationsNames != null && !relationsNames.isEmpty()) {
                                    for (String relationsName : relationsNames) {
                                        Map<String, Set<String>> existingRelations = existingInstance.getRelations();
                                        Set<String> relations = existingRelations.get(relationsName);
                                        String value = instanceAttributes.get(relationsName).toString();
                                        if (!value.equals("null")) {
                                            if (relations == null) {
                                                relations = Sets.newHashSet();
                                                existingRelations.put(relationsName, relations);
                                            }
                                            relations.add(value);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return orderedInstances;
    }

    private List<String> getQLAttributesTypes(Map<String, DataTypeDO> attributes) {
        List<String> attributesTypes = new ArrayList<>();
        if (attributes != null && !attributes.isEmpty()) {
            for (String attributeName : attributes.keySet()) {
                DataTypeDO attributeType = attributes.get(attributeName);
                if (attributeType instanceof IntTypeDO) {
                    attributesTypes.add("int");
                    continue;
                }
                if (attributeType instanceof BigIntTypeDO) {
                    attributesTypes.add("bigint");
                    continue;
                }
                if (attributeType instanceof FloatTypeDO) {
                    attributesTypes.add("float");
                    continue;
                }
                if (attributeType instanceof StringTypeDO) {
                    attributesTypes.add("string");
                    continue;
                }
                if (attributeType instanceof BoolTypeDO) {
                    attributesTypes.add("bool");
                    continue;
                }
                if (attributeType instanceof TextTypeDO) {
                    attributesTypes.add("text");
                    continue;
                }
                if (attributeType instanceof DateTypeDO) {
                    attributesTypes.add("date");
                    continue;
                }
                if (attributeType instanceof DatetimeTypeDO) {
                    attributesTypes.add("datetime");
                    continue;
                }
                if (attributeType instanceof PointTypeDO) {
                    attributesTypes.add("point");
                    continue;
                }
                if (attributeType instanceof PolygonTypeDO) {
                    attributesTypes.add("polygon");
                    continue;
                }
                if (attributeType instanceof BlobTypeDO) {
                    attributesTypes.add("blob");
                }
            }
        }
        return attributesTypes;
    }

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
                boolean areParametersInitialised = false;
                List<String> boundRows = new ArrayList<>();
                String query = "";
                String parameterNames = "";
                String parameterTypes = "";
                for (EntityInstance instance : entityInstances) {
                    String relationValue = (String) instance.getAttribute(relationName);
                    for (EntityInstance secondInstance : secondEntityInstances) {
                        if (!areParametersInitialised) {
                            List<String> attributesTypes = getQLAttributesTypes(secondEntity.getAttributes());
                            query = "\"query\":\"" + UPDATE + entityName + BLANK + entityName.toLowerCase() + BLANK + WHERE + entityName.toLowerCase() + DOT + UUID_ATTRIBUTE + DOUBLE_EQUALS + "??UUID" + BLANK
                                    + SET + OPENING_CURLY_BRACE
                                    + secondInstance.getAttributes().keySet().stream().map(o -> o + ":" + " ??" + o).collect(Collectors.joining(COMMA))
                                    + CLOSING_CURLY_BRACE + "\"" + COMMA;
                            parameterNames = "\"parameterNames\":" + OPENING_SQUARE_BRACKET + "\"UUID\"" + secondInstance.getAttributes().keySet().stream().map(o -> "\"" + o + "\"").collect(Collectors.joining(COMMA, COMMA, "")) + CLOSING_SQUARE_BRACKET + COMMA;
                            parameterTypes = "\"parameterTypes\":" + OPENING_SQUARE_BRACKET + "\"uuid\"" + attributesTypes.stream().map(type -> "\"" + type + "\"").collect(Collectors.joining(COMMA, COMMA, "")) + CLOSING_SQUARE_BRACKET + COMMA;
                            areParametersInitialised = true;
                        }
                        if (secondInstance.getUuid().equals(relationValue)) {
                            boundRows.add("[" + "\"" + instance.getUuid() + "\"" + secondInstance.getAttributes().keySet().stream().map(key -> secondInstance.getAttributes().get(key).toString().equals("null") ? null : ("\"" + secondInstance.getAttributes().get(key)) + "\"").collect(Collectors.joining(COMMA, COMMA, "")) + "]");
                            break;
                            //Old code, inserting without parametrized queries.
//                            EntityInstance referencedInstance = secondInstance;
//                            String updateQuery = referencedInstance.getAttributes().entrySet().stream()
//                                    .filter(attribute -> !attribute.getKey().equals(relationName))
//                                    .map(entrySet -> entrySet.getKey() + COLON + getAttributeValueByType(entrySet, secondEntity)).collect(Collectors.joining(COMMA, OPENING_CURLY_BRACE, CLOSING_CURLY_BRACE.trim()));
//                            tql = new StringBuilder(UPDATE).append(entityName).append(BLANK).append(entityName.toLowerCase()).append(BLANK)
//                                    .append(WHERE).append(entityName.toLowerCase()).append(DOT).append(UUID_ATTRIBUTE).append(EQUALS).append("#").append(instance.getUuid()).append(BLANK)
//                                    .append(SET).append(updateQuery).toString();
//                            getTyphonQLWebServiceClient().update(tql);
//                            break;
                        }
                    }
                }
                logger.info("# of updated instances for entity '{}': {}", entityName, boundRows.size());
                String completeBoundRows = "\"boundRows\":" + boundRows.stream().collect(Collectors.joining(COMMA, OPENING_SQUARE_BRACKET, CLOSING_SQUARE_BRACKET));
                tql = OPENING_CURLY_BRACE + query + parameterNames + parameterTypes + completeBoundRows + CLOSING_CURLY_BRACE;
                getTyphonQLWebServiceClient().update(tql);
            }
        } else {
            //TODO adapt for the inversed relation
            if (secondEntityInstances != null && !secondEntityInstances.isEmpty()) {
                for (EntityInstance instance : secondEntityInstances) {
                    String relationValue = (String) instance.getAttribute(relationName);
                    for (EntityInstance entityInstance : entityInstances) {
                        if (entityInstance.getUuid().equals(relationValue)) {
                            List<String> attributesTypes = getQLAttributesTypes(secondEntity.getAttributes());
                            String query = "\"query\":\"" + UPDATE + entityName + BLANK + entityName.toLowerCase() + BLANK + WHERE + entityName.toLowerCase() + DOT + UUID_ATTRIBUTE + DOUBLE_EQUALS + "??UUID" + BLANK
                                    + SET + OPENING_CURLY_BRACE
                                    + entityInstance.getAttributes().keySet().stream().map(o -> o + ":" + " ??" + o).collect(Collectors.joining(COMMA))
                                    + CLOSING_CURLY_BRACE + "\"" + COMMA;
                            String parameterNames = "\"parameterNames\":" + OPENING_SQUARE_BRACKET + "\"UUID\"" + entityInstance.getAttributes().keySet().stream().map(o -> "\"" + o + "\"").collect(Collectors.joining(COMMA, COMMA, "")) + CLOSING_SQUARE_BRACKET + COMMA;
                            String parameterTypes = "\"parameterTypes\":" + OPENING_SQUARE_BRACKET + "\"uuid\"" + attributesTypes.stream().map(type -> "\"" + type + "\"").collect(Collectors.joining(COMMA, COMMA, "")) + CLOSING_SQUARE_BRACKET + COMMA;
                            String boundRows = "\"boundRows\":[[" + "\"" + instance.getUuid() + "\"" + entityInstance.getAttributes().keySet().stream().map(key -> entityInstance.getAttributes().get(key).toString().equals("null") ? null : ("\"" + entityInstance.getAttributes().get(key)) + "\"").collect(Collectors.joining(COMMA, COMMA, "")) + "]]";
                            tql = OPENING_CURLY_BRACE + query + parameterNames + parameterTypes + boundRows + CLOSING_CURLY_BRACE;
                            getTyphonQLWebServiceClient().update(tql);
                            break;
                            //Old code, inserting without parametrized queries.
//                            EntityInstance referencedInstance = entityInstance;
//                            String updateQuery = instance.getAttributes().entrySet().stream()
//                                    .filter(attribute -> !attribute.getKey().equals(relationName))
//                                    .map(entrySet -> entrySet.getKey() + COLON + getAttributeValueByType(entrySet, secondEntity)).collect(Collectors.joining(COMMA, OPENING_CURLY_BRACE, CLOSING_CURLY_BRACE.trim()));
//                            tql = new StringBuilder(UPDATE).append(entityName).append(BLANK).append(entityName.toLowerCase()).append(BLANK)
//                                    .append(WHERE).append(entityName.toLowerCase()).append(DOT).append(UUID_ATTRIBUTE).append(EQUALS).append("#").append(referencedInstance.getUuid()).append(BLANK)
//                                    .append(SET).append(updateQuery).toString();
//                            getTyphonQLWebServiceClient().update(tql);
//                            break;
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
