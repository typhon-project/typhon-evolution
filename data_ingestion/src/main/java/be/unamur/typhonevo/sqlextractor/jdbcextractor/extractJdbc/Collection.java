package be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Collection  implements Serializable { 
	
	private String driver;
	private String url;
	private String user;
	private String password;
	
	private String name;
	private Map<String, Table> tables = new HashMap<String, Table>();
	
	public Collection(String name, String driver, String url, String user, String password) {
		this.name = name;
		this.driver = driver;
		this.url = url;
		this.user = user;
		this.password = password;
	}
	
	public void addTable(Table table) {
		tables.put(table.getName(), table);
		table.setCollection(this);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<String, Table> getTables() {
		return tables;
	}
	public void setTables(Map<String, Table> tables) {
		this.tables = tables;
	}

	public Table findTable(String tableName) {
		return tables.get(tableName);
	}
	
	@Override
	public String toString() {
		String res = ("DB:" + name + ", #tables: " + tables.size()) + "\n";
		for(Table table : tables.values()) {
			res += (table) + "\n";
		}
		
		return res;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
	

}
