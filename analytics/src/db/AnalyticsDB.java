package db;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import com.mongodb.client.result.UpdateResult;
import com.mongodb.util.JSON;

import capture.mains.ConsumePostEvents;
import capture.mains.Insert;
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

	public static boolean initConnection(String ip, int port, String username, String password, String dbName) {

		try {

			String mongoUrl = "mongodb://" + username + ":" + password + "@" + ip + ":" + port;
			MongoClientURI uri = new MongoClientURI(mongoUrl);
			mongoClient = new MongoClient(uri);
			database = mongoClient.getDatabase(dbName);
			logger.info("Connected to Analytics DB");
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
			document.put("currentModelVersion", version);
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

		queryColl.insertOne(query);

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

		//// implicit inserts
		if (q.getQueryType().equals("INSERT"))
			for (Insert i : q.getInserts())
				stats(i, entitiesStats);
		else
			for (Insert i : q.getInserts())
				stats2(i, entitiesStats);

		////////////////

		MongoCollection<Document> histColl = database.getCollection(TYPHON_ENTITY_HISTORY_COLLECTION);
		for (EntityStats stat : entitiesStats.values()) {
			String entityName = stat.getEntityName();
			Document history = getEntityHistory(histColl, entityName, q.getModel().getVersion());
			if (history != null) {
				BasicDBObject findObject = new BasicDBObject().append("_id", history.getObjectId("_id"));
				BasicDBObject updateObject = (BasicDBObject) JSON.parse("{ $inc: { nbOfQueries: 1 , nbOfSelect : "
						+ stat.getSelects() + " , nbOfInsert : " + stat.getInserts() + " , nbOfUpdate : "
						+ stat.getUpdates() + " , nbOfDelete : " + stat.getDeletes() + " } }");
				histColl.findOneAndUpdate(findObject, updateObject);
			}
		}

	}

	private static Document getEntityHistory(MongoCollection<Document> coll, String entityName, int version) {

		BasicDBObject findObject = new BasicDBObject().append("name", entityName);
		BasicDBObject sortObject = new BasicDBObject().append("_id", -1);
		Document history = coll.find(findObject).sort(sortObject).limit(1).first();
		return history;
	}

	private static void stats2(Insert child, Map<String, EntityStats> entitiesStats) {
		String entity = child.getEntityName();
		EntityStats stat = entitiesStats.get(entity);
		if (stat == null) {
			stat = new EntityStats(entity);
			entitiesStats.put(entity, stat);
		}

		stat.incrementInserts();
		for (Insert c2 : child.getChildren())
			stats2(c2, entitiesStats);
	}

	private static void stats(Insert i, Map<String, EntityStats> entitiesStats) {
		for (Insert child : i.getChildren()) {
			String entity = child.getEntityName();
			EntityStats stat = entitiesStats.get(entity);
			if (stat == null) {
				stat = new EntityStats(entity);
				entitiesStats.put(entity, stat);
			}

			stat.incrementInserts();
			stats(child, entitiesStats);

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
