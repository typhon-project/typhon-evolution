package sql_extractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Random;

public class SQLQueryGenerator {
	private static final String dir = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
			+ File.separator + "resources" + File.separator;

	public static void main(String[] args) throws Exception {
		insertEmployeeSQL(args);
		insertCustomerSQL(args);
		insertOrdersSQL(args);
		insertDepartmentSQL(args);
		insertAffiliationsSQL(args);
	}

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

	public static void insertCustomerSQL(String[] args) throws Exception {
		try {

			PrintStream fileOut = new PrintStream(dir + "Customer.sql");
			System.setOut(fileOut);
		} catch (FileNotFoundException e) {
			throw new Exception("Impossible to write to JSON script file: " + null);
		}

		for (int i = 0; i < 1000000; i++) {
			System.err.println(i);
			String name = randomString(100);
			String firstName = randomString(100);
			String sql = "INSERT INTO Customer VALUES (" + i + ",'" + name + "','" + firstName + "');";
			System.out.println(sql);
		}
	}

	public static void insertDepartmentSQL(String[] args) throws Exception {
		try {

			PrintStream fileOut = new PrintStream(dir + "Department.sql");
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

			PrintStream fileOut = new PrintStream(dir + "Affiliations.sql");
			System.setOut(fileOut);
		} catch (FileNotFoundException e) {
			throw new Exception("Impossible to write to JSON script file: " + null);
		}
		for (int i = 0; i < 200000; i++) {
			int depId = randomInt(100);
			int empId = randomInt(100000);
			String sql = "INSERT INTO R VALUES(" + depId + ", " + empId + ");";
			System.out.println(sql);
		}

	}

	public static void insertOrdersSQL(String[] args) throws Exception {
		try {

			PrintStream fileOut = new PrintStream(dir + "Orders.sql");
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

	public static void insertEmployeeSQL(String[] args) throws Exception {
		try {

			PrintStream fileOut = new PrintStream(dir + "Employee.sql");
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

}
