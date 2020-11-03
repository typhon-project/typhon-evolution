package be.unamur.typhonevo.sqlextractor.qlextractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sound.sampled.DataLine;

import org.apache.spark.api.java.function.ForeachFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;

import be.unamur.typhonevo.sqlextractor.conceptualschema.Attribute;
import be.unamur.typhonevo.sqlextractor.conceptualschema.ConceptualSchema;
import be.unamur.typhonevo.sqlextractor.conceptualschema.EntityType;
import be.unamur.typhonevo.sqlextractor.conceptualschema.RelationshipType;
import be.unamur.typhonevo.sqlextractor.conceptualschema.Role;
import be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.Column;
import be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.GroupComponent;
import be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.Table;

public class TyphonQLGenerator implements Serializable {
	private static Logger logger = LoggerFactory.getLogger(TyphonQLGenerator.class);

	private static String outputDir;

	private static PrintWriter writerBegin = null;
	private static PrintWriter writerEnd = null;

	public static int BOUNDROWS_LIMIT = 2;
	private static int CURRENT_BOUNDROWS_NUMBER = 0;
	private static List<List<String>> CURRENT_BOUNDROWS_ARRAY;
	private static List<List<List<String>>> CURRENT_UPDATE_BOUNDROWS_ARRAY;
	private static int CURRENT_UPDATE_BOUNDROWS_NUMBER = 0;

	public static int MAX_NB_OF_QUERIES_PER_FILE = 500000;
	private static int FILE_COUNTER_BEGIN = 0;
	private static int FILE_COUNTER_END = 0;

	private static int QL_QUERY_BEGIN = 0;
	private static int QL_QUERY_END = 0;

	private Dataset<Row> dataset;
	private List<Column> columns;
	private List<Column> splitColumns;
	private List<Column> id = null;
	private String entityName;
	private String tmlEntityName;
	private List<RelationshipType> oneToMany;
	private List<RelationshipType> manyToMany;

	public TyphonQLGenerator(EntityType ent, ConceptualSchema sch) {
		Table table = ent.getTable();
		this.id = table.getPrimaryKey();

		this.entityName = table.getName();
		this.tmlEntityName = ent.getAdaptedMLName();
		this.columns = table.getColumnsNotPartOfFk();
		this.dataset = table.getDataset();
		oneToMany = sch.getOneToMany(ent);
		manyToMany = sch.getManyToManyBasedOnRole1(ent);

	}

	public static void generateQLScript(ConceptualSchema sch, String sqlDir) throws Exception {
		logger.info("extracting QL queries ...");
		TyphonQLGenerator.outputDir = sqlDir;

		PrintStream console2 = System.err;

		int i = 1;
		for (EntityType ent : sch.getEntityTypes().values()) {
			logger.info("[" + i + "/" + +sch.getEntityTypes().size() + "] reading table " + ent.getName() + " ... ");
			Table table = ent.getTable();
			Dataset<Row> dataset = table.getDataset();
			TyphonQLGenerator generator = new TyphonQLGenerator(ent, sch);
			Date d1 = new Date();
			generator.generateQLScriptForTable();
			generator.generateQLScriptForJoinTable();
			Date d2 = new Date();
			long ms = d2.getTime() - d1.getTime();
			logger.debug("nb of ms:" + ms);

			i++;
		}

		closePrinters();

		logger.info("QL queries extraction has finished with success");

	}

	private static void closePrinters() {
		if (writerEnd != null)
			try {
				writerEnd.close();
			} catch (Exception e) {
			}

		if (writerBegin != null)
			try {
				writerBegin.close();
			} catch (Exception e) {
			}
	}

	private static String getStringValue(Object o) {
		if (o == null)
			return null;

		if (o instanceof Date) {
			Date date = (java.util.Date) o;
//			2020-03-31T18:08:28.477+00:00
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			String res = sdf.format(date);
			return res;

		} else {
			String res = o.toString().replaceAll("\"", "\\\\\"");
			return res;
		}
	}
	
	private static String getPreparedStringValue(Object o, String mlType) {
		if (o == null) {
			if(mlType.equals(Attribute.DATE_TYPE) || mlType.equals(Attribute.DATETIME_TYPE)) {
				//at this moment, the QL server does not support insertion of null values for date fields
				// the workaround is to replace null values with default datetime
				String dateStr = "1900-01-01T00:00:00.000Z";
				try {
					o = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(dateStr);
				} catch (ParseException e) {
					e.printStackTrace();
					return null;
				}  
			} else			
				return null;
		}

		if (o instanceof Date) {
			Date date = (java.util.Date) o;
//			2020-03-31T18:08:28.477+00:00
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			String res = sdf.format(date);
			return res;

		} else {
//			String res = o.toString().replaceAll("\"", "\\\\\\\\\\\\\"");
			String res = o.toString().replaceAll("\"", "\\\\\"");
			return res;
		}
	}

	public static void main(String[] args) {
		
//		String test = "test\"ok";
		System.out.println(getPreparedStringValue(null, "date"));

//		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//		String res = sdf.format(new Date(0));
//		System.out.println(res);
//
//		List<List<String>> lists = new ArrayList<List<String>>();
//		List<String> t1 = new ArrayList<String>();
//		t1.add("t11");
//		t1.add("t111");
//
//		List<String> t2 = new ArrayList<String>();
//		t2.add("t21");
//		t2.add("t211");
//
//		lists.add(t1);
//		lists.add(t2);
//
//		System.out.println(lists);
	}

	private void generateQLScriptForTable() throws Exception {

		String preparedQueryTemp = "insert " + tmlEntityName + "{";
		List<String> parametersNames = new ArrayList<String>();
		List<String> parametersTypes = new ArrayList<String>();
		preparedQueryTemp += "@id:??UUID";
		parametersNames.add("\"UUID\"");
		parametersTypes.add("\"uuid\"");
		for (Column c : columns) {
			if (c.isTechnicalIdentifier())
				continue;

			String attrName = c.getName();
			preparedQueryTemp += "," + attrName + ":??" + attrName;
			parametersNames.add("\"" + attrName + "\"");
			String mltype = c.getMLType();
			if (mltype.startsWith("string"))
				mltype = "string";
			parametersTypes.add("\"" + mltype + "\"");
		}

		preparedQueryTemp += "}";

		final String preparedQuery = preparedQueryTemp;

		CURRENT_BOUNDROWS_ARRAY = new ArrayList<List<String>>();
//		for (int j = 0; j < parametersNames.size(); j++)
//			CURRENT_BOUNDROWS_ARRAY.add(new ArrayList<String>());

		//////////////////////////////////////////////

		List<String> preparedUpdateQueries = new ArrayList<String>();
		for (RelationshipType rel : oneToMany) {
			String updateQuery = "update " + tmlEntityName + " x where x.@id == ??UUID set {" + rel.getRole2().getTmlName()
					+ ": " + "??UUID2}";
			preparedUpdateQueries.add(updateQuery);
		}

		List<String> parametersNames2 = new ArrayList<String>();
		List<String> parametersTypes2 = new ArrayList<String>();
		parametersNames2.add("\"UUID\"");
		parametersNames2.add("\"UUID2\"");
		parametersTypes2.add("\"uuid\"");
		parametersTypes2.add("\"uuid\"");

		CURRENT_UPDATE_BOUNDROWS_ARRAY = new ArrayList<List<List<String>>>();
		for (int i = 0; i < oneToMany.size(); i++) {
			List<List<String>> subarray = new ArrayList<List<String>>();
			CURRENT_UPDATE_BOUNDROWS_ARRAY.add(subarray);
		}

		dataset.foreach(new ForeachFunction<Row>() {

			@Override
			public void call(Row r) throws Exception {
//				String ql = "insert " + entityName + "{";

				List<String> idValues = new ArrayList<String>();
				for (Column idCol : id) {
					Object o = r.getAs(idCol.getName());
					String val = getStringValue(o);
					idValues.add(val);
				}

				String uuid = UUIDGenerator.get(entityName, idValues);

//				ql += "@id: #" + uuid;

				List<String> row = new ArrayList<String>();
				row.add("\"" + uuid + "\"");

//				CURRENT_BOUNDROWS_ARRAY.get(0).add("\"#" + uuid + "\"");

				for (Column c : columns) {
					if (c.isTechnicalIdentifier())
						continue;
					String attrName = c.getName();
					Object o = r.getAs(c.getName());
					String val = getStringValue(o);
					String dataDelimiter = getDataDelimiter(c.getMLType());
					String preparedStatementDelimiter = getPreparedStatementDelimiter(c.getMLType());
//					ql += ", " + attrName + ": " + dataDelimiter + val + dataDelimiter;

					String preparedStringValue = getPreparedStringValue(o, c.getMLType());
					if (preparedStringValue != null)
						row.add("\"" + preparedStatementDelimiter + preparedStringValue
								+ preparedStatementDelimiter + "\"");
					else
						row.add(null);
				}

				CURRENT_BOUNDROWS_ARRAY.add(row);
				CURRENT_BOUNDROWS_NUMBER++;

				if (CURRENT_BOUNDROWS_NUMBER == BOUNDROWS_LIMIT) {
					printInsertPreparedStatement(preparedQuery, parametersNames, parametersTypes,
							CURRENT_BOUNDROWS_ARRAY);
					CURRENT_BOUNDROWS_NUMBER = 0;
				}

//				ql += "}";
//
//				writeQLQueryInTheBeginning(ql);

				int counter = 0;
				boolean atLeastOneAddedRow = false;
				for (RelationshipType rel : oneToMany) {
					String targetTable = rel.getTable1().getName();
					List<String> refValues = new ArrayList<String>();
					boolean refValuesAreNull = true;
					for (GroupComponent gc : rel.getFk().getFKGroup()) {
						String colName = gc.getComponent().getName();
						Object o = r.getAs(colName);
						String refValue = getStringValue(o);
						refValues.add(refValue);
						if (refValue != null)
							refValuesAreNull = false;
					}

					if (!refValuesAreNull) {
						String uuid2 = UUIDGenerator.get(targetTable, refValues);

//					String ql2 = "update " + entityName + " x where x.@id == #" + uuid + " set {"
//							+ rel.getRole2().getTmlName() + ": " + "#" + uuid2 + "}";
//
//					writeQLQueryAtTheEnd(ql2);

						List<List<String>> relArray = CURRENT_UPDATE_BOUNDROWS_ARRAY.get(counter);
						List<String> uuids = new ArrayList<String>();
						uuids.add("\"" + uuid + "\"");
						uuids.add("\"" + uuid2 + "\"");
						relArray.add(uuids);
						atLeastOneAddedRow = true;
					}

					counter++;
				}

				if (atLeastOneAddedRow) {
					CURRENT_UPDATE_BOUNDROWS_NUMBER++;

					if (CURRENT_UPDATE_BOUNDROWS_NUMBER == BOUNDROWS_LIMIT) {
						int j = 0;
						for (List<List<String>> subarray : CURRENT_UPDATE_BOUNDROWS_ARRAY) {
							printUpdatePreparedStatement(preparedUpdateQueries.get(j), parametersNames2,
									parametersTypes2, subarray);
							j++;
						}
						CURRENT_UPDATE_BOUNDROWS_NUMBER = 0;
					}
				}

			}

		});

		if (CURRENT_BOUNDROWS_NUMBER > 0) {
			printInsertPreparedStatement(preparedQuery, parametersNames, parametersTypes, CURRENT_BOUNDROWS_ARRAY);
			CURRENT_BOUNDROWS_NUMBER = 0;
		}

		if (CURRENT_UPDATE_BOUNDROWS_NUMBER > 0) {
			int j = 0;
			for (List<List<String>> subarray : CURRENT_UPDATE_BOUNDROWS_ARRAY) {
				printUpdatePreparedStatement(preparedUpdateQueries.get(j), parametersNames2, parametersTypes2,
						subarray);
				j++;
			}
			CURRENT_UPDATE_BOUNDROWS_NUMBER = 0;
		}

	}

	private void writePreparedStatementToFile(String preparedQuery, List<String> parametersNames,
			List<String> parametersTypes, List<List<String>> boundRows, boolean end) {
		if (boundRows.size() == 0)
			return;

		String preparedQL = "{\"query\":\"" + preparedQuery + "\",\"parameterNames\":" + parametersNames
				+ ",\"parameterTypes\":" + parametersTypes + ",\"boundRows\":" + boundRows + "}";

		if (end)
			writeQLQueryAtTheEnd(preparedQL);
		else
			writeQLQueryInTheBeginning(preparedQL);

		boundRows.clear();
//		for (int j = 0; j < parametersNames.size(); j++)
//			boundRows.add(new ArrayList<String>());
	}

	protected void printInsertPreparedStatement(String preparedQuery, List<String> parametersNames,
			List<String> parametersTypes, List<List<String>> boundRows) {
		writePreparedStatementToFile(preparedQuery, parametersNames, parametersTypes, boundRows, false);
	}

	protected void printUpdatePreparedStatement(String preparedQuery, List<String> parametersNames,
			List<String> parametersTypes, List<List<String>> boundRows) {
		writePreparedStatementToFile(preparedQuery, parametersNames, parametersTypes, boundRows, true);
	}

	protected String getPreparedStatementDelimiter(String mlType) {
		if (mlType.startsWith(Attribute.STRING_TYPE))
//			return "\\\"";
			return "";

		switch (mlType) {
		case Attribute.BIGINT_TYPE:
		case Attribute.BOOLEAN_TYPE:
		case Attribute.FLOAT_TYPE:
		case Attribute.INTEGER_TYPE:
			return "";
		case Attribute.TEXT_TYPE:
		case Attribute.STRING_TYPE:
//			return "\\\"";
			return "";
		case Attribute.BLOB_TYPE:
//			return "\\\"";
			// TODO
			return "";
		case Attribute.DATE_TYPE:
		case Attribute.DATETIME_TYPE:
//			return "$";
			return "";
		default:
			return "";
		}
	}

	private void generateQLScriptForJoinTable() {
		for (RelationshipType rel : manyToMany) {
			if (rel.isAlreadyHandled())
				continue;

			String entity1 = rel.getTable1().getName();
			String relName = rel.getRole2().getTmlName();
			Role srcRole = rel.getRole1();
			String entity2 = rel.getTable2().getName();
			Role dstRole = rel.getRole2();

			Table srcJoinTable = rel.getManyToManyTable();
			Dataset<Row> joinDataset = srcJoinTable.getDataset();

			/////////////////////////////////////

			final String preparedQuery = "update " + rel.getTable1().getAdaptedMLName() + " x where x.@id == ??UUID set {" + relName
					+ " +: ??UUID2}";
			List<String> parametersNames = new ArrayList<String>();
			List<String> parametersTypes = new ArrayList<String>();
			parametersNames.add("\"UUID\"");
			parametersNames.add("\"UUID2\"");
			parametersTypes.add("\"uuid\"");
			parametersTypes.add("\"uuid\"");

			CURRENT_BOUNDROWS_ARRAY = new ArrayList<List<String>>();
//			for (int j = 0; j < parametersNames.size(); j++)
//				CURRENT_BOUNDROWS_ARRAY.add(new ArrayList<String>());

			/////////////////////////////////////

			joinDataset.foreach(new ForeachFunction<Row>() {

				@Override
				public void call(Row r) throws Exception {
					List<String> srcValues = new ArrayList<String>();
					// src

					boolean hasNotNullSrcValues = false;
					for (GroupComponent gc : srcRole.getGroup().getColumns()) {
						String colName = gc.getComponent().getName();
						Object o = r.getAs(colName);
						String srcValue = getStringValue(o);
						srcValues.add(srcValue);
						if (srcValue != null)
							hasNotNullSrcValues = true;
					}

					// ref
					boolean hasNotNullRefValues = false;
					List<String> refValues = new ArrayList<String>();
					for (GroupComponent gc : dstRole.getGroup().getColumns()) {
						String colName = gc.getComponent().getName();
						Object o = r.getAs(colName);
						String refValue = getStringValue(o);
						refValues.add(refValue);
						if (refValue != null)
							hasNotNullRefValues = true;
					}

					if (hasNotNullRefValues && hasNotNullSrcValues) {
						String srcUUID = UUIDGenerator.get(entity1, srcValues);
						String refUUID = UUIDGenerator.get(entity2, refValues);

//					String ql = "update " + entity1 + " x where x.@id == #" + srcUUID + " set {" + relName + " +: #"
//							+ refUUID + "}";
//
//					writeQLQueryAtTheEnd(ql);

						List<String> row = new ArrayList<String>();
						row.add("\"" + srcUUID + "\"");
						row.add("\"" + refUUID + "\"");

						CURRENT_BOUNDROWS_ARRAY.add(row);

						CURRENT_BOUNDROWS_NUMBER++;

						if (CURRENT_BOUNDROWS_NUMBER == BOUNDROWS_LIMIT) {
							printUpdatePreparedStatement(preparedQuery, parametersNames, parametersTypes,
									CURRENT_BOUNDROWS_ARRAY);
							CURRENT_BOUNDROWS_NUMBER = 0;
						}
					}
				}
			});

			if (CURRENT_BOUNDROWS_NUMBER > 0) {
				printUpdatePreparedStatement(preparedQuery, parametersNames, parametersTypes, CURRENT_BOUNDROWS_ARRAY);
				CURRENT_BOUNDROWS_NUMBER = 0;
			}

			rel.setAlreadyHandled(true);
		}
	}

	protected String getDataDelimiter(String mlType) {
		if (mlType.startsWith(Attribute.STRING_TYPE))
			return "\"";

		switch (mlType) {
		case Attribute.BIGINT_TYPE:
		case Attribute.BOOLEAN_TYPE:
		case Attribute.FLOAT_TYPE:
		case Attribute.INTEGER_TYPE:
			return "";
		case Attribute.TEXT_TYPE:
		case Attribute.STRING_TYPE:
			return "\"";
		case Attribute.BLOB_TYPE:
			return "\"";
		case Attribute.DATE_TYPE:
		case Attribute.DATETIME_TYPE:
			return "$";
		default:
			return "";
		}
	}

	private static void writeQLQueryInTheBeginning(String query) {
		QL_QUERY_BEGIN++;
		if (QL_QUERY_BEGIN > MAX_NB_OF_QUERIES_PER_FILE) {
			QL_QUERY_BEGIN = 1;
			FILE_COUNTER_BEGIN++;

			try {
				writerBegin.close();
				writerBegin = null;
			} catch (Exception e) {

			}
		}

		String qlScript = outputDir + File.separator + "script.partA" + FILE_COUNTER_BEGIN + ".tql";
		try {
			if (writerBegin == null) {
				FileWriter fileWriter = new FileWriter(qlScript);
				writerBegin = new PrintWriter(fileWriter);
			}

			writerBegin.println(query);
		} catch (IOException e) {
			logger.error("cannot write to " + qlScript);
		}
	}

	private static void writeQLQueryAtTheEnd(String query) {
		QL_QUERY_END++;
		if (QL_QUERY_END > MAX_NB_OF_QUERIES_PER_FILE) {
			QL_QUERY_END = 1;
			FILE_COUNTER_END++;

			try {
				writerEnd.close();
				writerEnd = null;
			} catch (Exception e) {

			}
		}

		String qlScript = outputDir + File.separator + "script.partB" + FILE_COUNTER_END + ".tql";

		try {
			if (writerEnd == null) {
				FileWriter fileWriter = new FileWriter(qlScript);
				writerEnd = new PrintWriter(fileWriter);
			}

			writerEnd.println(query);
		} catch (IOException e) {
			logger.error("cannot write to " + qlScript);
		}

	}
}
