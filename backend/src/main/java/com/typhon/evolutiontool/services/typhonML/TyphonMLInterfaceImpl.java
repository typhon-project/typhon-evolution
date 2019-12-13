package com.typhon.evolutiontool.services.typhonML;

import com.typhon.evolutiontool.entities.*;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.services.EvolutionServiceImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import typhonml.Collection;
import typhonml.Table;
import typhonml.*;

import javax.xml.crypto.Data;
import java.util.List;

public class TyphonMLInterfaceImpl implements TyphonMLInterface {

    private Logger logger = LoggerFactory.getLogger(EvolutionServiceImpl.class);

    @Override
    public Entity getEntityTypeFromName(String entityName, Model model) {
        DataType dataType = this.getDataTypeFromEntityName(entityName, model);
        if (dataType instanceof typhonml.Entity) {
            return (typhonml.Entity) dataType;
        }
        return null;
    }

    @Override
    public boolean hasRelationship(String entityname, Model model) {
        DataType dataType = this.getDataTypeFromEntityName(entityname, model);
        typhonml.Entity entity = (typhonml.Entity) dataType;
        return !entity.getRelations().isEmpty();
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
        Entity entity = this.getEntityTypeFromName(entityName, model);
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
        entity = this.getEntityTypeFromName(entityname, model);
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
    public DataType getDataTypeFromName(String dataTypeName, Model model) {
        List<DataType> dataTypes = model.getDataTypes();
        if (dataTypes != null) {
            for (DataType dataType : dataTypes) {
                if (dataType.getName().equals(dataTypeName)) {
                    return dataType;
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
        newModel.getDataTypes().add(entity);
        newEntity.getAttributes().forEach((name, type) -> entity.getAttributes().add(this.createAttribute(name, type.getName(), newModel)));
        newEntity.getRelations().forEach(relationDO -> entity.getRelations().add(this.createRelation(relationDO, newModel)));
        return newModel;
    }

    @Override
    public Model deleteEntityType(String entityname, Model model) {
        logger.info("Delete EntityDO type [{}] in TyphonML model", entityname);
        Model newModel;
        newModel = EcoreUtil.copy(model);
//        newModel.getDataTypes().remove(this.getDataTypeFromEntityName(entityname, newModel));
        EcoreUtil.delete(this.getDataTypeFromEntityName(entityname, newModel));
        return newModel;
    }

    @Override
    public Model renameEntity(String oldEntityName, String newEntityName, Model model) {
        logger.info("Renaming EntityDO type [{}] to [{}] in TyphonML model", oldEntityName, newEntityName);
        Model newModel = EcoreUtil.copy(model);
        Entity entity = (Entity) getDataTypeFromEntityName(oldEntityName, newModel);
        if (entity != null) {
            entity.setName(newEntityName);
        } else {
            logger.warn("The entity type to rename ('{}') has not been found", oldEntityName);
        }
        return newModel;
    }

    @Override
    public Model copyEntityType(String sourceEntityName, String targetEntityName, Model model) {
        logger.info("Copying EntityDO type [{}] to [{}] in TyphonML model", sourceEntityName, targetEntityName);
        Model newModel;
        newModel = EcoreUtil.copy(model);
        DataType copyEntity = EcoreUtil.copy(this.getDataTypeFromEntityName(sourceEntityName, newModel));
        copyEntity.setName(targetEntityName);
        newModel.getDataTypes().add(copyEntity);
        return newModel;
    }


    @Override
    public Model createRelationship(RelationDO relation, Model model) {
        logger.info("Create Relationship [{}] in [{}] in TyphonML model", relation.getName(), relation.getSourceEntity().getName());
        Model newModel;
        newModel = EcoreUtil.copy(model);
        Entity sourceEntity = this.getEntityTypeFromName(relation.getSourceEntity().getName(), newModel);
        sourceEntity.getRelations().add(this.createRelation(relation, newModel));

        return newModel;
    }

    @Override
    public Model deleteRelationshipInEntity(String relationname, String entityname, Model model) {
        logger.info("Deleting Relationship type [{}] in [{}] in TyphonML model", relationname, entityname);
        Model newModel;
        typhonml.Relation relToDelete = null;
        newModel = EcoreUtil.copy(model);
        typhonml.Entity e = this.getEntityTypeFromName(entityname, newModel);
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
        Entity entity = getEntityTypeFromName(entityName, newModel);
        Attribute attribute = TyphonmlFactory.eINSTANCE.createAttribute();
        attribute.setName(attributeDO.getName());
        attribute.setImportedNamespace(attributeDO.getImportedNamespace());
        attribute.setType(getAttributeDataTypeFromDataTypeName(attributeDO.getDataTypeDO().getName(), newModel));
        entity.getAttributes().add(attribute);
        return newModel;
    }

    @Override
    public Model removeAttribute(String attributeName, String entityName, Model model) {
        Model newModel = EcoreUtil.copy(model);
        Entity entity = getEntityTypeFromName(entityName, newModel);
        if (entity.getAttributes() != null) {
            for (Attribute attribute : entity.getAttributes()) {
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
        Entity entity = getEntityTypeFromName(entityName, newModel);
        if (entity.getAttributes() != null) {
            for (Attribute attribute : entity.getAttributes()) {
                if (attribute.getName().equals(oldAttributeName)) {
                    attribute.setName(newAttributeName);
                    break;
                }
            }
        }
        return newModel;
    }

    @Override
    public Model changeTypeAttribute(AttributeDO attributeDO, String entityName, String dataTypeName, Model model) {
        Model newModel = EcoreUtil.copy(model);
        Entity entity = getEntityTypeFromName(entityName, newModel);
        if (entity.getAttributes() != null) {
            for (Attribute attribute : entity.getAttributes()) {
                if (attribute.getName().equals(attributeDO.getName())) {
                    attribute.setType(getAttributeDataTypeFromDataTypeName(dataTypeName, newModel));
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
    public Model createNewEntityMappingInDatabase(DatabaseType databaseType, String dbname, String targetLogicalName, String entityTypeNameToMap, Model targetModel) {
        logger.info("Creating a mapping Database [{}] of type [{}] to entity [{}] mapped to [{}] in TyphonML", dbname, databaseType, entityTypeNameToMap, targetLogicalName);
        Model newModel;
        newModel = EcoreUtil.copy(targetModel);
        Database db = this.getDatabaseFromName(dbname, newModel);
        typhonml.Entity entityTypeToMap = this.getEntityTypeFromName(entityTypeNameToMap, newModel);
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
        typhonml.Entity entity = this.getEntityTypeFromName(entityName, newModel);
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
    public Model createDatabase(DatabaseType dbtype, String databasename, Model targetModel) throws InputParameterException {
        if (this.getDatabaseFromName(databasename, targetModel) == null) {

            logger.info("Creating a Database of type [{}] with name [{}] in TyphonML", dbtype.toString(), databasename);
            Model newModel;
            newModel = EcoreUtil.copy(targetModel);
            Database db = null;

            switch (dbtype) {
                case DOCUMENTDB:
                    db = TyphonmlFactory.eINSTANCE.createDocumentDB();
                    break;
                case RELATIONALDB:
                    db = TyphonmlFactory.eINSTANCE.createRelationalDB();
                    break;
                case COLUMNDB:
                    db = TyphonmlFactory.eINSTANCE.createColumnDB();
                    break;
                case GRAPHDB:
                    db = TyphonmlFactory.eINSTANCE.createGraphDB();
                    break;
                case KEYVALUE:
                    db = TyphonmlFactory.eINSTANCE.createKeyValueDB();
                    break;
            }
            if (db == null) {
                throw new InputParameterException("Error creating database. Verify that database type is [DOCUMENTDB, RELATIONALDB, COLUMNBD, GRAPHDB, KEYVALUEDB]");
            }
            db.setName(databasename);
            newModel.getDatabases().add(db);
            return newModel;
        }
        return targetModel;
    }

    @Override
    public Model deleteEntityMappings(String entityName, String entityNameInDatabase, Model model) {
        logger.info("Delete database mapping of entity type [{}]  in TyphonML", entityName);
        Model newModel = EcoreUtil.copy(model);
        typhonml.Entity entity = this.getEntityTypeFromName(entityName, newModel);
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

    private DataType getDataTypeFromEntityName(String entityname, Model model) {
        for (DataType datatype : model.getDataTypes()) {
            if (datatype instanceof typhonml.Entity) {
                if (datatype.getName().equalsIgnoreCase(entityname)) {
                    return datatype;
                }
            }
        }
        return null;
    }

    private DataType getAttributeDataTypeFromDataTypeName(String dataTypeName, Model model) {
        for (DataType datatype : model.getDataTypes()) {
            if (datatype.getName().equalsIgnoreCase(dataTypeName)) {
                if (datatype instanceof typhonml.PrimitiveDataType || datatype instanceof CustomDataType) {
                    return datatype;
                }
            }
        }
        return null;
    }

    private Relation createRelation(RelationDO relationDO, Model model) {
        Relation relation = TyphonmlFactory.eINSTANCE.createRelation();
        relation.setName(relationDO.getName());
        relation.setIsContainment(relationDO.isContainment());
        relation.setCardinality(Cardinality.getByName(relationDO.getCardinality().getName()));
        relation.setType((Entity) getDataTypeFromName(relationDO.getTypeName(), model));
        return relation;
    }

    private Attribute createAttribute(String name, String dataTypeName, Model targetModel) {
        //TODO Handling of dataTypes
        Attribute attribute = TyphonmlFactory.eINSTANCE.createAttribute();
        attribute.setName(name);
        attribute.setType(getDataTypeFromName(dataTypeName, targetModel));
        return attribute;
    }
}
