package be.unamur.typhonevo.sqlextractor.jdbcextractor.extractJdbc;

import java.awt.Toolkit;
import java.lang.reflect.Array;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jhe
 */
public class ExtractDialog {

	/**
	 * Constantes
	 */
	private static final long serialVersionUID = 1L;
	private final static int DBMS_ACCESS = 1;
	private final static String DBMS_ACCESS_NAME = "Access";
	private final static String DRIVER_ACCESS = "sun.jdbc.odbc.JbdcOdbcDriver";
	private final static String URL_ACCESS = "jdbc:odbc:<ODBCDATASOURCENAME>";
	private final static int DBMS_DB2 = 2;
	private final static String DBMS_DB2_NAME = "DB2";
	private final static String DRIVER_DB2 = "com.ibm.db2.jcc.DB2Driver";
	private final static String URL_DB2 = "jdbc:db2://<HOSTNAME>:<PORT>/<DATABASENAME>:currentSchema=<SCHEMANAME>;";
	private final static int DBMS_FIREBIRD = 3;
	private final static String DBMS_FIREBIRD_NAME = "Firebird";
	private final static String DRIVER_FIREBIRD = "org.firebirdsql.jdbc.FBDriver";
	private final static String URL_FIREBIRD = "jdbc:firebird://<HOSTNAME>:<PORT>]/<DATABASENAME>";
	private final static int DBMS_INTERBASE = 4;
	private final static String DBMS_INTERBASE_NAME = "Interbase";
	private final static String DRIVER_INTERBASE = "interbase.interclient.Driver";
	private final static String URL_INTERBASE = "jdbc:interbase://<HOSTNAME>:<PORT>]/<DATABASENAME>";
	public final static int DBMS_MYSQL = 5;
	private final static String DBMS_MYSQL_NAME = "MySQL";
	private final static String DRIVER_MYSQL = "com.mysql.jdbc.Driver";
	private final static String URL_MYSQL = "jdbc:mysql://<HOSTNAME>:<PORT>/<NAME>";
	private final static int DBMS_ORACLE = 6;
	private final static String DBMS_ORACLE_NAME = "Oracle";
	private final static String DRIVER_ORACLE = "oracle.jdbc.driver.OracleDriver";
	private final static String URL_ORACLE = "jdbc:oracle:thin:@//<HOSTNAME>/XE";
	private final static int DBMS_PSQL = 7;
	private final static String DBMS_PSQL_NAME = "PostgreSQL";
	private final static String DRIVER_PSQL = "org.postgresql.Driver";
	private final static String URL_PSQL = "jdbc:postgresql://<HOSTNAME>:<PORT>/<DATABASENAME>";
	public final static int DBMS_SQLITE = 8;// used by class Extract
	private final static String DBMS_SQLITE_NAME = "SQLite";
	private final static String DRIVER_SQLITE = "org.sqlite.JDBC";
	private final static String URL_SQLITE = "jdbc:sqlite:<FILENAME>";
	private final static int DBMS_SQL_SERVER = 9;
	private final static String DBMS_SQL_SERVER_NAME = "SQL Server";
	public final static String DRIVER_SQL_SERVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public final static String URL_SQL_SERVER = "jdbc:sqlserver://<HOSTNAME>:<PORT>;databaseName=<DATABASENAME>";
	private final static int DBMS_OTHER = 10;
	private final static String DBMS_OTHER_NAME = "Other";
	public final static String DRIVER_MARIADB = "org.mariadb.jdbc.Driver";

	public static String getDBMSDelimiter(String driverJDBC) {

		switch (driverJDBC) {
		case DRIVER_MYSQL:
			return "`";
		case DRIVER_MARIADB:
			return "`";
		case DRIVER_ORACLE:
			return "\"";
		case DRIVER_SQL_SERVER:
			return "\"";
		}
		return "";
	}

	public static String getDBMSName(int dbmsNumber) {
		switch (dbmsNumber) {
		case DBMS_ACCESS:
			return DBMS_ACCESS_NAME;
		case DBMS_DB2:
			return DBMS_DB2_NAME;
		case DBMS_FIREBIRD:
			return DBMS_FIREBIRD_NAME;
		case DBMS_INTERBASE:
			return DBMS_INTERBASE_NAME;
		case DBMS_MYSQL:
			return DBMS_MYSQL_NAME;
		case DBMS_ORACLE:
			return DBMS_ORACLE_NAME;
		case DBMS_PSQL:
			return DBMS_PSQL_NAME;
		case DBMS_SQLITE:
			return DBMS_SQLITE_NAME;
		case DBMS_SQL_SERVER:
			return DBMS_SQL_SERVER_NAME;
		default:
			return DBMS_OTHER_NAME;
		}
	}
 
    
}
