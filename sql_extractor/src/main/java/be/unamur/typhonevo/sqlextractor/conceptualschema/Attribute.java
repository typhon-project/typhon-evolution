package be.unamur.typhonevo.sqlextractor.conceptualschema;

import java.io.Serializable;

import be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.Column;

public class Attribute  implements Serializable {
	public static final String STRING_TYPE = "String";
	public static final String INTEGER_TYPE = "Int";
	public static final String REAL_TYPE = "Real";
	public static final String DATE_TYPE = "Date";

	private Column column;
	
	

	public Attribute(Column column) {
		this.setColumn(column);
	}

	public String getName() {
		return column.getName();
	}

	public Column getColumn() {
		return column;
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	@Override
	public String toString() {
		return column.getName();
	}

	public static String getTyphonType(Column column) {
		String type = column.getAbstractType();

		switch (type) {
		case Column.BOO_ATT:
			return INTEGER_TYPE;
		case Column.CHAR_ATT:
			return STRING_TYPE;
		case Column.DATE_ATT:
			return DATE_TYPE;
		case Column.FLOAT_ATT:
			return REAL_TYPE;
		case Column.NUM_ATT:
			return INTEGER_TYPE;
		case Column.VARCHAR_ATT:
			return STRING_TYPE;
		default:
			return STRING_TYPE;
		}
	}

	public String getSplitTable() {
		return column.getSplitTable();
	}

	public void setSplitTable(String splitTable) {
		this.column.setSplitTable(splitTable);
	}

}
