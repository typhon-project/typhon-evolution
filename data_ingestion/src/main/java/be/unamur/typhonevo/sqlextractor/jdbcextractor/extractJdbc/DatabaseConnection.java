package be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatabaseConnection {
	
	private String url;
	private String driver;
	private String catalogName;
	private String schemaName;
	private String userName;
	private String password;
	private Set<String> splitDocuments = new HashSet<String>();
	
	public DatabaseConnection(String url, String driver, String catalogName, String schemaName, String userName, String password) {
		this.url = url;
		this.driver = driver;
		this.catalogName = catalogName;
		this.schemaName = schemaName;
		this.userName = userName;
		this.password = password;
	}
	
	public void addDocumentSplits(String split) {
		splitDocuments.add(split);
	}
	
	public Set<String> getSplitDocuments() {
		return splitDocuments;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getCatalogName() {
		return catalogName;
	}
	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}
	public String getSchemaName() {
		return schemaName;
	}
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

}
