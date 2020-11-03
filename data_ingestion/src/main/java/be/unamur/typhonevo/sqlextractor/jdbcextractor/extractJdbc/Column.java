package be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc;

import java.io.Serializable;
import java.sql.Types;

import be.unamur.typhonevo.sqlextractor.conceptualschema.Attribute;

public class Column implements Serializable {

	private String name;
	private int columnType;
	private int columnSize;
	private int columnDecimal;
	private int columnMinCard;
	private int columnMaxCard;
	private boolean autoIncrement;
	private boolean technicalIdentifier = false;
	private boolean split = false;
	private String splitTable;
	private Table table;

	private String mlType = null;

	public Column(String columnName, int columnType, int columnSize, int columnDecimal, int columnMinCard,
			int columnMaxCard, boolean autoIncrement) {
		this.name = columnName;
		this.setColumnType(columnType);
		this.setColumnSize(columnSize);
		this.setColumnDecimal(columnDecimal);
		this.setColumnMinCard(columnMinCard);
		this.setColumnMaxCard(columnMaxCard);
		this.setAutoIncrement(autoIncrement);

	}

	public String getName() {
		return name;
	}

	public String getNameWithDBMSDelimiter() {
		String res = name;

		String driver = table.getCollection().getDriver();
		String qualifier = ExtractDialog.getDBMSDelimiter(driver);

		return qualifier + res + qualifier;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getColumnType() {
		return columnType;
	}

	public void setColumnType(int columnType) {
		this.columnType = columnType;
	}

	public int getColumnSize() {
		return columnSize;
	}

	public void setColumnSize(int columnSize) {
		this.columnSize = columnSize;
	}

	public int getColumnDecimal() {
		return columnDecimal;
	}

	public void setColumnDecimal(int columnDecimal) {
		this.columnDecimal = columnDecimal;
	}

	public int getColumnMinCard() {
		return columnMinCard;
	}

	public void setColumnMinCard(int columnMinCard) {
		this.columnMinCard = columnMinCard;
	}

	public int getColumnMaxCard() {
		return columnMaxCard;
	}

	public void setColumnMaxCard(int columnMaxCard) {
		this.columnMaxCard = columnMaxCard;
	}

	public String getMLType() {

		if (mlType == null) {
			String dbmType = "";
			int columnSize = this.columnSize > 0 ? this.columnSize : 1;

			// cannot create string[1] attribute at this moment
			columnSize = Math.max(2, columnSize);

			switch (columnType) {
			case Types.BIGINT:
				dbmType = Attribute.BIGINT_TYPE;
				break;
			case Types.BINARY:
				dbmType = Attribute.INTEGER_TYPE;
				break;
			case Types.BIT:
				dbmType = Attribute.STRING_TYPE + "[" + Math.max(columnSize, 5) + "]";
				break;
			case Types.BLOB:
				dbmType = Attribute.BLOB_TYPE;
				break;
			case Types.BOOLEAN:
				dbmType = Attribute.BOOLEAN_TYPE;
				break;
			case Types.CHAR:
				dbmType = Attribute.STRING_TYPE + "[" + columnSize + "]";
				break;
			case Types.CLOB:
				dbmType = Attribute.BLOB_TYPE;
				break;
			case Types.DATE:
				dbmType = Attribute.DATE_TYPE;
				break;
			case Types.DECIMAL:
				dbmType = Attribute.FLOAT_TYPE;
				break;
			case Types.DOUBLE:
				dbmType = Attribute.FLOAT_TYPE;
				break;
			case Types.FLOAT:
				dbmType = Attribute.FLOAT_TYPE;
				break;
			case Types.INTEGER:
				dbmType = Attribute.INTEGER_TYPE;
				break;
			case Types.LONGNVARCHAR:
				dbmType = Attribute.TEXT_TYPE;
				break;
			case Types.LONGVARBINARY:
				dbmType = Attribute.TEXT_TYPE;
				break;
			case Types.LONGVARCHAR:
				dbmType = Attribute.TEXT_TYPE;
				break;
			case Types.NCHAR:
				dbmType = Attribute.STRING_TYPE + "[" + columnSize + "]";
				break;
			case Types.NCLOB:
				dbmType = Attribute.BLOB_TYPE;
				break;
			case Types.NUMERIC:
				if (columnDecimal > 0)
					dbmType = Attribute.FLOAT_TYPE;
				else
					dbmType = Attribute.INTEGER_TYPE;
				break;
			case Types.NVARCHAR:
				dbmType = Attribute.STRING_TYPE + "[" + columnSize + "]";
				break;
			case Types.REAL:
				dbmType = Attribute.FLOAT_TYPE;
				break;
			case Types.SMALLINT:
				dbmType = Attribute.INTEGER_TYPE;
				break;
			case Types.SQLXML:
				dbmType = Attribute.TEXT_TYPE;
				break;
			case Types.TIME:
				dbmType = Attribute.DATETIME_TYPE;
				break;
			case Types.TIMESTAMP:
				dbmType = Attribute.DATETIME_TYPE;
				break;
			case Types.TINYINT:
				dbmType = Attribute.INTEGER_TYPE;
				break;
			case Types.VARBINARY:
				dbmType = Attribute.STRING_TYPE + "[" + columnSize + "]";
				break;
			case Types.VARCHAR:
				dbmType = Attribute.STRING_TYPE + "[" + columnSize + "]";
				break;
			/*
			 * case Types.ARRAY : dbmType = ' '; break; case Types.DATALINK : dbmType = ' ';
			 * break; case Types.DISTINCT : dbmType = ' '; break; case Types.JAVA_OBJECT :
			 * dbmType = ' '; break; case Types.NULL : dbmType = ' '; break; case
			 * Types.OTHER : dbmType = ' '; break; case Types.REF : dbmType = ' '; break;
			 * case Types.ROWID : dbmType = ' '; break; case Types.STRUCT : dbmType = ' ';
			 * break;
			 */
			default:
				dbmType = Attribute.STRING_TYPE + "[" + columnSize + "]";
				break;
			}
			
			mlType = dbmType;

		}

		return mlType;
	}

	@Override
	public String toString() {
		return name + " " + columnType + "(" + getColumnSize() + ")" + " [" + columnMinCard + "," + columnMaxCard + "]";
	}

	public boolean isSplit() {
		return split;
	}

	public void setSplit(boolean split) {
		this.split = split;
	}

	public String getSplitTable() {
		return splitTable;
	}

	public void setSplitTable(String splitTable) {
		this.splitTable = splitTable;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public boolean isTechnicalIdentifier() {
		return technicalIdentifier;
	}

	public void setTechnicalIdentifier(boolean technicalIdentifier) {
		this.technicalIdentifier = technicalIdentifier;
	}
}
