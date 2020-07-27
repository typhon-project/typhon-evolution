import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.TreeMap;

import be.unamur.typhonevo.sqlextractor.conceptualschema.ConceptualSchema;
import be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.Collection;
import be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.DatabaseConnection;
import be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.Extract;
import be.unamur.typhonevo.sqlextractor.qlextractor.TyphonQLGenerator;
import be.unamur.typhonevo.sqlextractor.qlextractor.TyphonSQLGenerator;

public class TMLExtractor {
	private static Logger logger = LoggerFactory.getLogger(TMLExtractor.class);
	private static final String TML_FILE_NAME = "schema.tml";
	private static final String QL_DIR_NAME = "data";

	public static boolean DEV_MODE = true;
	private static final String DEV_MODE_CONFIG_PROPERTIES = "C:\\Users\\lmeurice\\Documents\\WP6.5\\typhon-evolution\\sql_extractor\\src\\main\\resources\\files_to_copy\\extract.properties";
	private static final String DEV_MODE_OUTPUT_DIR = System.getProperty("user.dir");
	
	private static final String URL_FIELD = "URL".toUpperCase();
	private static final String DRIVER_FIELD = "DRIVER".toUpperCase();
	private static final String PASSWORD_FIELD = "PASSWORD".toUpperCase();
	private static final String SCHEMA_FIELD = "SCHEMA".toUpperCase();
	private static final String USERNAME_FIELD = "USER".toUpperCase();
	private static final String CATALOG_FIELD = "CATALOG".toUpperCase();
//	private static final String DOCUMENT_SPLIT_FIELD = "DOCUMENT_SPLIT".toUpperCase();
	private static final String MAX_QL_QUERIES_PER_FILE_FIELD = "MAX_QL_QUERIES_PER_FILE".toUpperCase();
//	private static final String MAX_SQL_QUERIES_PER_FILE_FIELD = "MAX_SQL_QUERIES_PER_FILE".toUpperCase();
//	private static final String MAX_JSON_RECORDS_PER_FILE_FIELD = "MAX_JSON_RECORDS_PER_FILE".toUpperCase();
	private static final String PREPARED_STATEMENTS_BOUND_ROWS = "PREPARED_STATEMENTS_BOUND_ROWS".toUpperCase();

	public static void main(String[] args) {
		try {
			String configProperties = null;
			String outputDir = null;

			if (args != null && args.length == 2) {
				configProperties = args[0];
				outputDir = args[1];
				logger.debug("Config properties: " + configProperties);
				logger.debug("Output directory: " + outputDir);
			} else if (!DEV_MODE) {
				throw new Exception("Error: the configuration file path and the output directory path are required as arguments");
			} else {
				configProperties = DEV_MODE_CONFIG_PROPERTIES;
				outputDir = DEV_MODE_OUTPUT_DIR;
			}
			
			File output = new File(outputDir);
			if(!output.exists() || !output.isDirectory()) {
				throw new Exception("The output directory " + outputDir + " does not exist");
			}
			
			String tmlOutputFile = outputDir + File.separator + TML_FILE_NAME;
			String qlOutputDir = outputDir + File.separator + QL_DIR_NAME;
			File qlDir = new File(qlOutputDir);
			if (!qlDir.exists() || !qlDir.isDirectory()) {
				boolean c = qlDir.mkdir();
				if (!c)
					throw new Exception("Impossible to create directory " + qlOutputDir);
			}

			List<DatabaseConnection> connections;

			logger.debug("TML output:" + tmlOutputFile);
			logger.debug("TQL script output:" + qlOutputDir);
			connections = new TMLExtractor().getDatabaseConnections(configProperties);
			List<Collection> collections = Extract.extractDatabaseSchemas(connections);
			ConceptualSchema sch = ConceptualSchema.transform(collections);
			logger.info("");
			logger.info("writing results to file: " + tmlOutputFile + " ...");
			sch.printTyphonML(tmlOutputFile);
			logger.info("TML extraction has finished with success");
			logger.info("output configuration:\n   - max nb of ql queries per file:"
					+ TyphonQLGenerator.MAX_NB_OF_QUERIES_PER_FILE + "\n   - max nb of bound rows in prepared statements:" + TyphonQLGenerator.BOUNDROWS_LIMIT);
			TyphonQLGenerator.generateQLScript(sch, qlOutputDir);
			logger.info("QL Extracted");
		} catch (Exception e) {
//			e.printStackTrace();
			logger.error(e.getMessage());
			return;
		}

	}

	private List<DatabaseConnection> getDatabaseConnections(String configProperties) throws Exception {
		Properties properties = new Properties();
		try {
			InputStream input = new FileInputStream(configProperties);
			properties.load(input);

			TreeMap<String, DatabaseConnection> connectionMap = new TreeMap<String, DatabaseConnection>();

			for (Entry<Object, Object> entry : properties.entrySet()) {
				String key = ((String) entry.getKey()).toUpperCase();
				String value = (String) entry.getValue();
				
				if(key.equals(PREPARED_STATEMENTS_BOUND_ROWS)) {
					try {
						int boundRows = Integer.parseInt(value);
						TyphonQLGenerator.BOUNDROWS_LIMIT = boundRows;
					} catch (Exception | Error e) {
						logger.error("Impossible to cast PREPARED_STATEMENTS_BOUND_ROWS value into integer: " + value);
					}
				}
				
				if(key.equals(MAX_QL_QUERIES_PER_FILE_FIELD)) {
					try {
						int maxQueries = Integer.parseInt(value);
						TyphonQLGenerator.MAX_NB_OF_QUERIES_PER_FILE = maxQueries;
					} catch (Exception | Error e) {
						logger.error("Impossible to cast MAX_QL_QUERIES_PER_FILE_FIELD value into integer: " + value);
					}
				}

//				if (key.startsWith(MAX_SQL_QUERIES_PER_FILE_FIELD)) {
//					try {
//						int maxSQL = Integer.parseInt(value);
//						TyphonSQLGenerator.MAX_NB_OF_QUERIES_PER_FILE = maxSQL;
//					} catch (Exception | Error e) {
//						logger.error("Impossible to cast MAX_SQL_QUERIES_PER_FILE value into integer: " + value);
//					}
//				}
//
//				if (key.startsWith(MAX_JSON_RECORDS_PER_FILE_FIELD)) {
//					try {
//						int maxJSON = Integer.parseInt(value);
//						TyphonSQLGenerator.MAX_NB_OF_JSON_RECORDS_PER_FILE = maxJSON;
//					} catch (Exception | Error e) {
//						logger.error("Impossible to cast MAX_JSON_RECORDS_PER_FILE value into integer: " + value);
//					}
//				}

				if (key.startsWith(URL_FIELD)) {
					// URL
					String suffix = key.replace(URL_FIELD, "");
					DatabaseConnection conn = connectionMap.get(suffix);
					if (conn == null) {
						conn = new DatabaseConnection(null, null, null, null, null, null);
						connectionMap.put(suffix, conn);
					}
					conn.setUrl(value);
					continue;
				}

				if (key.startsWith(DRIVER_FIELD)) {
					// DRIVER
					String suffix = key.replace(DRIVER_FIELD, "");
					DatabaseConnection conn = connectionMap.get(suffix);
					if (conn == null) {
						conn = new DatabaseConnection(null, null, null, null, null, null);
						connectionMap.put(suffix, conn);
					}
					conn.setDriver(value);
					continue;
				}

				if (key.startsWith(PASSWORD_FIELD)) {
					// PWD
					String suffix = key.replace(PASSWORD_FIELD, "");
					DatabaseConnection conn = connectionMap.get(suffix);
					if (conn == null) {
						conn = new DatabaseConnection(null, null, null, null, null, null);
						connectionMap.put(suffix, conn);
					}
					conn.setPassword(value);
					continue;
				}

				if (key.startsWith(USERNAME_FIELD)) {
					// USER
					String suffix = key.replace(USERNAME_FIELD, "");
					DatabaseConnection conn = connectionMap.get(suffix);
					if (conn == null) {
						conn = new DatabaseConnection(null, null, null, null, null, null);
						connectionMap.put(suffix, conn);
					}
					conn.setUserName(value);
					continue;
				}

				if (key.startsWith(SCHEMA_FIELD)) {
					// SCHEMA
					String suffix = key.replace(SCHEMA_FIELD, "");
					DatabaseConnection conn = connectionMap.get(suffix);
					if (conn == null) {
						conn = new DatabaseConnection(null, null, null, null, null, null);
						connectionMap.put(suffix, conn);
					}
					conn.setSchemaName(value);
					continue;
				}

				if (key.startsWith(CATALOG_FIELD)) {
					// CATALOG
					String suffix = key.replace(CATALOG_FIELD, "");
					DatabaseConnection conn = connectionMap.get(suffix);
					if (conn == null) {
						conn = new DatabaseConnection(null, null, null, null, null, null);
						connectionMap.put(suffix, conn);
					}
					conn.setCatalogName(value);
					continue;
				}

//				if (key.startsWith(DOCUMENT_SPLIT_FIELD)) {
//					// DOCUMENT_SPLIT
//					String suffix = key.replace(DOCUMENT_SPLIT_FIELD, "");
//					DatabaseConnection conn = connectionMap.get(suffix);
//					if (conn == null) {
//						conn = new DatabaseConnection(null, null, null, null, null, null);
//						connectionMap.put(suffix, conn);
//					}
//					String[] values = value.split(",");
//					for (String v : values)
//						conn.addDocumentSplits(v);
//					continue;
//				}

			}

			List<DatabaseConnection> connections = new ArrayList<DatabaseConnection>(connectionMap.values());

			if (connections.size() == 0)
				throw new Exception("The credentials of at least one database are required in the config file");

			return connections;

		} catch (IOException e) {
			throw new Exception("Impossible to open the config file: " + configProperties);
		}
	}

}