package be.unamur.typhonevo.sqlinjector;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class SQLQueryInjector {
	public static String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
	public static String DB_URL = "jdbc:mysql://localhost:3306/test";

	// Database credentials
	public static String USER = "root";
	public static String PASS = "example";

	private static final String dir = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
			+ File.separator + "resources" + File.separator;

	public static void main(String[] args) throws ClassNotFoundException, IOException, SQLException {
		injects();
	}

	public static void injects() throws ClassNotFoundException, IOException, SQLException {
		String[] files = new String[] { dir + "Employee.sql", dir + "Customer.sql", dir + "Department.sql",
				dir + "Orders.sql", dir + "Affiliations.sql" };

		for (String file : files) {
			System.out.println(file + " ...");
			inject(file);
		}

	}

	public static void inject(String filePath) throws IOException, ClassNotFoundException, SQLException {
		Connection conn = null;
		Statement stmt = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName(JDBC_DRIVER);

			// STEP 3: Open a connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			// STEP 4: Execute a query
			stmt = conn.createStatement();

			conn.setAutoCommit(false);

			FileInputStream inputStream = null;
			Scanner sc = null;
			try {
				inputStream = new FileInputStream(filePath);
				sc = new Scanner(inputStream, "UTF-8");
				int counter = 0;
				while (sc.hasNextLine()) {
					String sql = sc.nextLine();
					stmt.executeUpdate(sql);
					counter++;
					if (counter % 1000 == 0)
						conn.commit();
//					System.out.println(counter);
				}

				if (counter % 1000 != 0)
					conn.commit();

				// note that Scanner suppresses exceptions
				if (sc.ioException() != null) {
					throw sc.ioException();
				}
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
				if (sc != null) {
					sc.close();
				}
			}

		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			} // do nothing
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try
	}

}
