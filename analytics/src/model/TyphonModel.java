package model;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonValue;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import db.AnalyticsDB;
import nl.cwi.swat.typhonql.DBType;
import nl.cwi.swat.typhonql.client.DatabaseInfo;
import typhonml.impl.ModelImpl;
import typhonml.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class TyphonModel {

	private static final String GET_ML_MODEL_URL = "api/model/ml/";
	private static final String GET_ML_MODELS_URL = "api/models/ml";

	private static String authStringEnc;
	private static final JerseyClient restClient = JerseyClientBuilder.createClient();
	private static WebTarget webTarget;

	private static ResourceSet resourceSet = new ResourceSetImpl();
	private static TyphonModel currentModel = AnalyticsDB.loadLatestRegisteredTyphonModel();

	static {
		typhonMLPackageRegistering();
	}

	private static Logger logger = Logger.getLogger(TyphonModel.class);

	///////////////////

	private Model model;
	private int version;

	public TyphonModel(int version, Model model) {
		this.version = version;
		this.model = model;
	}

	public boolean isEmpty() {
		return model == null;
	}

	public static void initWebService(String url, String username, String password) {
		authStringEnc = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
		webTarget = restClient.target(url);
		logger.info("Connected to polystore webservice");
	}

	private static boolean typhonMLPackageRegistering() {

		try {
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
			resourceSet.getPackageRegistry().put(TyphonmlPackage.eINSTANCE.getNsURI(), TyphonmlPackage.eINSTANCE);
			return true;
		} catch (Exception | Error e) {
			logger.error("Impossible to register the resource factory\nCause:");
			e.printStackTrace();
			return false;
		}
	}

	public static TyphonModel getCurrentModel() {
		synchronized (currentModel) {

			File tempFile = null;
			try {

				WebTarget target = webTarget.path(GET_ML_MODELS_URL);
				String result = target.request(MediaType.APPLICATION_JSON)
						.header("Authorization", "Basic " + authStringEnc).get(String.class);

				JSONArray arr = new JSONArray(result);
				String latestModel = null;
				int latestVersion = -1;
				for (int i = 0; i < arr.length(); i++) {
					int modelVersion = arr.getJSONObject(i).getInt("version");
					if (latestVersion < modelVersion) {
						latestVersion = modelVersion;
						latestModel = arr.getJSONObject(i).getString("contents");
					}

				}

				if (currentModel == null || currentModel.isEmpty() || currentModel.getVersion() < latestVersion) {
					tempFile = File.createTempFile("model", ".tmp");
					FileUtils.writeStringToFile(tempFile, latestModel, Charset.defaultCharset());
					Model model = loadModelTyphonML(tempFile.getAbsolutePath());
					currentModel = new TyphonModel(latestVersion, model);
				}

			} catch (Exception | Error e) {
				logger.error("Impossible to load the current TyphonML model\nCause:");
				e.printStackTrace();
			} finally {
				if (tempFile != null)
					try {
						tempFile.delete();
					} catch (Exception | Error e) {
						// error while deleting temp file
					}
			}

		}

		return currentModel;

	}

	public static Model loadModelTyphonML(String modelPath) {

		ResourceSet resourceSet = new ResourceSetImpl();
		URI uri = URI.createFileURI(modelPath);
		Resource resource = resourceSet.getResource(uri, true);
		Model model = (Model) resource.getContents().get(0);
		return model;
	}

	public Entity getEntityTypeFromName(String entityName) {

		for (Entity datatype : model.getEntities()) {
			if (datatype.getName().equalsIgnoreCase(entityName))
				return datatype;
		}

		return null;
	}

	public Database getEntityDatabase(String entityName) {
		List<Database> databases = model != null ? model.getDatabases() : null;
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

	public String getEntityNameInDatabase(String entityName) {
		Entity entity = this.getEntityTypeFromName(entityName);
		Database database = getEntityDatabase(entityName);
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

	public EntityAttributeKind getAttributeFromNameInEntity(String attributename, Entity entity) {
		if (entity != null) {
			for (EntityAttributeKind a : entity.getAttributes()) {
				if (a.getName().equalsIgnoreCase(attributename)) {
					return a;
				}
			}
		}
		return null;
	}

	public EntityAttributeKind getAttributeFromNameInEntity(String attributename, String entityname) {
		Entity entity;
		entity = this.getEntityTypeFromName(entityname);
		return getAttributeFromNameInEntity(attributename, entity);
	}

	public Relation getRelationFromNameInEntity(String relationname, Entity entity) {
		if (entity != null) {
			for (Relation r : entity.getRelations()) {
				if (r.getName().equalsIgnoreCase(relationname)) {
					return r;
				}
			}
		}
		return null;
	}

	public Relation getRelationFromNameInEntity(String relationname, String entityname) {
		Entity entity;
		entity = this.getEntityTypeFromName(entityname);
		return getRelationFromNameInEntity(relationname, entity);
	}

	public Database getDatabaseFromName(String dbname) {
		if (model != null)
			for (Database db : model.getDatabases()) {
				if (db.getName().equals(dbname)) {
					return db;
				}
			}
		return null;
	}

	public List<Entity> getEntities() {
		List<Entity> entities = new ArrayList<Entity>();
		if (model != null)
			for (Entity datatype : model.getEntities()) {
				entities.add(datatype);
			}
		return entities;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public Model getModel() {
		return this.model;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public static TyphonModel checkIfNewModelWasLoaded() {
		getCurrentModelWithStats(true);
		return currentModel;
	}

	public static void getCurrentModelWithStats(boolean onlyUpdateHistoriesIfCurrentModelIsOutdated) {
		TyphonModel oldModel;
		synchronized (currentModel) {
			oldModel = currentModel;
		}
		TyphonModel newModel = getCurrentModel();
		boolean isOutdated = false;
		if (oldModel.getVersion() < newModel.getVersion()) {
			// new model was loaded
			isOutdated = true;
			AnalyticsDB.saveTyphonModel(oldModel, newModel);
		}

		if (!onlyUpdateHistoriesIfCurrentModelIsOutdated
				|| (onlyUpdateHistoriesIfCurrentModelIsOutdated && isOutdated)) {
			Map<String, Long> entitySize = DatabaseInformationMgr.getCurrentModelWithStats(newModel, webTarget,
					authStringEnc);
			AnalyticsDB.saveEntitiesHistory(entitySize, newModel.getVersion());
		}

	}

}
