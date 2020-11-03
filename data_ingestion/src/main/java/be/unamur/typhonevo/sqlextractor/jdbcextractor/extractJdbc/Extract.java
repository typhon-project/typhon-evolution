package be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.unamur.typhonevo.sqlextractor.conceptualschema.ConceptualSchema;

public class Extract {
	private static Logger logger = LoggerFactory.getLogger(Extract.class);

	private static final String MySQLDelimiter = "`";
	private Connection con = null;
	private DatabaseMetaData md = null;
	private String schemaName = null;
	private String catalogName = null;
	private String driver = null;
	private String url = null;
	private String login = null;
	private String password = null;

	private boolean isSQLITE = false;

	public Extract(String url, String login, String password, String driver, String catalogName, String schemaName)
			throws Exception {
		this.url = url;
		this.login = login;
		this.password = password;
		this.driver = driver;
		this.catalogName = catalogName;
		this.schemaName = schemaName;

		connect(driver, url, login, password);
		
	}

	public Connection getConnection() {
		return (con);
	}
	
	public static void main(String[] args) throws ClassNotFoundException {
		Class.forName("org.postgresql.Driver");
	}

	private void connect(String driver, String url, String login, String password) throws Exception {
		try {
			if (isSQLITE) {
				System.setProperty("sqlite.purejava", "true");
			}
			Class.forName(driver);
		} catch (ClassNotFoundException ex) {
			throw new Exception("Unable to load the driver: " + ex.getMessage()
					+ "\nModify your CLASSPATH global variable to add the JAR archive(s) corresponding to the required JDBC driver!");
		}
		try {
			con = DriverManager.getConnection(url, login, password);
		} catch (SQLException ex) {
			con = null;
			String message = "Impossible to connect " + url + "\n";
			while (ex != null) {
				message += ("SQLState: " + ex.getSQLState()) + "\n";
				message += ("Message:" + ex.getMessage()) + "\n";
				ex = ex.getNextException();
			}
			throw new Exception(message, ex);

		}
	}

	public static List<be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.Collection> extractDatabaseSchemas(
			List<DatabaseConnection> connections) throws Exception {
		List<be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.Collection> collections = new ArrayList<be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.Collection>();
		for (DatabaseConnection c : connections) {
			logger.info("connecting to " + c.getUrl() + " ...");
			Extract ex = new Extract(c.getUrl(), c.getUserName(), c.getPassword(), c.getDriver(), c.getCatalogName(),
					c.getSchemaName());
			logger.info("connected");
			logger.info("extracting schema ...");
			be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.Collection collection = ex.extractDb();
			markSplitColumns(collection, c);
			logger.info("schema extracted");
			collections.add(collection);
		}

		return collections;

	}

	private static void markSplitColumns(
			be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.Collection collection, DatabaseConnection c) throws Exception {
		if (c.getSplitDocuments().isEmpty())
			return;
		for (String column : c.getSplitDocuments()) {
			Column col = getColumn(column, collection);
			if (col == null)
				throw new Exception("Cannot find column '" + column + "' to split in document database (dbName="
						+ collection.getName() + ")");
			col.setSplit(true);
		}

	}

	private static Column getColumn(String column,
			be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.Collection collection) {
		int tableIndex = column.indexOf(".");
		if(tableIndex == -1)
			return null;
		
		String tableName = column.substring(0, tableIndex);
		String colName = column.substring(tableIndex + 1);
		Table t = collection.findTable(tableName);
		if(t == null)
			return null;
		return t.findColumn(colName);
		
		
//		String[] tab = column.split("\\.");
//		if(tab == null || tab.length != 2)
//			return null;
//		String tableName = tab[0];
//		String colName = tab[1];
//		Table t = collection.findTable(tableName);
//		if(t == null)
//			return null;
//		return t.findColumn(colName);
	}

	private be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.Collection extractDb() {
		be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.Collection res = null;
		try {
			md = con.getMetaData();
			String[] tableType = new String[1];
			tableType[0] = "TABLE";
			ResultSet rs = md.getTables(catalogName, schemaName, "%", tableType);
			res = new be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.Collection(schemaName, driver, url,
					login, password);
			extractTable(res, rs, catalogName, schemaName);
			rs.close();
			rs = md.getTables(catalogName, schemaName, "%", tableType);
			while (rs.next()) {
//				String curCatalogName = rs.getString(1);
//				schemaName = rs.getString(2);
				String tableName = rs.getString(3);
				if (tableName.indexOf("$") < 0) {
					extractFk(catalogName, schemaName, tableName, res.findTable(tableName), res);
				}
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return res;
	}

	private void extractTable(be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.Collection coll, ResultSet rs,
			String catalogName, String schemaName) throws SQLException {
		while (rs.next()) {
//			String catalogName = rs.getString(1);
//			String schemaName = rs.getString(2);
			String tableName = rs.getString(3);
			String desc = rs.getString(5);
			if (tableName.indexOf("$") >= 0) {
				continue;
			}
			Table table = new Table(tableName);
			coll.addTable(table);
			ExtractColumn(table, tableName);
			extractIndex(catalogName, schemaName, tableName, table);

		}
	}

	/*
	 * private static DBMCollection findCollection(DBMSchema sch, String name) {
	 * DBMCollection coll = sch.getFirstCollection(); while (coll != null) { if
	 * (coll.getName().equalsIgnoreCase(name)) return coll; coll =
	 * sch.getNextCollection(coll); } return null; }
	 */

	private void ExtractColumn(Table table, String tableName) throws SQLException {
		ResultSet rs = md.getColumns(catalogName, schemaName, tableName, "%");
		while (rs.next()) {
			String columnName = rs.getString(4);
			int columnType = rs.getInt(5);
			String columnTypeName = rs.getString(6);

			int columnSize = rs.getInt(7);
			int columnDecimal = rs.getInt(9);
			int columnNullable = rs.getInt(11);
			String desc = rs.getString(12);
			int columnMinCard = 0;
			if (columnNullable == DatabaseMetaData.columnNoNulls) {
				columnMinCard = 1;
			}
			
			String autoIncrementStr = rs.getString(23);
			boolean isAutoIncrement = autoIncrementStr != null && autoIncrementStr.equals("YES");

			Column column = new Column(columnName, columnType, columnSize, columnDecimal, columnMinCard, 1, isAutoIncrement);
			table.addColumn(column);

		}
		rs.close();
	}

	private void extractFk(String catalog, String schema, String tableName, Table fkTable,
			be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.Collection collection) throws SQLException {
		if (fkTable == null) {
			return;
		}
		Hashtable<String, FK> fkHash = new Hashtable<String, FK>();
		try {
			ResultSet rs = md.getImportedKeys(catalog, schema, tableName);
			while (rs.next()) {
				String fkName = rs.getString(12);
				if (fkName == null) {
					fkName = "??";
				}

				String pkTableName = rs.getString(3);
				FK curFk = fkHash.get(fkName);
				if (curFk == null) {
					Table pkTable = collection.findTable(pkTableName);
					curFk = new FK(fkName, fkTable, pkTable);
					fkHash.put(fkName, curFk);
				} else if (!pkTableName.equals(curFk.getPkTableName())) {
					continue;
				}
				String fkCol = rs.getString(8);
				String pkCol = rs.getString(4);
				int index = rs.getInt(9);
				curFk.addCol(index, fkCol, pkCol);
			}
			rs.close();
		} catch (NullPointerException e) {
			// nothing to do...
			e.printStackTrace();
		}

		Collection<FK> fkColl = fkHash.values();

		Iterator<FK> it = fkColl.iterator();
		while (it.hasNext()) {
			FK curFk = it.next();
			curFk.createFk();
		}

	}

	private void extractIndex(String catalog, String schema, String tableName, Table table) throws SQLException {
		ResultSet rs = md.getPrimaryKeys(catalog, schema, tableName);
		Map<Integer, GroupComponent> lComp = new HashMap<Integer, GroupComponent>();
		String grName = null;
		while (rs.next()) {
			grName = rs.getString(6);
			String colName = rs.getString(4);
			int colPos = rs.getInt(5);
			Column col = table.findColumn(colName);
			lComp.put(colPos, new GroupComponent(colPos, col));
		}
		rs.close();

		if (lComp.size() > 0) {
			getGroup(table, grName, new ArrayList<GroupComponent>(lComp.values()), true, null, null);
//			PrimaryKey pk = new PrimaryKey();
//			ListIterator<GroupComponent> it = lComp.listIterator();
//			while (it.hasNext()) {
//				GroupComponent comp = it.next();
//				pk.addColumn(comp.getComponent());
//			}
//
//			table.setPk(pk);

		}

		lComp = new HashMap<Integer, GroupComponent>();
		boolean secGrp = false;
		rs = md.getIndexInfo(catalog, schema, tableName, false, true);
		String curGrName = "";
		while (rs.next()) {
			int type = rs.getInt(7);
			if (type != DatabaseMetaData.tableIndexStatistic) {
				grName = rs.getString(6);
				if (!grName.equals(curGrName)) {
					if (!curGrName.isEmpty() && lComp.size() > 0) {
						getGroup(table, curGrName, new ArrayList<GroupComponent>(lComp.values()), null, secGrp, null);

					}
					secGrp = false;
					lComp = new HashMap<Integer, GroupComponent>();
					boolean nonUnique = rs.getBoolean(4);
					curGrName = grName;
					if (!nonUnique) {
						secGrp = true;
					}
				}
				String colName = rs.getString(9);
				Column col = table.findColumn(colName);
				if (col != null) {
					int colPos = rs.getInt(8);
					lComp.put(colPos, new GroupComponent(colPos, col));
				} else {
//					System.out.println("Error: column not found " + colName + ".");
				}
			}
		}
		if (lComp.size() > 0) {
			getGroup(table, curGrName, new ArrayList<GroupComponent>(lComp.values()), null, secGrp, null);
		}
		rs.close();
	}

	public static Group getGroup(Table table, String grName, List<GroupComponent> lComp, Boolean pk, Boolean unique,
			Boolean ref) {
		Collections.sort(lComp);

		Group gr = findSameGroup(table, lComp);

		if (unique != null && unique && gr != null && !gr.getName().equals(grName)) {
			// deux groupes uniques sur les memes composants veut forcement dire qu'il y a
			// un index sur ce grp
			gr.setKey(true);
		}

		if (gr == null) {
			if (grName == null) {
				grName = "Id";
			}

			gr = new Group(grName, pk, unique, ref);
			table.addGroup(gr);
			ListIterator<GroupComponent> it = lComp.listIterator();
			while (it.hasNext()) {
				GroupComponent comp = it.next();
				gr.addColumn(comp);
			}
		} else {

			gr.setPk(pk);
			gr.setUnique(unique);
			gr.setRef(ref);
		}

		boolean unknownConstraint = true;
		if (pk != null && pk)
			unknownConstraint = false;
		if (unique != null && unique)
			unknownConstraint = false;
		if (ref != null && ref)
			unknownConstraint = false;
		if (unknownConstraint) {
			gr.addUnknownConstraint(grName);
		}

		return gr;
	}

	public static Group findSameGroup(Table table, List<GroupComponent> lComp) {

		for (Group gr : table.getGroups()) {
			if (gr.getColumns().size() == lComp.size()) {
				boolean ok = true;
				for (GroupComponent gc : lComp) {
					Column c1 = gr.getColumn(gc.getPos());
					if (c1 != null) {
						Column c2 = gc.getComponent();
						if (!c1.getName().equals(c2.getName())) {
							ok = false;
							break;
						}
					} else {
						ok = false;
						break;
					}

				}

				if (ok)
					return gr;

			}
		}

		return null;
	}

	public String getSchemaName() {
		return schemaName;
	}

}
