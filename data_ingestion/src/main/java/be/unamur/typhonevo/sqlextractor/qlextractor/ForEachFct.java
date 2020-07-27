package be.unamur.typhonevo.sqlextractor.qlextractor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.Scanner;

public class ForEachFct {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/test";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "example";
	static int START_COUNTER = 0;
	static final int NB_OF_ROWS_TO_INSERT = 1000000;

	public static void main3(String[] args) {
		Connection conn = null;
		Statement stmt = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName(JDBC_DRIVER);

			// STEP 3: Open a connection
			System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Connected database successfully...");

			// STEP 4: Execute a query
			System.out.println("Inserting records into the table...");
			stmt = conn.createStatement();
//			stmt.setFetchSize(10);

			ResultSet rs = stmt.executeQuery("select * from CLIENT");
			while (rs.next()) {
				System.out.println(rs.getInt(1));
			}
			System.out.println("");
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
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

	public static void main2(String[] args) {
		String filePath = "C:/Users/lmeurice/Documents/test.sql";
		Connection conn = null;
		Statement stmt = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName(JDBC_DRIVER);

			// STEP 3: Open a connection
			System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/RelationalDatabase", USER, PASS);
			System.out.println("Connected database successfully...");

			// STEP 4: Execute a query
			System.out.println("Inserting records into the table...");
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
					System.out.println(counter);
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

		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
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
		System.out.println("Goodbye!");
	}

	public static void insertDepartmentSQL(String[] args) throws Exception {
		try {

			PrintStream fileOut = new PrintStream(
					"C:/Users/lmeurice/Documents/typhon-evolutiontool/sql_extractor/src/test/resources/Department.sql");
			System.setOut(fileOut);
		} catch (FileNotFoundException e) {
			throw new Exception("Impossible to write to JSON script file: " + null);
		}

		for (int i = 0; i < 100; i++)
			System.out.println("INSERT INTO Department VALUES(" + i + ", '" + randomString(100) + "', '"
					+ randomString(200) + "', '" + randomString(1000) + "', NOW(), " + randomInt(1000) + ");");

	}
	
	public static void insertAffiliationsSQL(String[] args) throws Exception {
		try {

			PrintStream fileOut = new PrintStream(
					"C:/Users/lmeurice/Documents/typhon-evolutiontool/sql_extractor/src/test/resources/Affiliations.sql");
			System.setOut(fileOut);
		} catch (FileNotFoundException e) {
			throw new Exception("Impossible to write to JSON script file: " + null);
		}
		for(int i = 0; i < 200000; i++) {
			int depId = randomInt(100);
			int empId = randomInt(100000);
			String sql = "INSERT INTO R VALUES(" + depId + ", " + empId + ");";
			System.out.println(sql);
		}
		
	}

	public static void createOrdersSQL(String[] args) throws Exception {
		try {

			PrintStream fileOut = new PrintStream(
					"C:/Users/lmeurice/Documents/typhon-evolutiontool/sql_extractor/src/test/resources/Orders.sql");
			System.setOut(fileOut);
		} catch (FileNotFoundException e) {
			throw new Exception("Impossible to write to JSON script file: " + null);
		}

		for (int i = 0; i < 200000; i++) {
			String tva = randomString(20);
			int empId = randomInt(100000);
			int custId = randomInt(1000000);
			String sql = "INSERT INTO Orders VALUES(" + i + ", '" + tva + "', " + custId + ", " + empId + ");";
			System.out.println(sql);
		}

	}

	public static void createEmployeeSQL(String[] args) throws Exception {
		try {

			PrintStream fileOut = new PrintStream(
					"C:/Users/lmeurice/Documents/typhon-evolutiontool/sql_extractor/src/test/resources/Employee.sql");
			System.setOut(fileOut);
		} catch (FileNotFoundException e) {
			throw new Exception("Impossible to write to JSON script file: " + null);
		}

		for (int i = 0; i < 100000; i++) {
			String name = randomString(100);
			String firstName = randomString(100);
			String history = randomString(1000);
			String sql = "INSERT INTO Employee VALUES (" + i + ", '" + firstName + "', '" + name + "', '" + history
					+ "');";
			System.out.println(sql);
		}

	}

	public static void main33(String[] args) {

		Connection conn = null;
		Statement stmt = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName(JDBC_DRIVER);

			// STEP 3: Open a connection
			System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Connected database successfully...");

			// STEP 4: Execute a query
			System.out.println("Inserting records into the table...");
			stmt = conn.createStatement();

			conn.setAutoCommit(false);

			try {

				PrintStream fileOut = new PrintStream(
						"C:/Users/lmeurice/Documents/typhon-evolutiontool/sql_extractor/src/test/resources/Customer.sql");
				System.setOut(fileOut);
			} catch (FileNotFoundException e) {
				throw new Exception("Impossible to write to JSON script file: " + null);
			}

			for (int i = 0; i < NB_OF_ROWS_TO_INSERT; i++) {
				System.err.println(i);
				String name = randomString(100);
				String firstName = randomString(100);
				String sql = "INSERT INTO Customer VALUES (" + i + ",'" + name + "','" + firstName + "');";
				System.out.println(sql);
				// stmt.executeUpdate(sql);
//				START_COUNTER++;
//				if (i % 1000 == 0) {
//					conn.commit();
//					System.out.println("commit:" + i);
//				}
			}

		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
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
	}// end main

	private static int randomInt(int maxBound) {
		Random r = new Random();
		return r.nextInt(maxBound);
	}

	private static String randomString(int targetStringLength) {
		int leftLimit = 97; // letter 'a'
		int rightLimit = 122; // letter 'z'
		Random random = new Random();
		StringBuilder buffer = new StringBuilder(targetStringLength);
		for (int i = 0; i < targetStringLength; i++) {
			int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
			buffer.append((char) randomLimitedInt);
		}
		String generatedString = buffer.toString();
		return generatedString;
	}

}
