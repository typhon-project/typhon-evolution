package be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc;

import java.util.LinkedHashMap;

public class Index {
	
	private String name;
	private LinkedHashMap<String, Column> columns = new LinkedHashMap<String, Column>();

	public Index(String name) {
		this.name = name;
	}
	
	public void addColumn(Column column) {
		columns.put(column.getName(), column);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
