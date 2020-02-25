package sql_extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.WriteResult;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class PolyStoreFiller {
	private static Logger logger = LoggerFactory.getLogger(PolyStoreFiller.class);

	private static final String DEFAULT_PROPERTIES_FILE = System.getProperty("user.dir") + File.separator + "src"
			+ File.separator + "test" + File.separator + "resources" + File.separator + "config.properties";

	private static final String DEFAULT_INPUT_DIR = System.getProperty("user.dir") + File.separator + "/sql";
	private static final boolean DEV_MODE = true;

	private static String inputDir;
	private static String relationalDBURL = null;
	private static String relationalDBDriver = null;
	private static String relationalDBUserName = null;
	private static String relationDBPwd = null;

	private static String documentDBUrl = null;
	private static String documentDBName = null;

	private static final String RELATIONAL_DB_URL = "RELATIONAL_DB_URL";
	private static final String RELATIONAL_DB_DRIBER = "RELATIONAL_DB_DRIVER";
	private static final String RELATIONAL_DB_USERNAME = "RELATIONAL_DB_USERNAME";
	private static final String RELATIONAL_DB_PWD = "RELATIONAL_DB_PWD";

	private static final String DOCUMENT_DB_URL = "DOCUMENT_DB_URL_WITH_AUTH";
	private static final String DOCUMENT_DB_NAME = "DOCUMENT_DB_NAME";

	public static void main(String[] args) throws Throwable {
		if ((args == null || args.length < 2) && !DEV_MODE) {
			logger.error(
					"You must specify as arguments:\n 1) the path of your configuration file that contains the polystore credentials\n 2) the path of the directory that contains the SQL and JSON files to inject into the polystore");
			return;
		}

		String configProperties;
		if (args != null && args.length > 1) {
			configProperties = args[0];
			inputDir = args[1];
		} else {
			configProperties = DEFAULT_PROPERTIES_FILE;
			inputDir = DEFAULT_INPUT_DIR;
		}

		if (!new File(configProperties).exists()) {
			logger.error("Configuration file " + configProperties + " does not exist");
			return;
		}

		File dir = new File(inputDir);
		if (!dir.exists() || !dir.isDirectory()) {
			logger.error("Source directory " + inputDir + " does not exist");
			return;
		}

		parseConfigProperties(configProperties);

		logger.debug("Relational DB credentials:");
		logger.debug("url:" + relationalDBURL);
		logger.debug("driver:" + relationalDBDriver);
		logger.debug("username:" + relationalDBUserName);
		logger.debug("password:" + relationDBPwd);
		logger.debug("");
		logger.debug("Document DB credentials:");
		logger.debug("url:" + documentDBUrl);
		logger.debug("name:" + documentDBName);

		insertSQLQueryIntoPolyStore();
		insertJSONRowsIntoPolyStore();
		
		logger.info("Data injected into the polystore with success.");
	}

	private static void parseConfigProperties(String configProperties) throws IOException {
		Properties properties = new Properties();
		InputStream input;
		try {
			input = new FileInputStream(configProperties);
			properties.load(input);
			for (Entry<Object, Object> entry : properties.entrySet()) {
				String key = ((String) entry.getKey()).toUpperCase();
				String value = (String) entry.getValue();

				switch (key) {
				case RELATIONAL_DB_URL:
					relationalDBURL = value;
					break;
				case RELATIONAL_DB_DRIBER:
					relationalDBDriver = value;
					break;
				case RELATIONAL_DB_USERNAME:
					relationalDBUserName = value;
					break;
				case RELATIONAL_DB_PWD:
					relationDBPwd = value;
					break;
				case DOCUMENT_DB_URL:
					documentDBUrl = value;
					break;
				case DOCUMENT_DB_NAME:
					documentDBName = value;
					break;
				}

			}
		} catch (IOException e) {
			logger.error("Impossible to parse config file: " + configProperties);
			throw e;
		}

	}

	public static void insertJSONRowsIntoPolyStore() throws Throwable {
		File f = new File(inputDir);
		File[] files = f.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".json");
			}
		});
		logger.info("JSON files reading (" + files.length + " files) ...");
		for (int i = 0; i < files.length; i++) {
			logger.info("[" + (i + 1) + "/" + files.length + "]->" + files[i].getAbsoluteFile() + " ...");
			File file = files[i];
			try {
				injectJSON(file);
			} catch (Throwable e) {
				logger.error("Problem while reading: " + file.getAbsolutePath());
				throw e;
			}
		}

	}

	private static void injectJSON(File file) throws Throwable {
//		MongoClientURI uri = new MongoClientURI("mongodb://admin:admin@localhost:27018");

		MongoClientURI uri = new MongoClientURI(documentDBUrl);
		MongoClient mongoClient = new MongoClient(uri);
		MongoDatabase database = mongoClient.getDatabase(documentDBName);
		try {
			FileInputStream fis = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();

			String str = new String(data, "UTF-8");
			Document myDoc = Document.parse(str);
			String collectionName = myDoc.keySet().iterator().next();
			MongoCollection<Document> collection = database.getCollection(collectionName);

			List<Document> array = (List<Document>) myDoc.get(collectionName);
			collection.insertMany(array);

		} finally {
			mongoClient.close();
		}

	}

	public static void insertSQLQueryIntoPolyStore() throws Exception {
		File f = new File(inputDir);
		File[] files = f.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".sql");
			}
		});
		SQLQueryInjector.DB_URL = relationalDBURL;
		SQLQueryInjector.JDBC_DRIVER = relationalDBDriver;
		SQLQueryInjector.USER = relationalDBUserName;
		SQLQueryInjector.PASS = relationDBPwd;
		logger.info("SQL files reading  (" + files.length + " files) ...");
		for (int i = 0; i < files.length; i++) {
			logger.info("[" + (i + 1) + "/" + files.length + "]->" + files[i].getAbsoluteFile() + " ...");
			try {
				SQLQueryInjector.inject(files[i].getAbsolutePath());
			} catch (ClassNotFoundException | IOException | SQLException e) {
				logger.error("Problem while reading file: " + files[i].getAbsolutePath());
				throw e;
			}
		}

	}

}