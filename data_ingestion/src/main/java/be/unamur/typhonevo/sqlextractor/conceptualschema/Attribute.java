package be.unamur.typhonevo.sqlextractor.conceptualschema;

import java.io.Serializable;

import be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc.Column;

public class Attribute  implements Serializable {
	public static final String STRING_TYPE = "string";
	public static final String INTEGER_TYPE = "int";
	public static final String BIGINT_TYPE = "bigint";
	public static final String TEXT_TYPE = "text";
	public static final String BOOLEAN_TYPE = "bool";
	public static final String DATE_TYPE = "date";
	public static final String DATETIME_TYPE = "datetime";
	public static final String BLOB_TYPE = "blob";
	public static final String FLOAT_TYPE = "float";

	private Column column;
	private boolean technicalIdentifier = false;
	
	

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
		return column.getMLType();
	}

	public String getSplitTable() {
		return column.getSplitTable();
	}

	public void setSplitTable(String splitTable) {
		this.column.setSplitTable(splitTable);
	}

	public boolean isTechnicalIdentifier() {
		return technicalIdentifier;
	}

	public void setTechnicalIdentifier(boolean technicalIdentifier) {
		this.technicalIdentifier = technicalIdentifier;
		this.column.setTechnicalIdentifier(technicalIdentifier);
	}

}
