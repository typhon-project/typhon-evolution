package be.unamur.typhonevo.sqlextractor.qlextractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.spark.api.java.function.ForeachFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import com.mongodb.BasicDBObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.RequestBuilder;
import com.sun.jersey.api.client.WebResource;

import be.unamur.typhonevo.sqlextractor.conceptualschema.Attribute;
import be.unamur.typhonevo.sqlextractor.conceptualschema.ConceptualSchema;
import be.unamur.typhonevo.sqlextractor.conceptualschema.EntityType;
import be.unamur.typhonevo.sqlextractor.conceptualschema.RelationshipType;
import be.unamur.typhonevo.sqlextractor.conceptualschema.Role;
import be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.Column;
import be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.GroupComponent;
import be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.Table;
import org.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TyphonSQLGenerator implements Serializable {
	private static Logger logger = LoggerFactory.getLogger(TyphonSQLGenerator.class);

	private static String outputDir;
	private static int SQL_QUERY_COUNTER = 0;
	private static int FILE_COUNTER = 0;
	public static int MAX_NB_OF_QUERIES_PER_FILE = 500000;

	public static int MAX_NB_OF_JSON_RECORDS_PER_FILE = 100000;
	private static Map<String, BasicDBObject> jsons = new HashMap<String, BasicDBObject>();
	private static Map<String, Integer> jsonCounter = new HashMap<String, Integer>();
	private static Map<String, String> jsonFiles = new HashMap<String, String>();
	private static Set<String> allJSONFiles = new HashSet<String>();

	private static String delimiter = "`";

	private Dataset<Row> dataset;
	private List<Column> columns;
	private List<Column> splitColumns;
	private List<Column> id = null;
	private String entityName;
	private List<RelationshipType> oneToMany;
	private List<RelationshipType> manyToMany;

	public TyphonSQLGenerator(EntityType ent, ConceptualSchema sch) {
		Table table = ent.getTable();
		this.id = table.getPrimaryKey();

		this.entityName = table.getName();
		this.columns = table.getColumnsNotPartOfFk();
		this.splitColumns = table.getSplitColumns();
		this.dataset = table.getDataset();
		oneToMany = sch.getOneToMany(ent);
		manyToMany = sch.getManyToManyBasedOnRole1(ent);

	}

	private static void writeJSONInsert(BasicDBObject o, String collectionName) throws Exception {
		BasicDBObject json = jsons.get(collectionName);
		if (json == null) {
			// first record for this collection
			json = new BasicDBObject();
			List<BasicDBObject> array = new ArrayList<BasicDBObject>();
			json.put(collectionName, array);
			jsons.put(collectionName, json);
			jsonCounter.put(collectionName, 0);
		}

		List<BasicDBObject> array = (List<BasicDBObject>) json.get(collectionName);
		array.add(o);
		if (array.size() >= MAX_NB_OF_JSON_RECORDS_PER_FILE) {
			// must write to file
			int fileCounter = jsonCounter.get(collectionName);
			String filePath = outputDir + File.separator + getFileName(collectionName) + ".part" + fileCounter
					+ ".json";

			try {

				PrintStream fileOut = new PrintStream(filePath);
				System.setErr(fileOut);
				System.err.println(json);
				fileCounter++;
				jsonCounter.put(collectionName, fileCounter);
				array.clear();
			} catch (FileNotFoundException e) {
				throw new Exception("Impossible to write to JSON script file: " + filePath);
			}

		}

	}

	private static String getFileName(String collectionName) {
		String fileName = jsonFiles.get(collectionName);
		if (fileName == null) {
			collectionName = collectionName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
			fileName = collectionName;
			int i = 0;
			while (allJSONFiles.contains(fileName)) {
				fileName = collectionName + "_" + i;
				i++;
			}

			jsonFiles.put(collectionName, fileName);
			allJSONFiles.add(fileName);
		}

		return fileName;
	}

	private static void lastJSONWrite() throws Exception {
		for (Entry<String, BasicDBObject> entry : jsons.entrySet()) {
			String collectionName = entry.getKey();
			BasicDBObject json = entry.getValue();
			List<BasicDBObject> array = (List<BasicDBObject>) json.get(collectionName);
			if (array.size() > 0) {
				// write
				int fileCounter = jsonCounter.get(collectionName);
				String filePath = outputDir + File.separator + getFileName(collectionName) + ".part" + fileCounter
						+ ".json";

				try {

					PrintStream fileOut = new PrintStream(filePath);
					System.setErr(fileOut);
					System.err.println(json);
				} catch (FileNotFoundException e) {
					throw new Exception("Impossible to write to JSON script file: " + filePath);
				}
			}
		}

		jsons.clear();
		jsonCounter.clear();
	}

	private static void writeSQLQuery(String query) throws Exception {
		SQL_QUERY_COUNTER++;
		if (SQL_QUERY_COUNTER > MAX_NB_OF_QUERIES_PER_FILE) {
			SQL_QUERY_COUNTER = 1;
			FILE_COUNTER++;
			String sqlScript = outputDir + File.separator + "script.part" + FILE_COUNTER + ".sql";
			try {

				PrintStream fileOut = new PrintStream(sqlScript);
				System.setOut(fileOut);
			} catch (FileNotFoundException e) {
				throw new Exception("Impossible to write to SQL script file: " + sqlScript);
			}
		}

		System.out.println(query);

	}

	public static void generateQLScript(ConceptualSchema sch, String sqlDir) throws Exception {
		logger.info("extracting SQL queries ...");
		TyphonSQLGenerator.outputDir = sqlDir;
		String sqlScript = outputDir + File.separator + "script.part" + FILE_COUNTER + ".sql";
		PrintStream console = System.out;
		try {

			PrintStream fileOut = new PrintStream(sqlScript);
			System.setOut(fileOut);
		} catch (FileNotFoundException e) {
			throw new Exception("Impossible to write to SQL script file: " + sqlScript);
		}

		PrintStream console2 = System.err;

		int i = 1;
		for (EntityType ent : sch.getEntityTypes().values()) {
			logger.info("[" + i + "/" + +sch.getEntityTypes().size() + "] reading table " + ent.getName() + " ... ");
			Table table = ent.getTable();
			Dataset<Row> dataset = table.getDataset();
			TyphonSQLGenerator generator = new TyphonSQLGenerator(ent, sch);
			Date d1 = new Date();
			generator.generateSQLScriptForTable();
			generator.generateSQLScriptForJoinTable();
			Date d2 = new Date();
			long ms = d2.getTime() - d1.getTime();
			logger.debug("nb of ms:" + ms);

			i++;
		}

		System.setOut(console);
		System.setErr(console2);

		logger.info("SQL queries extraction has finished with success");

	}

	private void generateSQLScriptForJoinTable() {
		for (RelationshipType rel : manyToMany) {
			if (rel.isAlreadyHandled())
				continue;

			String targetName = rel.getTable2().getName();
			Role srcRole = rel.getRole2();
			Role dstRole = rel.getRole1();
			String part1 = targetName + "." + srcRole.getTmlName() + "^";
			String part2 = entityName + "." + srcRole.getTmlName();

			String joinTable;
			if (part1.compareTo(part2) < 0) {
				joinTable = part1 + "-" + part2;
			} else {
				joinTable = part2 + "-" + part1;
			}

			Table srcJoinTable = rel.getManyToManyTable();
			Dataset<Row> joinDataset = srcJoinTable.getDataset();

			joinDataset.foreach(new ForeachFunction<Row>() {

				@Override
				public void call(Row r) throws Exception {
					List<String> srcValues = new ArrayList<String>();
					// src
					for (GroupComponent gc : srcRole.getGroup().getColumns()) {
						String colName = gc.getComponent().getName();
						Object o = r.getAs(colName);
						String srcValue = getStringValue(o);
						srcValues.add(srcValue);
					}

					String srcUUID = UUIDGenerator.get(targetName, srcValues);

					// ref
					List<String> refValues = new ArrayList<String>();
					for (GroupComponent gc : dstRole.getGroup().getColumns()) {
						String colName = gc.getComponent().getName();
						Object o = r.getAs(colName);
						String refValue = getStringValue(o);
						refValues.add(refValue);
					}

					String refUUID = UUIDGenerator.get(entityName, refValues);
					String sql = "INSERT INTO " + delimiter + joinTable + delimiter + " (" + delimiter + part2
							+ delimiter + ", " + delimiter + part1 + delimiter;

					sql += ") VALUES ('" + refUUID + "', '" + srcUUID + "');";
					writeSQLQuery(sql);
				}
			});

			rel.setAlreadyHandled(true);
		}
	}

	private static String getJSONValue(Object o) {
		if (o == null)
			return null;

		if (o instanceof Date) {
			Date date = (java.util.Date) o;
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String res = sdf.format(date);
			return res;

		} else {
			String res = o.toString().replaceAll("\"", "\\\\u0022");
			return res;
		}
	}

	private static String getStringValue(Object o) {
		if (o == null)
			return null;

		if (o instanceof Date) {
			Date date = (java.util.Date) o;
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String res = sdf.format(date);
			return res;

		} else {
			String res = o.toString().replaceAll("'", "''");
			return res;
		}
	}

	private void generateSQLScriptForTable() throws Exception {
//		dataset.printSchema();
		dataset.foreach(new ForeachFunction<Row>() {

			@Override
			public void call(Row r) throws Exception {
				String sql = "INSERT INTO " + delimiter + entityName + delimiter + "(" + delimiter
						+ getTargetPhysicalColumn(entityName, "@id") + delimiter;
				List<String> values = new ArrayList<String>();
				List<String> idValues = new ArrayList<String>();
				for (Column idCol : id) {
					Object o = r.getAs(idCol.getName());
					String val = getStringValue(o);
					idValues.add(val);
				}

				String uuid = UUIDGenerator.get(entityName, idValues);

				for (Column c : columns) {
					String colName = delimiter + getTargetPhysicalColumn(entityName, c.getName()) + delimiter;
					sql += ", " + colName;
					Object o = r.getAs(c.getName());
					String val = getStringValue(o);
					values.add(val);
				}
				sql += ") VALUES ('" + uuid + "'";

				for (int i = 0; i < columns.size(); i++) {
					String val = values.get(i);
					String valueWithDelimiter = getDelimiter(val, columns.get(i));
					sql += ", " + valueWithDelimiter;
				}

				sql += ");";
				writeSQLQuery(sql);

				///// one to many rels
				for (RelationshipType rel : oneToMany) {
					Role role = rel.getRole2();
					String targetTable = rel.getTable1().getName();
					String part1 = targetTable + "." + role.getTmlName() + "^";
					String part2 = entityName + "." + role.getTmlName();
					String joinTable;
					if (part1.compareTo(part2) < 0) {
						joinTable = part1 + "-" + part2;
					} else {
						joinTable = part2 + "-" + part1;
					}

					String srcColumn = part2;
					String refColumn = part1;

					List<String> refValues = new ArrayList<String>();
					for (GroupComponent gc : rel.getFk().getFKGroup()) {
						String colName = gc.getComponent().getName();
						Object o = r.getAs(colName);
						String refValue = getStringValue(o);
						refValues.add(refValue);
					}

					String refUuid = UUIDGenerator.get(targetTable, refValues);

					String sql2 = "INSERT INTO " + delimiter + joinTable + delimiter + "(" + delimiter + srcColumn
							+ delimiter + ", " + delimiter + refColumn + delimiter + ") VALUES ('" + uuid + "', '"
							+ refUuid + "');";

					writeSQLQuery(sql2);
				}

				// split columns
				for (Column split : splitColumns) {
					String splitTable = split.getSplitTable();
					Object o = r.getAs(split.getName());
					if (o != null) {
						if (o instanceof Date) {
							Date date = (java.util.Date) o;
							java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String value = sdf.format(date);
							o = value;
						}

						BasicDBObject json = new BasicDBObject();
						json.put("_id", UUIDGenerator.get(null, null));
						json.put(split.getName(), o);
						json.put(entityName, uuid);

//						String json = "{\"_id\" : \"" + UUIDGenerator.get(null, null) + "\", \"" + split.getName()
//								+ "\" : " + value + ", \"" + splitTable + "\" : \"" + uuid + "\" }";
						writeJSONInsert(json, splitTable);
					}

				}

			}

			private String getTargetPhysicalColumn(String entityName, String name) {
				return entityName + "." + name;
			}
		});

		lastJSONWrite();
	}

	private static String getDelimiter(Column column) {

		String delimiter = "";
		if (Attribute.getTyphonType(column).startsWith(Attribute.STRING_TYPE)
				|| Attribute.getTyphonType(column).equals(Attribute.DATE_TYPE)
				|| Attribute.getTyphonType(column).equals(Attribute.TEXT_TYPE)
				|| Attribute.getTyphonType(column).equals(Attribute.DATETIME_TYPE)
				|| Attribute.getTyphonType(column).equals(Attribute.BLOB_TYPE))
			delimiter = "'";
		return delimiter;
	}

	private static String getJSONDelimiter(Column column) {
		String delimiter = "";
		if (Attribute.getTyphonType(column).startsWith(Attribute.STRING_TYPE)
				|| Attribute.getTyphonType(column).equals(Attribute.DATE_TYPE)
				|| Attribute.getTyphonType(column).equals(Attribute.TEXT_TYPE)
				|| Attribute.getTyphonType(column).equals(Attribute.DATETIME_TYPE)
				|| Attribute.getTyphonType(column).equals(Attribute.BLOB_TYPE))
			delimiter = "\"";
		return delimiter;
	}

	private static String getDelimiter(String value, Column column) {
		if (value == null)
			return null;

		String delimiter = getDelimiter(column);
		return delimiter + value + delimiter;

	}

	private static String getJSONDelimiter(String value, Column column) {
		if (value == null)
			return null;

		String delimiter = getJSONDelimiter(column);
		return delimiter + value + delimiter;
	}

}
