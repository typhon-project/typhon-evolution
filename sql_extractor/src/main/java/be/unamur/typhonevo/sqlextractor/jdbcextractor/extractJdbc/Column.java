package be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc;

import java.io.Serializable;
import java.sql.Types;

public class Column implements Serializable {

	public static final String NUM_ATT = "numeric";
	public static final String VARCHAR_ATT = "varchar";
	public static final String BOO_ATT = "boolean";
	public static final String CHAR_ATT = "char";
	public static final String DATE_ATT = "date";
	public static final String FLOAT_ATT = "float";

	private String name;
	private int columnType;
	private int columnSize;
	private int columnDecimal;
	private int columnMinCard;
	private int columnMaxCard;
	private boolean split = false;
	private String splitTable;
	private Table table;

	public Column(String columnName, int columnType, int columnSize, int columnDecimal, int columnMinCard,
			int columnMaxCard) {
		this.name = columnName;
		this.setColumnType(columnType);
		this.setColumnSize(columnSize);
		this.setColumnDecimal(columnDecimal);
		this.setColumnMinCard(columnMinCard);
		this.setColumnMaxCard(columnMaxCard);

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

	public String getAbstractType() {
		String dbmType = "";
		switch (columnType) {
		case Types.BIGINT:
			dbmType = NUM_ATT;
			break;
		case Types.BINARY:
			dbmType = NUM_ATT;
			break;
		case Types.BIT:
			dbmType = VARCHAR_ATT;
			break;
		case Types.BLOB:
			dbmType = VARCHAR_ATT;
			break;
		case Types.BOOLEAN:
			dbmType = BOO_ATT;
			break;
		case Types.CHAR:
			dbmType = CHAR_ATT;
			break;
		case Types.CLOB:
			dbmType = VARCHAR_ATT;
			break;
		case Types.DATE:
			dbmType = DATE_ATT;
			break;
		case Types.DECIMAL:
			dbmType = NUM_ATT;
			break;
		case Types.DOUBLE:
			dbmType = FLOAT_ATT;
			break;
		case Types.FLOAT:
			dbmType = FLOAT_ATT;
			break;
		case Types.INTEGER:
			dbmType = NUM_ATT;
			break;
		case Types.LONGNVARCHAR:
			dbmType = VARCHAR_ATT;
			break;
		case Types.LONGVARBINARY:
			dbmType = VARCHAR_ATT;
			break;
		case Types.LONGVARCHAR:
			dbmType = VARCHAR_ATT;
			break;
		case Types.NCHAR:
			dbmType = CHAR_ATT;
			break;
		case Types.NCLOB:
			dbmType = VARCHAR_ATT;
			break;
		case Types.NUMERIC:
			dbmType = NUM_ATT;
			break;
		case Types.NVARCHAR:
			dbmType = VARCHAR_ATT;
			break;
		case Types.REAL:
			dbmType = FLOAT_ATT;
			break;
		case Types.SMALLINT:
			dbmType = NUM_ATT;
			break;
		case Types.SQLXML:
			dbmType = VARCHAR_ATT;
			break;
		case Types.TIME:
			dbmType = DATE_ATT;
			break;
		case Types.TIMESTAMP:
			dbmType = DATE_ATT;
			break;
		case Types.TINYINT:
			dbmType = NUM_ATT;
			break;
		case Types.VARBINARY:
			dbmType = VARCHAR_ATT;
			break;
		case Types.VARCHAR:
			dbmType = VARCHAR_ATT;
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
			dbmType = VARCHAR_ATT;
			break;
		}

		return dbmType;

	}

	@Override
	public String toString() {
		return name + " " + getAbstractType() + "(" + getColumnSize() + ")" + " [" + columnMinCard + "," + columnMaxCard
				+ "]";
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
}
