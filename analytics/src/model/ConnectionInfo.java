package model;

import java.sql.Connection;

import com.mongodb.MongoClient;

import nl.cwi.swat.typhonql.DBType;
import nl.cwi.swat.typhonql.client.DatabaseInfo;

public class ConnectionInfo extends nl.cwi.swat.typhonql.ConnectionInfo {
	public ConnectionInfo(String polystoreId, String host, int port, String dbName, DBType dbType, String dbms,
			String user, String password) {
		super(polystoreId, host, port, dbName, dbType, dbms, user, password);
	}

	private Connection JDBCConn = null;
	private MongoClient MongoDBConn = null;

	public Connection getJDBCConn() {
		return JDBCConn;
	}

	public void setJDBCConn(Connection jDBCConn) {
		JDBCConn = jDBCConn;
	}

	public MongoClient getMongoDBConn() {
		return MongoDBConn;
	}

	public void setMongoDBConn(MongoClient mongoDBConn) {
		MongoDBConn = mongoDBConn;
	}

}
