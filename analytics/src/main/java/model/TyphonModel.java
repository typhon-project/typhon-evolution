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
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import db.AnalyticsDB;
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
//	private static final String GET_ML_MODELS_URL = "api/models/ml";
	private static final String GET_ML_MODELS_URL = "api/model/ml";

	private static String authStringEnc;
	private static final JerseyClient restClient = JerseyClientBuilder.createClient();
	private static WebTarget webTarget;

	private static ResourceSet resourceSet = new ResourceSetImpl();
	private static TyphonModel currentModel;
	static {
		try {
			currentModel = AnalyticsDB.loadLatestRegisteredTyphonModel();
		} catch (Exception | Error e) {
			currentModel = new TyphonModel(-1, null);
		}
	}

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
		logger.info("Connected to polystore webservice: " + url + " => " + username + " / " + password);
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
				e.printStackTrace();
//				logger.error("Impossible to load the current TyphonML model");
//				e.printStackTrace();
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
					
					List<GraphEdge> graphEdges = ((GraphDB) database).getEdges();
					if (graphEdges != null) {
						for (GraphEdge graphEdge : graphEdges) {
							if (graphEdge.getEntity().getName().equals(entityName)) {
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
				
//				List<GraphEdge> graphEdges = ((GraphDB) database).getEdges();
//				if (graphEdges != null) {
//					for (GraphEdge graphEdge : graphEdges) {
//						if (graphEdge.getEntity().getName().equals(entity.getName())) {
//							return graphEdge.getName();
//						}
//					}
//				}
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
		logger.info("updating entities statistics...");
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
		
		logger.info("model verified: " + onlyUpdateHistoriesIfCurrentModelIsOutdated);

		if (!onlyUpdateHistoriesIfCurrentModelIsOutdated
				|| (onlyUpdateHistoriesIfCurrentModelIsOutdated && isOutdated)) {
			logger.info("getting current stats...");
			Map<String, Long> entitySize = DatabaseInformationMgr.getCurrentModelWithStats(newModel, webTarget,
					authStringEnc);
			logger.info("current stats returned");
			AnalyticsDB.saveEntitiesHistory(entitySize, newModel.getVersion());
			logger.info("stats saved");
		}

	}

	public static Long getEntityCount(String entityName) {
		return DatabaseInformationMgr.getCountEntity(webTarget, authStringEnc, entityName);
	}

	public Relation getOpposite(Relation rel) {
		if (rel.getOpposite() != null)
			return rel.getOpposite();

		for (Entity e : getEntities())
			for (Relation r : e.getRelations()) {
				if (r.getOpposite() == rel)
					return r;
			}

		return null;

	}

	public Database getPhysicalDatabase(Entity ent) {
		String entityName = ent.getName();

		for (Database d : getModel().getDatabases()) {
			if (d instanceof RelationalDB) {
				RelationalDB db = (RelationalDB) d;
				for (Table table : db.getTables()) {
					Entity entity = table.getEntity();
					if (entity.getName().equals(entityName)) {
						return d;
					}

				}
			}

			if (d instanceof DocumentDB) {
				DocumentDB db = (DocumentDB) d;
				for (typhonml.Collection collection : db.getCollections()) {
					Entity entity = collection.getEntity();
					if (entity.getName().equals(entityName)) {
						return d;
					}
				}
			}

			if (d instanceof KeyValueDB) {
				KeyValueDB k = (KeyValueDB) d;
				for (KeyValueElement el : k.getElements()) {
					Entity entity = el.getEntity();
					if (entity.getName().equals(entityName)) {
						return d;
					}
				}
			}

			if (d instanceof GraphDB) {
				GraphDB g = (GraphDB) d;
				if (g.getEdges() != null)
					for (GraphEdge e : g.getEdges()) {
						Entity entity = e.getEntity();
						if (entity.getName().equals(entityName)) {
							return d;
						}
					}
				if (g.getNodes() != null)
					for (GraphNode n : g.getNodes()) {
						Entity entity = n.getEntity();
						if (entity.getName().equals(entityName)) {
							return d;
						}
					}

			}
		}

		return null;
	}

	public NamedElement getPhysicalEntity(Entity ent) {
		String entityName = ent.getName();

		for (Database d : getModel().getDatabases()) {
			if (d instanceof RelationalDB) {
				RelationalDB db = (RelationalDB) d;
				for (Table table : db.getTables()) {
					Entity entity = table.getEntity();
					if (entity.getName().equals(entityName)) {
						return table;
					}

				}
			}

			if (d instanceof DocumentDB) {
				DocumentDB db = (DocumentDB) d;
				for (typhonml.Collection collection : db.getCollections()) {
					Entity entity = collection.getEntity();
					if (entity.getName().equals(entityName)) {
						return collection;
					}
				}
			}

			if (d instanceof KeyValueDB) {
				KeyValueDB k = (KeyValueDB) d;
				for (KeyValueElement el : k.getElements()) {
					Entity entity = el.getEntity();
					if (entity.getName().equals(entityName)) {
						return el;
					}
				}
			}

			if (d instanceof GraphDB) {
				GraphDB g = (GraphDB) d;
				for (GraphNode n : g.getNodes()) {
					Entity entity = n.getEntity();
					if (entity.getName().equals(entityName)) {
						return n;
					}
				}
				
//				for(GraphEdge e : g.getEdges()) {
//					Entity entity = e.getEntity();
//					if (entity.getName().equals(entityName)) {
//						return e;
//					}
//				}

			}
		}

		return null;
	}

	public boolean isContainmentRelation(Relation rel) {
		if (rel != null) {
			return rel.getIsContainment() != null && rel.getIsContainment().booleanValue();
		}

		return false;
	}

	public boolean hasOtherRelations(TyphonModel model, Entity ent, Relation rel, Relation opposite) {
		// must check the list of relations defined in entity ent AND relations
		// referring to entity ent

		for (Relation r : ent.getRelations())
			if (r != rel)
				return true;

		for (Entity e : model.getEntities())
			if (e != ent) {
				for (Relation r : e.getRelations())
					if (r.getType() == ent && r != opposite)
						return true;
			}

		return false;
	}

}
