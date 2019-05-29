package com.typhon.evolutiontool.services.typhonML;

import com.typhon.evolutiontool.entities.DatabaseType;
import com.typhon.evolutiontool.entities.Entity;
import com.typhon.evolutiontool.entities.Relation;
import com.typhon.evolutiontool.exceptions.InputParameterException;
import com.typhon.evolutiontool.services.EvolutionServiceImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import typhonml.*;


@Component
public class TyphonMLInterfaceImpl implements TyphonMLInterface {

	Logger logger = LoggerFactory.getLogger(EvolutionServiceImpl.class);

	@Override
	public void setNewTyphonMLModel(String newModelIdentifier) {
		logger.info("Setting current TyphonML to [{}] ", newModelIdentifier);
		//TODO Implement TyphonML interface
	}

	@Override
	public typhonml.Entity getEntityTypeFromName(String entityName, Model model) {
		logger.info("Getting Entity type object from name [{}] on model [{}]", entityName, model);
		DataType dataType = this.getDataTypeFromEntityName(entityName, model);
		if (dataType instanceof typhonml.Entity) {
			return (typhonml.Entity) dataType;
		}
		return null;
	}

	@Override
	public String getAttributeIdOfEntityType(String sourceEntityName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasRelationship(String entityname, Model model) {
	    DataType dataType = this.getDataTypeFromEntityName(entityname,model);
        typhonml.Entity entity = (typhonml.Entity) dataType;
        return !entity.getRelations().isEmpty();
	}

	@Override
	public DatabaseType getDatabaseType(String entityname, Model model) {
		//TODO implement. GenericList?
		return null;
	}

	@Override
	public String getAttributeOfType(String entityname, Entity targetEntityType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Relation getRelationFromName(String relationname) {
		return null;
	}

	@Override
	public Model createEntityType(Model sourceModel, Entity newEntity) {
		Model newModel;
		newModel = EcoreUtil.copy(sourceModel);

		//ENTITY
		typhonml.Entity entity = TyphonmlFactory.eINSTANCE.createEntity();
		entity.setName(newEntity.getName());
		newEntity.getAttributes().entrySet().forEach(entry -> entity.getAttributes().add(this.createAttribute(entry.getKey(), entity)));
		newModel.getDataTypes().add(entity);
		return newModel;
	}

    @Override
    public Model deleteEntityType(String entityname, Model model) {
	    logger.info("Delete Entity type [{}] in TyphonML model", entityname);
        Model newModel;
        newModel = EcoreUtil.copy(model);
//        newModel.getDataTypes().remove(this.getDataTypeFromEntityName(entityname, newModel));
        EcoreUtil.delete(this.getDataTypeFromEntityName(entityname, newModel));
        return newModel;
    }

    @Override
    public Model renameEntity(String oldEntityName, String newEntityName, Model model) {
	    logger.info("Renaming Entity type [{}] to [{}] in TyphonML model", oldEntityName, newEntityName);
        Model newModel;
        newModel = EcoreUtil.copy(model);
        getDataTypeFromEntityName(oldEntityName, newModel).setName(newEntityName);
        return newModel;
    }

	@Override
	public Model copyEntityType(String sourceEntityName, String targetEntityName, Model model) {
		logger.info("Copying Entity type [{}] to [{}] in TyphonML model", sourceEntityName, targetEntityName);
		Model newModel;
		newModel = EcoreUtil.copy(model);
		DataType copyEntity = EcoreUtil.copy(this.getDataTypeFromEntityName(sourceEntityName, newModel));
		copyEntity.setName(targetEntityName);
		newModel.getDataTypes().add(copyEntity);
		return newModel;
	}

	@Override
	public Model createNewEntityMappingInDatabase(DatabaseType databaseType, String dbname, String targetLogicalName, String entityTypeNameToMap, Model targetModel) {

		logger.info("Creating an instance (table/collection...) in Database of type [{}] with name [{}] in TyphonML", databaseType.toString(), dbname);
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
				RelationalDB relationalDB = (RelationalDB)db;
				relationalDB.getTables().add(table);
				table.setDb(relationalDB);
				break;
			case COLUMNDB:
				//TODO
				break;
			case GRAPHDB:
				//TODO
				break;
			case KEYVALUE:
				//TODO
				break;
		}
		return newModel;
	}

	private Database getDatabaseFromName(String dbname, Model model) {
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
	public String getDatabaseName(String sourceEntityName, Model model) {
		return null;
	}

	private DataType getDataTypeFromEntityName(String entityname, Model model) {
        for (DataType datatype : model.getDataTypes()) {
            if (datatype instanceof typhonml.Entity) {
                if (datatype.getName().equals(entityname)) {
                    return datatype;
                }
            }
        }
        return null;
    }

    private Attribute createAttribute(String name, DataType type) {
		//TODO Handling of dataTypes
		Attribute attribute = TyphonmlFactory.eINSTANCE.createAttribute();
		attribute.setName(name);
		attribute.setType(type);
		return attribute;
	}

}
