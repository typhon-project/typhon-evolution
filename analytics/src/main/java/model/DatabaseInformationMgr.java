package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonValue;
import org.bson.Document;
import org.glassfish.jersey.client.ClientResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.MongoClientURI;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import nl.cwi.swat.typhonql.client.DatabaseInfo;
import typhonml.ColumnDB;
import typhonml.Database;
import typhonml.DocumentDB;
import typhonml.Entity;
import typhonml.GraphDB;
import typhonml.KeyValueDB;
import typhonml.RelationalDB;
import typhonmlreq.databaseType;

public class DatabaseInformationMgr {
	private static Logger logger = Logger.getLogger(TyphonModel.class);
	public static final String GET_DATABASES = "api/databases";
	public static final String GET_NOANALYTICS_QUERY = "api/noAnalytics/query";
	public static final String RELATIONALDB = "RELATIONALDB";
	public static final String DOCUMENTDB = "DOCUMENTDB";
	public static final String GRAPHDB = "GRAPHDB";
	public static final String COLUMNDB = "COLUMNDB";
	public static final String KEYVALUEDB = "KEYVALUEDB";

	public static String getDatatbaseType(Database database) {
		if (database != null) {
			if (database instanceof RelationalDB) {
				return RELATIONALDB;
			}
			if (database instanceof DocumentDB) {
				return DOCUMENTDB;
			}
			if (database instanceof GraphDB) {
				return GRAPHDB;

			}
			if (database instanceof ColumnDB) {
				return COLUMNDB;
			}
			if (database instanceof KeyValueDB) {
				return KEYVALUEDB;
			}
		}

		return null;
	}

	public static Map<String, Long> getCurrentModelWithStats(TyphonModel m, WebTarget target, String auth) {

		Map<String, Long> res = new HashMap<String, Long>();

		List<ConnectionInfo> infos = getDatabasesInfo(target, auth);

		try {

			for (Entity entity : m.getEntities()) {
				logger.info("counting entity " + entity.getName());
				Long nb = null;
				Database database = m.getEntityDatabase(entity.getName());
				if (database != null) {
					if (database instanceof RelationalDB) {
						RelationalDB rDB = (RelationalDB) database;
						nb = getNbOfRowsInRelationalTable(rDB, entity.getName(), infos);
					}
					if (database instanceof DocumentDB) {
						DocumentDB dDB = (DocumentDB) database;
						nb = getNbOfDocumentsInDocumentCollection(dDB, entity.getName(), infos);
					}
					if (database instanceof GraphDB) {
						GraphDB gDB = (GraphDB) database;
						nb = getNbOfXXXInGraphNode(gDB, entity.getName(), infos);

					}
					if (database instanceof ColumnDB) {
						ColumnDB cDB = (ColumnDB) database;
						nb = getNbOfXXXInColumn(cDB, entity.getName(), infos);
					}
					if (database instanceof KeyValueDB) {
						KeyValueDB kDB = (KeyValueDB) database;
						nb = getNbOfXXXInKeyValueElement(kDB, entity.getName(), infos);
					}
				}

				////////////////////////
				/// TO REMOVE

				// nb = new Long(new Random().nextInt(1000000));

				///////////////////////
				logger.info("res:" + nb);
				nb = nb == null ? 0 : nb;
				res.put(entity.getName(), nb);
			}
		} catch (Exception | Error e) {
			e.printStackTrace();
			throw e;
		} finally {
			closeConnections(infos);
		}

		return res;

	}

	private static void closeConnections(List<ConnectionInfo> infos) {

		for (ConnectionInfo info : infos) {
			if (info.getJDBCConn() != null) {
				try {
					info.getJDBCConn().close();
//					logger.info("Relational db connection closed:" + info.getDatabaseInfo().getDbName());
				} catch (Exception | Error e) {
				}
			}

			if (info.getMongoDBConn() != null) {
				try {
					info.getMongoDBConn().close();
//					logger.info("Document db connection closed:" + info.getDatabaseInfo().getDbName());
				} catch (Exception | Error e) {

				}
			}
		}

	}

	private static Long getNbOfXXXInKeyValueElement(KeyValueDB kDB, String name, List<ConnectionInfo> infos) {
		// TODO Auto-generated method stub
		return getCountEntity(name);
	}

	private static Long getNbOfXXXInColumn(ColumnDB cDB, String name, List<ConnectionInfo> infos) {
		// TODO Auto-generated method stub
		return getCountEntity(name);
	}

	private static Long getNbOfXXXInGraphNode(GraphDB gDB, String name, List<ConnectionInfo> infos) {
		// TODO Auto-generated method stub
		return getCountEntity(name);
	}

	private static Long getCountEntity(String entityName) {
		return TyphonModel.getEntityCount(entityName);

	}

	public static void main(String[] args) {
		TyphonModel.initWebService("http://168.119.234.158:4200", "admin", "admin1@");
		System.out.println(getCountEntity("ESPData"));
	}
	
	private static Long getNbOfDocumentsInDocumentCollection(DocumentDB dDB, String collectionName,
			List<ConnectionInfo> infos) {
		return getCountEntity(collectionName);
//		Long res = null;
//		for (ConnectionInfo info : infos) {
//			DatabaseInfo di = info.getDatabaseInfo();
//			if (di.getDbName().equals(dDB.getName())/** && di.getDbType() == DBType.documentdb **/
//			) {
//				MongoClient mongoClient = info.getMongoDBConn();
//
//				try {
//
//					if (mongoClient == null) {
//
//						String url = "mongodb://" + di.getUser() + ":" + di.getPassword() + "@" + di.getHost() + ":"
//								+ di.getPort();
//
//						MongoClientURI uri = new MongoClientURI(url);
//
//						mongoClient = new MongoClient(uri);
//						info.setMongoDBConn(mongoClient);
//					}
//
//					MongoDatabase database = mongoClient.getDatabase(dDB.getName());
//					res = database.getCollection(collectionName).estimatedDocumentCount();
//
//				} catch (Exception | Error e) {
//					// cannot execute mongo query
//				}
//
//				break;
//			}
//		}
//
//		return res;

	}

	private static Long getNbOfRowsInRelationalTable(RelationalDB rDB, String tableName, List<ConnectionInfo> infos) {
		return getCountEntity(tableName);
//		Long res = null;
//		for (ConnectionInfo info : infos) {
//			DatabaseInfo di = info.getDatabaseInfo();
//			if (di.getDbName().equals(rDB.getName())/** && di.getDbType() == DBType.relationaldb **/
//			) {
//				String ip = di.getHost();
//				String dbName = di.getDbName();
//				String dbms = di.getDbms();
//				int port = di.getPort();
//				String user = di.getUser();
//				String pwd = di.getPassword();
//
//				Statement stmt = null;
//				try {
//
//					Connection conn = info.getJDBCConn();
//					if (conn == null) {
//						String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
//						String DB_URL = "jdbc:mysql://" + ip + ":" + port + "/" + dbName;
//						Class.forName(JDBC_DRIVER);
//						conn = DriverManager.getConnection(DB_URL, user, pwd);
//						info.setJDBCConn(conn);
//					}
//
//					// STEP 4: Execute a query
//					stmt = conn.createStatement();
//					String sql = "SELECT COUNT(*) FROM `" + tableName + "`;";
//					ResultSet rs = stmt.executeQuery(sql);
//					rs.next();
//					res = rs.getLong(1);
//
//					stmt.close();
//				} catch (Exception | Error e) {
//					// cannot execute query
//					logger.error("Cannot get relational table size: " + tableName);
////					e.printStackTrace();
//				} finally {
//					if (stmt != null)
//						try {
//							stmt.close();
//						} catch (Exception | Error e) {
//
//						}
//				}
//
//				break;
//
//			}
//		}
//
//		return res;
	}

	public static List<ConnectionInfo> getDatabasesInfo(WebTarget webTarget, String authStringEnc) {
		List<ConnectionInfo> infos = new ArrayList<ConnectionInfo>();
		String json = getCurrentDatabasesInformation(webTarget, authStringEnc);
		if (json != null) {
			BsonArray array = BsonArray.parse(json);

			for (BsonValue v : array.getValues()) {
				BsonDocument d = v.asDocument();
				try {

					String type = d.getString("dbType").getValue();
					if (type == null)
						throw new RuntimeException(
								"Engine type " + d.getString("engineType").getValue() + " not known");
					ConnectionInfo info = new ConnectionInfo(null, d.getString("externalHost").getValue(),
							d.getNumber("externalPort").intValue(), d.getString("name").getValue(),
							d.getString("dbType").getValue(), d.getString("username").getValue(),
							d.getString("password").getValue());
					infos.add(info);
				} catch (BsonInvalidOperationException e) {
					// TODO not do anything if row of connection information is unparsable
				}
			}
		}

		return infos;
	}

	private static String getCurrentDatabasesInformation(WebTarget webTarget, String authStringEnc) {
		try {
			WebTarget target = webTarget.path(GET_DATABASES);
			String result = target.request(MediaType.APPLICATION_JSON).header("Authorization", "Basic " + authStringEnc)
					.get(String.class);
			return result;
		} catch (Exception | Error e) {
			logger.error("Impossible to load the current databases information");
			e.printStackTrace();
			return null;
		}
	}
	
	public static Long getCountEntity(WebTarget webTarget, String authStringEnc, String entityName) {
		try {
			logger.info("Trying to count nb of records in Entity: " + entityName);
			String query = "from " + entityName + " x select count(x.@id) as cnt";
			 WebTarget target = webTarget.path(GET_NOANALYTICS_QUERY);
			 logger.info("sending to WS server...");
			 javax.ws.rs.core.Response response = target
		                .request(MediaType.APPLICATION_JSON)
		                .header("Authorization", "Basic " + authStringEnc)
		                .post(javax.ws.rs.client.Entity.entity(query, MediaType.APPLICATION_JSON));
		        if (response.getStatus() != 200) {
		        	logger.error("Impossible to count the number of records in Entity " + entityName);
		        	return null;
		        }
		        logger.info("WS response received");
		        String result = response.readEntity(String.class);
		        JSONObject json = new JSONObject(result);
		        JSONArray attributesValues = json.getJSONArray("values");
		        JSONArray lengthArray = (JSONArray) attributesValues.get(0);
		        String length = lengthArray.getString(0);
		        Long res = Long.parseLong(length);
		        logger.info("Counting the number of records in Entity: " + entityName + ": " + res);
		        return res;
		} catch (Exception | Error e) {
			logger.error("Impossible to count the number of records in Entity " + entityName);
			e.printStackTrace();
			return null;
		}
	}

}
