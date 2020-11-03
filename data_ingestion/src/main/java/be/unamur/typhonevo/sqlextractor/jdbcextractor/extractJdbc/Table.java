package be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import be.unamur.typhonevo.sqlextractor.conceptualschema.EntityType;
import be.unamur.typhonevo.sqlextractor.conceptualschema.RelationshipType;
import be.unamur.typhonevo.sqlextractor.conceptualschema.Role;

public class Table implements Serializable {
	private String DEFAULT_FETCH_SIZE = "1000";
	private static SparkSession spark_ = null;

	private String name;
	private Collection collection;
	private Map<String, Column> columns = new LinkedHashMap<String, Column>();
	private Dataset<Row> dataset = null;

	private EntityType entityType;

	private List<Group> groups = new ArrayList<Group>();

	public Table(String name) {
		this.name = name;
	}

	public static SparkSession getSparkSession() {
		if (spark_ == null)
			spark_ = SparkSession.builder().appName("Java Spark SQL basic example").config("spark.master", "local")
					.getOrCreate();
		return spark_;

	}

	public void addGroup(Group group) {
		groups.add(group);
	}

	public void addColumn(Column c) {
		columns.put(c.getName(), c);
		c.setTable(this);
	}

	public String getName() {
		return name;
	}
	
	public String getAdaptedMLName() {
		return EntityType.getTmlEntityName(name);
	}

	public String getNameWithDBMSDelimiter(String driver) {
		String res = name;

		String qualifier = ExtractDialog.getDBMSDelimiter(driver);

		return qualifier + res + qualifier;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Column findColumn(String colName) {
		return columns.get(colName);
	}

	@Override
	public String toString() {
		String res = "---------\n";
		res += name + "\n";
		res += "---------\n";
		for (Column col : columns.values())
			res += col + "\n";

		res += "---------\n";

		for (Group gr : getGroups())
			res += gr + "\n";

		res += "---------\n";

		return res;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public List<Column> getColumnsNotPartOfFk() {
		List<Column> res = new ArrayList<Column>();
		for (Column col : columns.values()) {
			if (col.isSplit())
				continue;

			boolean partOfFk = false;
			for (Group g : groups)
				if (g.isRef() && g.contains(col)) {
					partOfFk = true;
					break;
				}

			if (!partOfFk)
				res.add(col);
		}

		return res;
	}

	public RelationshipType isManyToMany() {

		if (getGroups().size() != 3)
			return null;

		int primary = 0;
		int fks = 0;

		Group ref1 = null;
		Group ref2 = null;

		for (int i = 0; i < 3; i++) {
			Group g = getGroups().get(i);
			if (g.isPk())
				primary++;
			if (g.isRef()) {
				fks++;
				if (ref1 == null)
					ref1 = g;
				else
					ref2 = g;
			}

		}

		if (primary != 1 || fks != 2)
			return null;

		List<Column> cols = getColumnsNotPartOfFk();
		if (cols.size() > 0)
			return null;

		for (Table t : collection.getTables().values()) {
			if (t.getName().equals(name))
				continue;
			boolean ok = true;
			for (Group g : t.groups) {
				if (!g.isRef())
					continue;
				if (g.getFk().getPkTableName().equals(name)) {
					// there exists at least one table having a fk to the current table
					// => Thus, the table is not a many-to-many relationship
					ok = false;
					break;
				}
			}

			if (!ok)
				return null;

		}

		for (Column c : columns.values())
			if (c.getColumnMinCard() != 1 || c.getColumnMaxCard() != 1)
				return null;

		Table table1 = collection.getTables().get(ref1.getFk().getPkTableName());
		Table table2 = collection.getTables().get(ref2.getFk().getPkTableName());

		Role role1 = new Role(ref1, 0, 2);
		Role role2 = new Role(ref2, 0, 2);
//		EntityType e1 = new EntityType(table1);
//		EntityType e2 = new EntityType(table2);
//		System.out.println("e1 - e2:" + e1.getName() + "->" + e2.getName());
//		System.out.println(table1.getEntityType() + "=>" + table2.getEntityType());
		RelationshipType rel = new RelationshipType(table1.getEntityType(), table2.getEntityType(), role1, role2,
				ref1.getFk().getFkTable());

		return rel;

	}

	public Collection getCollection() {
		return collection;
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
	}

	public List<FK> getFks() {
		List<FK> res = new ArrayList<FK>();
		for (Group g : groups)
			if (g.isRef())
				res.add(g.getFk());
		return res;
	}

	public List<Group> getIds() {
		List<Group> res = new ArrayList<Group>();
		for (Group g : groups)
			if (g.isPk() || g.isUnique())
				res.add(g);
		return res;
	}

	public List<Group> getIndexes() {
		List<Group> res = new ArrayList<Group>();
		for (Group g : groups)
			if (g.isKey())
				res.add(g);
		return res;
	}

	public List<Column> getPrimaryKey() {
		List<Column> res = null;
		for (Group g : groups) {
			if (g.isPk()) {
				res = new ArrayList<Column>();
				for (int i = 0; i < g.getColumns().size(); i++)
					res.add(g.getColumns().get(i).getComponent());
				return res;
			}
		}

		for (Group g : groups)
			if (g.isUnique()) {
				res = new ArrayList<Column>();
				for (int i = 0; i < g.getColumns().size(); i++)
					res.add(g.getColumns().get(i).getComponent());
				return res;
			}

		if (res == null) {
			res = new ArrayList<Column>();
		}

		return res;

	}

	public Dataset<Row> getDataset() {
		if (dataset == null)
			dataset = createDataset();

		return dataset;
	}

	private Dataset<Row> createDataset() {
		String tableName = getNameWithDBMSDelimiter(collection.getDriver());
		if (collection.getName() != null)
			tableName = collection.getName() + "." + tableName;
		DataFrameReader dfr = getSparkSession().read().format("jdbc").option("url", collection.getUrl())
				.option("driver", collection.getDriver()).option("dbtable", tableName)
				.option("user", collection.getUser()).option("password", collection.getPassword());
		if (DEFAULT_FETCH_SIZE != null)
			dfr = dfr.option("fetchsize", DEFAULT_FETCH_SIZE);

		Dataset<Row> res = dfr.load();
		return res;

	}

	public EntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

	public List<Column> getSplitColumns() {
		List<Column> res = new ArrayList<Column>();
		for (Column col : columns.values()) {
			if (col.isSplit())
				res.add(col);
		}

		return res;
	}

}
