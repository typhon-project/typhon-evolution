package com.typhon.evolutiontool.services.typhonML;

import com.typhon.evolutiontool.datatypes.*;
import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.services.EvolutionServiceImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import typhonml.Collection;
import typhonml.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TyphonMLInterfaceImpl implements TyphonMLInterface {

    private Logger logger = LoggerFactory.getLogger(EvolutionServiceImpl.class);

    @Override
    public boolean hasRelationship(String entityname, Model model) {
        Entity entity = this.getEntityByEntityName(entityname, model);
        return entity != null && !entity.getRelations().isEmpty();
    }

    @Override
    public boolean hasTableIndex(String databaseName, String tableName, Model sourceModel) {
        logger.info("Checking if table '{}' has an index in TyphonML model database '{}'", tableName, databaseName);
        List<Database> databases = sourceModel.getDatabases();
        if (databases != null) {
            for (Database database : databases) {
                if (database instanceof RelationalDB && databaseName.equals(database.getName())) {
                    List<Table> tables = ((RelationalDB) database).getTables();
                    if (tables != null) {
                        for (Table table : tables) {
                            if (tableName.equals(table.getName())) {
                                return table.getIdSpec() != null;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Model addTableIndex(String databaseName, String tableName, String entityName, Set<String> entityAttributesNames, Model sourceModel) {
        logger.info("Add attributes to table '{}' index list in TyphonML model database '{}'", tableName, databaseName);
        Model newModel = EcoreUtil.copy(sourceModel);
        Entity entity = this.getEntityByEntityName(entityName, newModel);
        List<Attribute> attributesToAddToIndex = getEntityAttributes(entity, entityAttributesNames);
        List<Database> databases = newModel.getDatabases();
        if (databases != null) {
            for (Database database : databases) {
                if (database instanceof RelationalDB && databaseName.equals(database.getName())) {
                    List<Table> tables = ((RelationalDB) database).getTables();
                    if (tables != null) {
                        for (Table table : tables) {
                            if (tableName.equals(table.getName())) {
                                IdSpec idSpec = table.getIdSpec();
                                if (idSpec == null) {
                                    idSpec = TyphonmlFactory.eINSTANCE.createIdSpec();
                                    table.setIdSpec(idSpec);
                                }
                                List<Attribute> attributes = idSpec.getAttributes();
                                if (attributes == null) {
                                    attributes = new ArrayList<>();
                                }
                                attributes.addAll(attributesToAddToIndex);
                            }
                        }
                    }
                }
            }
        }
        return newModel;
    }

    @Override
    public Model addCollectionIndex(String databaseName, String collectionName, String entityName, Set<String> entityAttributesNames, Model sourceModel) {
        logger.info("Add attributes to collection '{}' index list in TyphonML model database '{}'", collectionName, databaseName);
        Model newModel = EcoreUtil.copy(sourceModel);
        Entity entity = this.getEntityByEntityName(entityName, newModel);
        List<Attribute> attributesToAddToIndex = getEntityAttributes(entity, entityAttributesNames);
        List<Database> databases = newModel.getDatabases();
        if (databases != null) {
            for (Database database : databases) {
                if (database instanceof DocumentDB && databaseName.equals(database.getName())) {
                    List<Collection> collections = ((DocumentDB) database).getCollections();
                    if (collections != null) {
                        for (Collection collection : collections) {
                            if (collectionName.equals(collection.getName())) {
//                                //TODO: build the IdSpecDO when ML and QL have implemented the change operator
//                                IdSpec idSpec = collection.getIdSpec();
//                                if (idSpec == null) {
//                                    idSpec = TyphonmlFactory.eINSTANCE.createIdSpec();
//                                    collection.setIdSpec(idSpec);
//                                }
//                                List<Attribute> attributes = idSpec.getAttributes();
//                                if (attributes == null) {
//                                    attributes = new ArrayList<>();
//                                }
//                                attributes.addAll(attributesToAddToIndex);
                            }
                        }
                    }
                }
            }
        }
        return newModel;
    }

    @Override
    public Database getEntityDatabase(String entityName, Model model) {
        List<Database> databases = model.getDatabases();
        if (databases != null) {
            for (Database database : databases) {
                if (database instanceof RelationalDB) {
                    List<Table> tables = ((RelationalDB) database).getTables();
                    if (tables != null) {
                        for (Table table : tables) {
                            if (table.getEntity().getName().equals(entityName)) {
                                return database;
                            }
                        }
                    }
                }
                if (database instanceof DocumentDB) {
                    List<Collection> collections = ((DocumentDB) database).getCollections();
                    if (collections != null) {
                        for (Collection collection : collections) {
                            if (collection.getEntity().getName().equals(entityName)) {
                                return database;
                            }
                        }
                    }
                }
                if (database instanceof GraphDB) {
                    List<GraphNode> graphNodes = ((GraphDB) database).getNodes();
                    if (graphNodes != null) {
                        for (GraphNode graphNode : graphNodes) {
                            if (graphNode.getEntity().getName().equals(entityName)) {
                                return database;
                            }
                        }
                    }
                }
                if (database instanceof ColumnDB) {
                    List<Column> columns = ((ColumnDB) database).getColumns();
                    if (columns != null) {
                        for (Column column : columns) {
                            if (column.getEntity().getName().equals(entityName)) {
                                return database;
                            }
                        }
                    }
                }
                if (database instanceof KeyValueDB) {
                    List<KeyValueElement> keyValueElements = ((KeyValueDB) database).getElements();
                    if (keyValueElements != null) {
                        for (KeyValueElement keyValueElement : keyValueElements) {
                            if (keyValueElement.getEntity().getName().equals(entityName)) {
                                return database;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String getEntityNameInDatabase(String entityName, Model model) {
        Entity entity = this.getEntityByName(entityName, model);
        Database database = getEntityDatabase(entityName, model);
        if (database != null) {
            if (database instanceof RelationalDB) {
                List<Table> tables = ((RelationalDB) database).getTables();
                if (tables != null) {
                    for (Table table : tables) {
                        if (table.getEntity().getName().equals(entity.getName())) {
                            return table.getName();
                        }
                    }
                }
            }
            if (database instanceof DocumentDB) {
                List<Collection> collections = ((DocumentDB) database).getCollections();
                if (collections != null) {
                    for (Collection collection : collections) {
                        if (collection.getEntity().getName().equals(entity.getName())) {
                            return collection.getName();
                        }
                    }
                }
            }
            if (database instanceof GraphDB) {
                List<GraphNode> graphNodes = ((GraphDB) database).getNodes();
                if (graphNodes != null) {
                    for (GraphNode graphNode : graphNodes) {
                        if (graphNode.getEntity().getName().equals(entity.getName())) {
                            return graphNode.getName();
                        }
                    }
                }
            }
            if (database instanceof ColumnDB) {
                List<Column> columns = ((ColumnDB) database).getColumns();
                if (columns != null) {
                    for (Column column : columns) {
                        if (column.getEntity().getName().equals(entity.getName())) {
                            return column.getName();
                        }
                    }
                }
            }
            if (database instanceof KeyValueDB) {
                List<KeyValueElement> keyValueElements = ((KeyValueDB) database).getElements();
                if (keyValueElements != null) {
                    for (KeyValueElement keyValueElement : keyValueElements) {
                        if (keyValueElement.getEntity().getName().equals(entity.getName())) {
                            return keyValueElement.getName();
                        }
                    }
                }
            }
        }
        return null;
    }


    @Override
    public Relation getRelationFromNameInEntity(String relationname, String entityname, Model model) {
        Entity entity;
        entity = this.getEntityByName(entityname, model);
        if (entity != null) {
            for (Relation r : entity.getRelations()) {
                if (r.getName().equalsIgnoreCase(relationname)) {
                    return r;
                }
            }
        }
        return null;
    }

    @Override
    public Entity getEntityByName(String dataTypeName, Model model) {
        List<Entity> entities = model.getEntities();
        if (entities != null) {
            for (Entity entity : entities) {
                if (entity.getName().equals(dataTypeName)) {
                    return entity;
                }
            }
        }
        return null;
    }

    @Override
    public Model createEntityType(Model sourceModel, EntityDO newEntity) {
        logger.info("Create Entity type [{}] in TyphonML model", newEntity.getName());
        Model newModel;
        newModel = EcoreUtil.copy(sourceModel);

        //ENTITY
        typhonml.Entity entity = TyphonmlFactory.eINSTANCE.createEntity();
        entity.setName(newEntity.getName());
        newModel.getEntities().add(entity);
        newEntity.getAttributes().forEach((name, type) -> entity.getAttributes().add(this.createAttribute(name, type, newModel)));
        newEntity.getRelations().forEach(relationDO -> entity.getRelations().add(this.createRelation(relationDO, newModel)));
        return newModel;
    }

    @Override
    public Model deleteEntityType(String entityname, Model model) {
        logger.info("Delete EntityDO type [{}] in TyphonML model", entityname);
        Model newModel;
        newModel = EcoreUtil.copy(model);
//        newModel.getDataTypes().remove(this.getDataTypeFromEntityName(entityname, newModel));
        EcoreUtil.delete(this.getEntityByEntityName(entityname, newModel));
        return newModel;
    }

    @Override
    public Model renameEntity(String oldEntityName, String newEntityName, Model model) {
        logger.info("Renaming EntityDO type [{}] to [{}] in TyphonML model", oldEntityName, newEntityName);
        Model newModel = EcoreUtil.copy(model);
        Entity entity = (Entity) getEntityByEntityName(oldEntityName, newModel);
        if (entity != null) {
            entity.setName(newEntityName);
        } else {
            logger.warn("The entity type to rename ('{}') has not been found", oldEntityName);
        }
        return newModel;
    }

    @Override
    public Model createRelationship(RelationDO relation, Model model) {
        logger.info("Create Relationship [{}] in [{}] in TyphonML model", relation.getName(), relation.getSourceEntity().getName());
        Model newModel;
        newModel = EcoreUtil.copy(model);
        Entity sourceEntity = this.getEntityByName(relation.getSourceEntity().getName(), newModel);
        sourceEntity.getRelations().add(this.createRelation(relation, newModel));

        return newModel;
    }

    @Override
    public Model deleteRelationshipInEntity(String relationname, String entityname, Model model) {
        logger.info("Deleting Relationship type [{}] in [{}] in TyphonML model", relationname, entityname);
        Model newModel;
        typhonml.Relation relToDelete = null;
        newModel = EcoreUtil.copy(model);
        typhonml.Entity e = this.getEntityByName(entityname, newModel);
        if (e != null && e.getRelations() != null) {
            for (typhonml.Relation relation : e.getRelations()) {
                if (relation.getName().equals(relationname)) {
                    relToDelete = relation;
                }
            }
        }
        if (relToDelete != null)
            EcoreUtil.delete(relToDelete);
        return newModel;
    }

    @Override
    public Model enableContainment(RelationDO relation, Model model) {
        Model newModel = EcoreUtil.copy(model);
        Relation relationML = this.getRelationFromNameInEntity(relation.getName(), relation.getSourceEntity().getName(), newModel);
        relationML.setIsContainment(true);
        return newModel;
    }

    @Override
    public Model disableContainment(RelationDO relation, Model model) {
        Model newModel = EcoreUtil.copy(model);
        Relation relationML = this.getRelationFromNameInEntity(relation.getName(), relation.getSourceEntity().getName(), newModel);
        relationML.setIsContainment(false);
        return newModel;
    }

    @Override
    public Model changeCardinalityInRelation(RelationDO relation, CardinalityDO cardinality, Model model) {
        Relation relationML;
        Model newModel;
        newModel = EcoreUtil.copy(model);
        relationML = this.getRelationFromNameInEntity(relation.getName(), relation.getSourceEntity().getName(), newModel);
        relationML.setCardinality(Cardinality.getByName(cardinality.getName()));
        return newModel;
    }

    @Override
    public Model addAttribute(AttributeDO attributeDO, String entityName, Model model) {
        Model newModel = EcoreUtil.copy(model);
        Entity entity = getEntityByName(entityName, newModel);
        Attribute attribute = TyphonmlFactory.eINSTANCE.createAttribute();
        attribute.setName(attributeDO.getName());
        attribute.setImportedNamespace(attributeDO.getImportedNamespace());
        attribute.setType(getDataType(attributeDO.getDataTypeDO()));
        entity.getAttributes().add(attribute);
        return newModel;
    }

    @Override
    public Model removeAttribute(String attributeName, String entityName, Model model) {
        Model newModel = EcoreUtil.copy(model);
        Entity entity = getEntityByName(entityName, newModel);
        if (entity.getAttributes() != null) {
            for (EntityAttributeKind attribute : entity.getAttributes()) {
                if (attribute.getName().equals(attributeName)) {
                    entity.getAttributes().remove((attribute));
                    break;
                }
            }
        }
        return newModel;
    }

    @Override
    public Model renameAttribute(String oldAttributeName, String newAttributeName, String entityName, Model model) {
        Model newModel = EcoreUtil.copy(model);
        Entity entity = getEntityByName(entityName, newModel);
        if (entity.getAttributes() != null) {
            for (EntityAttributeKind attribute : entity.getAttributes()) {
                if (attribute.getName().equals(oldAttributeName)) {
                    attribute.setName(newAttributeName);
                    break;
                }
            }
        }
        return newModel;
    }

    @Override
    public Model changeTypeAttribute(AttributeDO attributeDO, String entityName, DataTypeDO attributeDataType, Model model) {
        Model newModel = EcoreUtil.copy(model);
        Entity entity = getEntityByName(entityName, newModel);
        if (entity.getAttributes() != null) {
            for (EntityAttributeKind attribute : entity.getAttributes()) {
                if (attribute.getName().equals(attributeDO.getName())) {
                    ((Attribute) attribute).setType(getDataType(attributeDataType));
                    break;
                }
            }
        }
        return newModel;
    }

    @Override
    public Model enableOpposite(RelationDO relation, RelationDO oppositeRelation, Model model) {
        Model newModel = EcoreUtil.copy(model);
        Relation relationML = this.getRelationFromNameInEntity(relation.getName(), relation.getSourceEntity().getName(), newModel);
        Relation oppositeRelationML = this.getRelationFromNameInEntity(oppositeRelation.getName(), oppositeRelation.getSourceEntity().getName(), newModel);
        relationML.setOpposite(oppositeRelationML);
        return newModel;
    }

    @Override
    public Model disableOpposite(RelationDO relation, Model model) {
        Model newModel = EcoreUtil.copy(model);
        Relation relationML = this.getRelationFromNameInEntity(relation.getName(), relation.getSourceEntity().getName(), newModel);
        relationML.setOpposite(null);
        return newModel;
    }

    @Override
    public Model renameRelation(String relationName, String entityName, String newRelationName, Model model) {
        logger.info("Renaming Relation [{} in {} entity] to [{}] in TyphonML model", relationName, entityName, newRelationName);
        Model newModel;
        newModel = EcoreUtil.copy(model);
        getRelationFromNameInEntity(relationName, entityName, newModel).setName(newRelationName);
        return newModel;
    }

    @Override
    public Model removeCurrentChangeOperator(Model model) {
        Model newModel = EcoreUtil.copy(model);
        List<ChangeOperator> changeOperators = newModel.getChangeOperators();
        if (changeOperators != null && changeOperators.get(0) != null) {
            changeOperators.remove(changeOperators.get(0));
        }
        return newModel;
    }

    @Override
    public Model mergeEntities(String firstEntityName, String secondEntityName, Model model) {
        Model newModel = EcoreUtil.copy(model);
        Entity firstEntity = getEntityByName(firstEntityName, newModel);
        Entity secondEntity = getEntityByName(secondEntityName, newModel);
        //Remove the relation between the entities
        if (firstEntity != null && secondEntity != null) {
            List<Relation> firstEntityRelations = firstEntity.getRelations();
            if (firstEntityRelations != null) {
                for (Relation firstRelation : firstEntityRelations) {
                    if (firstRelation.getType().getName().equals(secondEntity.getName())) {
                        firstEntityRelations.remove(firstRelation);
                        break;
                    }
                }
            }
            List<Relation> secondEntityRelations = secondEntity.getRelations();
            if (secondEntityRelations != null) {
                for (Relation secondRelation : secondEntityRelations) {
                    if (secondRelation.getType().getName().equals(firstEntity.getName())) {
                        secondEntityRelations.remove(secondRelation);
                        break;
                    }
                }
            }
            //Merge second entity attributes and relations into the first one
            if (firstEntity.getAttributes() != null && secondEntity.getAttributes() != null) {
                firstEntity.getAttributes().addAll(secondEntity.getAttributes());
            }
            if (firstEntity.getRelations() != null && secondEntity.getRelations() != null) {
                firstEntity.getRelations().addAll(secondEntity.getRelations());
            }
        }
        return newModel;
    }

    @Override
    public Model createNewEntityMappingInDatabase(DatabaseType databaseType, String dbname, String targetLogicalName, String entityTypeNameToMap, Model targetModel) {
        logger.info("Creating a mapping Database [{}] of type [{}] to entity [{}] mapped to [{}] in TyphonML", dbname, databaseType, entityTypeNameToMap, targetLogicalName);
        Model newModel;
        newModel = EcoreUtil.copy(targetModel);
        Database db = this.getDatabaseFromName(dbname, newModel);
        typhonml.Entity entityTypeToMap = this.getEntityByName(entityTypeNameToMap, newModel);
        switch (databaseType) {
            case DOCUMENTDB:
                Collection collection = TyphonmlFactory.eINSTANCE.createCollection();
                collection.setName(targetLogicalName);
                collection.setEntity(entityTypeToMap);
                DocumentDB documentDB = (DocumentDB) db;
                documentDB.getCollections().add(collection);
                break;
            case RELATIONALDB:
                Table table = TyphonmlFactory.eINSTANCE.createTable();
                table.setName(targetLogicalName);
                table.setEntity(entityTypeToMap);
                RelationalDB relationalDB = (RelationalDB) db;
                relationalDB.getTables().add(table);
                table.setDb(relationalDB);
                break;
            case COLUMNDB:
                Column column = TyphonmlFactory.eINSTANCE.createColumn();
                column.setName(targetLogicalName);
                column.setEntity(entityTypeToMap);
                ColumnDB columnDB = (ColumnDB) db;
                columnDB.getColumns().add(column);
                break;
            case GRAPHDB:
                GraphNode graphNode = TyphonmlFactory.eINSTANCE.createGraphNode();
                graphNode.setName(targetLogicalName);
                graphNode.setEntity(entityTypeToMap);
                GraphDB graphDB = (GraphDB) db;
                graphDB.getNodes().add(graphNode);
                break;
            case KEYVALUE:
                KeyValueElement keyValueElement = TyphonmlFactory.eINSTANCE.createKeyValueElement();
                keyValueElement.setName(targetLogicalName);
                keyValueElement.setEntity(entityTypeToMap);
                KeyValueDB keyValueDB = (KeyValueDB) db;
                keyValueDB.getElements().add(keyValueElement);
                break;
        }
        return newModel;
    }

    @Override
    public Model updateEntityMappingInDatabase(String entityName, String databaseName, Model model) {
        logger.info("Updating the mapping for entity [{}] in TyphonML", entityName);
        Model newModel;
        newModel = EcoreUtil.copy(model);
        typhonml.Entity entity = this.getEntityByName(entityName, newModel);
        Database database = getDatabaseFromName(databaseName, newModel);
        if (database instanceof DocumentDB) {
            DocumentDB documentDB = (DocumentDB) database;
            List<Collection> collections = documentDB.getCollections();
            if (collections != null) {
                for (Collection collection : collections) {
                    if (collection.getEntity().getName().equals(entityName)) {
                        collection.setEntity(entity);
                        return newModel;
                    }
                }
            }
        }
        if (database instanceof RelationalDB) {
            RelationalDB relationalDB = (RelationalDB) database;
            List<Table> tables = relationalDB.getTables();
            if (tables != null) {
                for (Table table : tables) {
                    if (table.getEntity().getName().equals(entityName)) {
                        table.setEntity(entity);
                        return newModel;
                    }
                }
            }
        }
        if (database instanceof ColumnDB) {
            ColumnDB columnDB = (ColumnDB) database;
            List<Column> columns = columnDB.getColumns();
            if (columns != null) {
                for (Column column : columns) {
                    if (column.getEntity().getName().equals(entityName)) {
                        column.setEntity(entity);
                        return newModel;
                    }
                }
            }
        }
        if (database instanceof GraphDB) {
            GraphDB graphDB = (GraphDB) database;
            List<GraphNode> graphNodes = graphDB.getNodes();
            if (graphNodes != null) {
                for (GraphNode graphNode : graphNodes) {
                    if (graphNode.getEntity().getName().equals(entityName)) {
                        graphNode.setEntity(entity);
                        return newModel;
                    }
                }
            }
        }
        if (database instanceof KeyValueDB) {
            KeyValueDB keyValueDB = (KeyValueDB) database;
            List<KeyValueElement> keyValueElements = keyValueDB.getElements();
            if (keyValueElements != null) {
                for (KeyValueElement keyValueElement : keyValueElements) {
                    if (keyValueElement.getEntity().getName().equals(entityName)) {
                        keyValueElement.setEntity(entity);
                        return newModel;
                    }
                }
            }
        }
        return newModel;
    }

    @Override
    public Database getDatabaseFromName(String dbname, Model model) {
        for (Database db : model.getDatabases()) {
            if (db.getName().equals(dbname)) {
                return db;
            }
        }
        return null;
    }

    @Override
    public Model deleteEntityMappings(String entityName, String entityNameInDatabase, Model model) {
        logger.info("Delete database mapping of entity type [{}]  in TyphonML", entityName);
        Model newModel = EcoreUtil.copy(model);
        typhonml.Entity entity = this.getEntityByName(entityName, newModel);
        //Remove the entity from the database containing the entity
        removeEntityFromDatabase(entityNameInDatabase, getEntityDatabase(entityName, newModel));
        return newModel;
    }

    private void removeEntityFromDatabase(String entityNameInDatabase, Database database) {
        if (database != null) {
            if (database instanceof RelationalDB) {
                List<Table> tables = ((RelationalDB) database).getTables();
                if (tables != null) {
                    for (Table table : tables) {
                        if (table.getName().equals(entityNameInDatabase)) {
                            tables.remove(table);
                            return;
                        }
                    }
                }
            }
            if (database instanceof DocumentDB) {
                List<Collection> collections = ((DocumentDB) database).getCollections();
                if (collections != null) {
                    for (Collection collection : collections) {
                        if (collection.getName().equals(entityNameInDatabase)) {
                            collections.remove(collection);
                            return;
                        }
                    }
                }
            }
            if (database instanceof GraphDB) {
                List<GraphNode> graphNodes = ((GraphDB) database).getNodes();
                if (graphNodes != null) {
                    for (GraphNode graphNode : graphNodes) {
                        if (graphNode.getName().equals(entityNameInDatabase)) {
                            graphNodes.remove(graphNode);
                            return;
                        }
                    }
                }
            }
            if (database instanceof ColumnDB) {
                List<Column> columns = ((ColumnDB) database).getColumns();
                if (columns != null) {
                    for (Column column : columns) {
                        if (column.getName().equals(entityNameInDatabase)) {
                            columns.remove(column);
                            return;
                        }
                    }
                }
            }
            if (database instanceof KeyValueDB) {
                List<KeyValueElement> keyValueElements = ((KeyValueDB) database).getElements();
                if (keyValueElements != null) {
                    for (KeyValueElement keyValueElement : keyValueElements) {
                        if (keyValueElement.getName().equals(entityNameInDatabase)) {
                            keyValueElements.remove(keyValueElement);
                            return;
                        }
                    }
                }
            }
        }
    }

    private Entity getEntityByEntityName(String entityName, Model model) {
        for (Entity entity : model.getEntities()) {
            if (entity.getName().equalsIgnoreCase(entityName)) {
                return entity;
            }
        }
        return null;
    }

    private DataType getAttributeTypeFromTypeName(String dataTypeName, Model model) {
        //TODO: Refactor AttributeDO - create all Attribute types DO
//        for (DataType datatype : model.getDataTypes()) {
//            if (datatype.getName().equalsIgnoreCase(dataTypeName)) {
//                if (datatype instanceof typhonml.PrimitiveDataType || datatype instanceof CustomDataType) {
//                    return datatype;
//                }
//            }
//        }
        return null;
    }

    private Relation createRelation(RelationDO relationDO, Model model) {
        Relation relation = TyphonmlFactory.eINSTANCE.createRelation();
        relation.setName(relationDO.getName());
        relation.setIsContainment(relationDO.isContainment());
        relation.setCardinality(Cardinality.getByName(relationDO.getCardinality().getName()));
        relation.setType((Entity) getEntityByEntityName(relationDO.getTypeName(), model));
        return relation;
    }

    private Attribute createAttribute(String name, DataTypeDO dataType, Model targetModel) {
        Attribute attribute = TyphonmlFactory.eINSTANCE.createAttribute();
        attribute.setName(name);
        attribute.setType(getDataType(dataType));
        return attribute;
    }

    private DataType getDataType(DataTypeDO dataType) {
        if (dataType != null) {
            if (dataType instanceof BigIntTypeDO) {
                return TyphonmlFactory.eINSTANCE.createBigintType();
            }
            if (dataType instanceof BlobTypeDO) {
                return TyphonmlFactory.eINSTANCE.createBlobType();
            }
            if (dataType instanceof BoolTypeDO) {
                return TyphonmlFactory.eINSTANCE.createBoolType();
            }
            if (dataType instanceof DatetimeTypeDO) {
                return TyphonmlFactory.eINSTANCE.createDatetimeType();
            }
            if (dataType instanceof DateTypeDO) {
                return TyphonmlFactory.eINSTANCE.createDateType();
            }
            if (dataType instanceof FloatTypeDO) {
                return TyphonmlFactory.eINSTANCE.createFloatType();
            }
            if (dataType instanceof FreetextTypeDO) {
                return TyphonmlFactory.eINSTANCE.createFreetextType();
            }
            if (dataType instanceof IntTypeDO) {
                return TyphonmlFactory.eINSTANCE.createIntType();
            }
            if (dataType instanceof PointTypeDO) {
                return TyphonmlFactory.eINSTANCE.createPointType();
            }
            if (dataType instanceof PolygonTypeDO) {
                return TyphonmlFactory.eINSTANCE.createPolygonType();
            }
            if (dataType instanceof StringTypeDO) {
                StringType stringType = TyphonmlFactory.eINSTANCE.createStringType();
                stringType.setMaxSize(((StringTypeDO) dataType).getMaxSize());
                return stringType;
            }
            if (dataType instanceof TextTypeDO) {
                return TyphonmlFactory.eINSTANCE.createTextType();
            }
        }
        return null;
    }

    private List<Attribute> getEntityAttributes(Entity entity, Set<String> entityAttributesNames) {
        List<Attribute> attributes = new ArrayList<>();
        if (entity != null && entityAttributesNames != null && !entityAttributesNames.isEmpty()) {
            attributes = entity.getAttributes().stream().map(attribute -> (Attribute) attribute).filter(attribute -> entityAttributesNames.contains(attribute.getName())).collect(Collectors.toList());
        }
        return attributes;
    }
}
