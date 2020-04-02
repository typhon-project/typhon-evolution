package db;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.WriteResult;
import com.mongodb.MongoCredential;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.util.JSON;

import capture.mains.AttributeSelector;
import capture.mains.ConsumePostEvents;
import capture.mains.Insert;
import capture.mains.Join;
import capture.mains.Query;
import model.TyphonModel;
import typhonml.Entity;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

public class AnalyticsDB {
	private static MongoClient mongoClient = null;
	private static MongoDatabase database = null;
	private static Logger logger = Logger.getLogger(AnalyticsDB.class);

	private static final String TYPHON_MODEL_COLLECTION = "TyphonModel";
	private static final String TYPHON_ENTITY_COLLECTION = "TyphonEntity";
	private static final String TYPHON_ENTITY_HISTORY_COLLECTION = "TyphonEntityHistory";
	private static final String QL_NORMALIZED_QUERY_COLLECTION = "QLNormalizedQuery";
	private static final String QL_QUERY_COLLECTION = "QLQuery";

	public static void saveTyphonModel(TyphonModel oldModel, TyphonModel newModel) {
		logger.info("New Typhon model was loaded: " + newModel.getVersion());

		MongoCollection<Document> coll = database.getCollection(TYPHON_MODEL_COLLECTION);
		
		Document document = new Document();
		document.put("version", newModel.getVersion());
		document.put("date", new Date().getTime());

		coll.insertOne(document);

		List<Entity> entities = newModel.getEntities();
		MongoCollection<Document> entityColl = database.getCollection(TYPHON_ENTITY_COLLECTION);
		for (Entity ent : entities) {
			String entityName = ent.getName();

			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.append("name", entityName);

//			BasicDBObject updateQuery = new BasicDBObject();
//			updateQuery.append("$set", new BasicDBObject().append("latestVersion", newModel.getVersion()));

//			db.students.update(
//					   { _id: 1 },
//					   { $push: { scores: 89 } }
//					)

			BasicDBObject updateQuery = (BasicDBObject) JSON.parse("{ $push: { versions: " + newModel.getVersion()
					+ " }, $set: {latestVersion: " + newModel.getVersion() + "} }");
			UpdateResult ur = entityColl.updateOne(searchQuery, updateQuery);
			long nbOfModifiedDocs = ur.getModifiedCount();

			if (nbOfModifiedDocs == 0) {
				// entity is not in the db yet
				Document entityDoc = new Document();
				entityDoc.put("name", entityName);
				entityDoc.put("latestVersion", newModel.getVersion());
				List<Integer> versions = new ArrayList<Integer>();
				versions.add(newModel.getVersion());
				entityDoc.put("versions", versions);
				entityColl.insertOne(entityDoc);
				logger.info("new entity mongo inserted");
			} else {
				logger.info("update in Entity mongo");
			}

//			Document entityDoc = entityColl.find(Filters.eq("name", entityName)).first();
//			if(entityDoc == null) {
//				//new entity
//				entityDoc = new Document();
//				entityDoc.put("name", entityName);
//				entityDoc.put("latestVersion", newModel.getVersion());
//			} else {
//				entityDoc.
//			}
		}

	}
	
	private static void createIndex(MongoCollection<Document> coll, boolean unique, String... attributes) {
		
		
		String index = "{";
		int i = 0;
		for(String attr : attributes) {
			if(i > 0)
				index += ", ";
			index += attr + ": 1";
			i++;
		}
		
		index += "}";
		if(unique)
			index += ", {unique: true}";
		
		logger.debug("Created index: " + index);
		coll.createIndex((BasicDBObject) JSON.parse(index));
	}

	public static boolean initConnection(String ip, int port, String username, String password, String dbName) {

		try {

			String mongoUrl = "mongodb://" + username + ":" + password + "@" + ip + ":" + port;
			MongoClientURI uri = new MongoClientURI(mongoUrl);
			mongoClient = new MongoClient(uri);
			database = mongoClient.getDatabase(dbName);
			logger.info("Connected to Analytics DB");
			
			MongoCollection<Document> coll = database.getCollection(TYPHON_MODEL_COLLECTION);
			createIndex(coll, true, "version");
			
			coll = database.getCollection(TYPHON_ENTITY_COLLECTION);
			createIndex(coll, true, "name");
			
			coll = database.getCollection(TYPHON_ENTITY_HISTORY_COLLECTION);
			createIndex(coll, true, "name", "updateDate");
			createIndex(coll, false, "updateDate");
			
			coll = database.getCollection(QL_NORMALIZED_QUERY_COLLECTION);
			createIndex(coll, true, "normalizedForm");
			
			coll = database.getCollection(QL_QUERY_COLLECTION);
			createIndex(coll, false, "normalizedQueryId");
			createIndex(coll, false, "executionDate");
			
			return true;
		} catch (Exception | Error e) {
			logger.error("Impossible to connect the Analytics DB\nCause:");
			e.printStackTrace();
			closeConnection();
			return false;
		}

	}

	private static void closeConnection() {

		try {
			if (mongoClient != null)
				mongoClient.close();

		} catch (Exception | Error e) {

		}

	}

	public static TyphonModel loadLatestRegisteredTyphonModel() {
		logger.info("restoring latest model ...");
		MongoCollection<Document> coll = database.getCollection(TYPHON_MODEL_COLLECTION);
		FindIterable<Document> iterable = coll.find();
		MongoCursor<Document> iterator = iterable.iterator();
		int latestVersion = -1;
		Document latestModel = null;
		while (iterator.hasNext()) {
			Document d = iterator.next();
			if (d.getInteger("version") > latestVersion) {
				latestVersion = d.getInteger("version");
				latestModel = d;
			}
		}

		if (latestModel != null) {
			// existing model, need to load it
			logger.info("restore latest saved model: " + latestModel.getInteger("version"));
			return new TyphonModel(latestVersion, null);
		} else {
			// no current model found, first execution
			return new TyphonModel(-1, null);
		}

	}

	public static void saveEntitiesHistory(Map<String, Long> entitySize, int version) {

		MongoCollection<Document> coll = database.getCollection(TYPHON_ENTITY_HISTORY_COLLECTION);

		List<Document> documents = new ArrayList<Document>();
		for (Entry<String, Long> entry : entitySize.entrySet()) {
			String entityName = entry.getKey();
			Long dataSize = entry.getValue();

			Document document = new Document();
			document.put("name", entityName);
			document.put("updateDate", new Date().getTime());
			document.put("modelVersion", version);
			document.put("dataSize", dataSize);

			document.put("nbOfQueries", 0);
			document.put("nbOfSelect", 0);
			document.put("nbOfInsert", 0);
			document.put("nbOfUpdate", 0);
			document.put("nbOfDelete", 0);

			documents.add(document);
		}

		coll.insertMany(documents);

	}

	public static void saveExecutedQuery(Query q, Date startDate, long executionTime) {
		String normalizedQuery = q.getNormalizedQuery();
		String displayableQuery = q.getDisplayableQuery();
		MongoCollection<Document> coll = database.getCollection(QL_NORMALIZED_QUERY_COLLECTION);

		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.append("normalizedForm", normalizedQuery);

		BasicDBObject updateQuery = (BasicDBObject) JSON.parse("{ $inc: { count: 1 } }");

		Document queryTempl = coll.findOneAndUpdate(searchQuery, updateQuery);

		if (queryTempl == null) {
			queryTempl = new Document();
			queryTempl.put("normalizedForm", normalizedQuery);
			queryTempl.put("displayableForm", displayableQuery);
			queryTempl.put("count", 1);
			coll.insertOne(queryTempl);
		}

		ObjectId id = queryTempl.getObjectId("_id");

		///////////////////////////////

		MongoCollection<Document> queryColl = database.getCollection(QL_QUERY_COLLECTION);
		Document query = new Document();
		query.put("normalizedQueryId", id);
		query.put("query", q.getOriginalQuery());
		query.put("type", q.getQueryType());
		query.put("executionDate", startDate.getTime());
		query.put("executionTime", executionTime);
		query.put("modelVersion", q.getModel().getVersion());
		if (q.getMainEntities().size() > 0)
			query.put("mainEntities", q.getMainEntities());

		Map<String, EntityStats> entitiesStats = new HashMap<String, EntityStats>();
		///// main entities
		for (String mainEntity : q.getMainEntities()) {
			EntityStats stat = entitiesStats.get(mainEntity);
			if (stat == null) {
				stat = new EntityStats(mainEntity);
				entitiesStats.put(mainEntity, stat);
			}

			switch (q.getQueryType()) {
			case "SELECT":
				stat.incrementSelects();
				break;
			case "UPDATE":
				stat.incrementUpdates();
				break;
			case "INSERT":
				stat.incrementInserts();
				break;
			case "DELETE":
				stat.incrementDeletes();
				break;
			}
		}

		//// joins
		List<Document> joinDocuments = new ArrayList<Document>();
		for (Join j : q.getAllJoins()) {
			String entity1 = j.getEntityName1();
			String entity2 = j.getEntityName2();

			EntityStats stat = entitiesStats.get(entity1);
			if (stat == null) {
				stat = new EntityStats(entity1);
				entitiesStats.put(entity1, stat);
			}
			stat.incrementSelects();

			stat = entitiesStats.get(entity2);
			if (stat == null) {
				stat = new EntityStats(entity2);
				entitiesStats.put(entity2, stat);
			}
			stat.incrementSelects();

			Document d = new Document();
			d.put("entity1", entity1);
			if (j.getAttributes1().size() > 0)
				d.put("rel1", j.getAttributes1().get(0));
			d.put("entity2", entity2);
			if (j.getAttributes2().size() > 0)
				d.put("rel2", j.getAttributes2().get(0));
			joinDocuments.add(d);

		}

		if (joinDocuments.size() > 0)
			query.put("joins", joinDocuments);

		List<Document> selDocuments = new ArrayList<Document>();
		for (AttributeSelector as : q.getAllAttributeSelectors()) {
			String entity = as.getEntityName();
			String attribute = as.getAttributes().get(0);
			String operator = "WHERE";
			Document sel = new Document();
			sel.put("entity", entity);
			sel.put("attribute", attribute);
			sel.put("operator", operator);
			selDocuments.add(sel);
		}

		if (selDocuments.size() > 0)
			query.put("selectors", selDocuments);

		//// implicit inserts
		Set<String> implicitInsertedEntities = new HashSet<String>();
		if (q.getQueryType().equals("INSERT"))
			for (Insert i : q.getInserts())
				stats(i, entitiesStats, implicitInsertedEntities);
		else
			for (Insert i : q.getInserts())
				stats2(i, entitiesStats, implicitInsertedEntities);

		List<String> implicitList = new ArrayList<String>(implicitInsertedEntities);
		if (implicitInsertedEntities.size() > 0)
			query.put("implicitInsertedEntities", implicitList);

		////////////////

		queryColl.insertOne(query);

		///////////////

		MongoCollection<Document> histColl = database.getCollection(TYPHON_ENTITY_HISTORY_COLLECTION);
		for (EntityStats stat : entitiesStats.values()) {

			logger.debug("Statistics for " + stat.getEntityName() + ":");
			if (stat.getSelects() > 0)
				logger.debug("selected");
			if (stat.getDeletes() > 0)
				logger.debug("deleted");
			if (stat.getInserts() > 0)
				logger.debug("inserted");
			if (stat.getUpdates() > 0)
				logger.debug("updated");

			String entityName = stat.getEntityName();
			Document history = getEntityHistory(histColl, entityName, q.getModel().getVersion());
			if (history != null) {
				BasicDBObject findObject = new BasicDBObject().append("_id", history.getObjectId("_id"));
				BasicDBObject updateObject = (BasicDBObject) JSON.parse("{ $inc: { nbOfQueries: 1 , nbOfSelect : "
						+ (stat.getSelects() > 0 ? 1 : 0) + " , nbOfInsert : " + (stat.getInserts() > 0 ? 1 : 0)
						+ " , nbOfUpdate : " + (stat.getUpdates() > 0 ? 1 : 0) + " , nbOfDelete : "
						+ (stat.getDeletes() > 0 ? 1 : 0) + " } }");
				histColl.findOneAndUpdate(findObject, updateObject);
			}

		}

	}

	private static void checkIfModelIsUpToDate() {
		TyphonModel.getCurrentModelWithStats(false);
		
	}

	private static Document getEntityHistory(MongoCollection<Document> coll, String entityName, int version) {

		BasicDBObject findObject = new BasicDBObject().append("name", entityName);
		BasicDBObject sortObject = new BasicDBObject().append("_id", -1);
		Document history = coll.find(findObject).sort(sortObject).limit(1).first();
		return history;
	}

	private static void stats2(Insert child, Map<String, EntityStats> entitiesStats,
			Set<String> implicitInsertedEntities) {
		String entity = child.getEntityName();
		implicitInsertedEntities.add(entity);
		EntityStats stat = entitiesStats.get(entity);
		if (stat == null) {
			stat = new EntityStats(entity);
			entitiesStats.put(entity, stat);
		}

		stat.incrementInserts();
		for (Insert c2 : child.getChildren())
			stats2(c2, entitiesStats, implicitInsertedEntities);
	}

	private static void stats(Insert i, Map<String, EntityStats> entitiesStats, Set<String> implicitInsertedEntities) {
		for (Insert child : i.getChildren()) {
			String entity = child.getEntityName();
			implicitInsertedEntities.add(entity);
			EntityStats stat = entitiesStats.get(entity);
			if (stat == null) {
				stat = new EntityStats(entity);
				entitiesStats.put(entity, stat);
			}

			stat.incrementInserts();
			stats(child, entitiesStats, implicitInsertedEntities);

		}

	}

}

class EntityStats {
	private String entityName;
	private int selects = 0;
	private int updates = 0;
	private int inserts = 0;
	private int deletes = 0;

	public EntityStats(String entityName) {
		this.entityName = entityName;
	}

	public String getEntityName() {
		return entityName;
	}

	public void incrementSelects() {
		selects++;
	}

	public void incrementUpdates() {
		updates++;
	}

	public void incrementInserts() {
		inserts++;
	}

	public void incrementDeletes() {
		deletes++;
	}

	public int getSelects() {
		return selects;
	}

	public int getUpdates() {
		return updates;
	}

	public int getInserts() {
		return inserts;
	}

	public int getDeletes() {
		return deletes;
	}

}
