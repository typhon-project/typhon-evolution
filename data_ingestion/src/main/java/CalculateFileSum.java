import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.hadoop.util.Shell;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.json.JSONObject;

import com.sun.jersey.api.client.ClientResponse;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class CalculateFileSum {

	private static final String url = "localhost:3306/";
	private static final String table = "";
	private static final String password = "";
	private static final String user = "";

	public static String SPACE_DELIMITER = " ";

	public static void resetDatabases() {
		String url = "http://h2020.info.fundp.ac.be:8080/api/resetdatabases";
		String name = "admin";
		String password = "admin1@";
		String authString = name + ":" + password;
		String authStringEnc = Base64.getEncoder().encodeToString((authString).getBytes());
		System.out.println("Base64 encoded auth string: " + authStringEnc);
		Client restClient = Client.create();
		WebResource webResource = restClient.resource(url);
		ClientResponse resp = webResource.accept("application/json").header("Authorization", "Basic " + authStringEnc)
				.get(ClientResponse.class);
		if (resp.getStatus() != 200) {
			System.err.println("Unable to connect to the server");
		}
		String output = resp.getEntity(String.class);
		System.out.println("response: " + output);
	}

	public static void getUsers() {
		String url = "http://h2020.info.fundp.ac.be:8080/users";
		String name = "admin";
		String password = "admin1@";
		String authString = name + ":" + password;
		String authStringEnc = Base64.getEncoder().encodeToString((authString).getBytes());
		System.out.println("Base64 encoded auth string: " + authStringEnc);
		Client restClient = Client.create();
		WebResource webResource = restClient.resource(url);
		ClientResponse resp = webResource.accept("application/json").header("Authorization", "Basic " + authStringEnc)
				.get(ClientResponse.class);
		if (resp.getStatus() != 200) {
			System.err.println("Unable to connect to the server");
		}
		String output = resp.getEntity(String.class);
		System.out.println("response: " + output);
	}

	public static void main(String[] args) {
//		resetDatabases();
		post2();
	}

	public static void post() {
		try {
			Client client = Client.create();
			String name = "admin";
			String password = "admin1@";
			String authString = name + ":" + password;
			String authStringEnc = Base64.getEncoder().encodeToString((authString).getBytes());
			System.out.println("Base64 encoded auth string: " + authStringEnc);
			WebResource webResource = client.resource("http://h2020.info.fundp.ac.be:8080/api/update");
//			WebResource webResource = client.resource("http://localhost:8080/api/query");
//			String input = "from CreditCard u select u where u.id == \"0\" , u.id == \"1\"";
//			String input = "from User u select u";
			String input = "insert User {id: 2, name: \"test\", surname: \"test\" }";
			Map<String, String> map = new HashMap<String, String>();
			map.put("a", "b");
			map.put("a1", "a2");
			JSONObject jsonObject = new JSONObject(map);
			System.out.println(jsonObject.toString());
			
			ClientResponse response = webResource.type("application/json")
					.header("Authorization", "Basic " + authStringEnc).post(ClientResponse.class, input);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}

			System.out.println("Output from Server .... \n");
			String output = response.getEntity(String.class);
			System.out.println(output);

		} catch (

		Exception e) {

			e.printStackTrace();

		}
	}
	
	public static void post2() {
		try {
			Client client = Client.create();
			String name = "admin";
			String password = "admin1@";
			String authString = name + ":" + password;
			String authStringEnc = Base64.getEncoder().encodeToString((authString).getBytes());
			System.out.println("Base64 encoded auth string: " + authStringEnc);
			WebResource webResource = client.resource("http://localhost:8080/api/model/ml");
			Map<String, String> map = new HashMap<>();
			map.put("name", "OK");
	        map.put("contents", "" +
	                "<?xml version=\"1.0\" encoding=\"ASCII\"?>\n" +
	                "<typhonml:Model xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:typhonml=\"http://org.typhon.dsls.typhonml.sirius\">\n" +
	                "  <databases xsi:type=\"typhonml:RelationalDB\" name=\"RelationalDatabase\">\n" +
	                "    <tables name=\"Order\" entity=\"//@dataTypes.8\">\n" +
	                "      <indexSpec name=\"orderIndex\" attributes=\"//@dataTypes.8/@attributes.0\"/>\n" +
	                "      <idSpec attributes=\"//@dataTypes.8/@attributes.0\"/>\n" +
	                "    </tables>\n" +
	                "    <tables name=\"User\" entity=\"//@dataTypes.9\">\n" +
	                "      <indexSpec name=\"userIndex\" attributes=\"//@dataTypes.9/@attributes.1\"/>\n" +
	                "      <idSpec attributes=\"//@dataTypes.9/@attributes.1\"/>\n" +
	                "    </tables>\n" +
	                "    <tables name=\"CreditCard\" entity=\"//@dataTypes.11\">\n" +
	                "      <idSpec attributes=\"//@dataTypes.11/@attributes.1\"/>\n" +
	                "    </tables>\n" +
	                "    <tables name=\"TestRelational\" entity=\"//@dataTypes.12\">\n" +
	                "    </tables>\n" +
	                "  </databases>\n" +
	                "  <databases xsi:type=\"typhonml:DocumentDB\" name=\"DocumentDatabase\">\n" +
	                "    <collections name=\"Review\" entity=\"//@dataTypes.6\"/>\n" +
	                "    <collections name=\"Comment\" entity=\"//@dataTypes.10\"/>\n" +
	                "    <collections name=\"Product\" entity=\"//@dataTypes.7\"/>\n" +
	                "    <collections name=\"TestDocument\" entity=\"//@dataTypes.13\"/>\n" +
	                "  </databases>\n" +
	                "  <dataTypes xsi:type=\"typhonml:PrimitiveDataType\" name=\"Date\"/>\n" +
	                "  <dataTypes xsi:type=\"typhonml:PrimitiveDataType\" name=\"String\"/>\n" +
	                "  <dataTypes xsi:type=\"typhonml:PrimitiveDataType\" name=\"int\"/>\n" +
	                "  <dataTypes xsi:type=\"typhonml:PrimitiveDataType\" name=\"Real\"/>\n" +
	                "  <dataTypes xsi:type=\"typhonml:PrimitiveDataType\" name=\"Blob\"/>\n" +
	                "  <dataTypes xsi:type=\"typhonml:PrimitiveDataType\" name=\"natural_language\"/>\n" +
	                "  <dataTypes xsi:type=\"typhonml:Entity\" name=\"Review\">\n" +
	                "    <attributes name=\"id\" type=\"//@dataTypes.1\"/>\n" +
	                "    <relations name=\"product\" type=\"//@dataTypes.7\" cardinality=\"one\"/>\n" +
	                "  </dataTypes>\n" +
	                "  <dataTypes xsi:type=\"typhonml:Entity\" name=\"Product\">\n" +
	                "    <attributes name=\"id\" type=\"//@dataTypes.1\"/>\n" +
	                "    <attributes name=\"name\" type=\"//@dataTypes.1\"/>\n" +
	                "    <attributes name=\"description\" type=\"//@dataTypes.1\"/>\n" +
	                "    <attributes name=\"photo\" type=\"//@dataTypes.4\"/>\n" +
	                "    <relations name=\"review\" type=\"//@dataTypes.6\" cardinality=\"zero_many\" isContainment=\"true\"/>\n" +
	                "    <relations name=\"orders\" type=\"//@dataTypes.7\" cardinality=\"zero_many\"/>\n" +
	                "  </dataTypes>\n" +
	                "  <dataTypes xsi:type=\"typhonml:Entity\" name=\"Order\">\n" +
	                "    <attributes name=\"id\" type=\"//@dataTypes.1\"/>\n" +
	                "    <attributes name=\"date\" type=\"//@dataTypes.0\"/>\n" +
	                "    <attributes name=\"totalAmount\" type=\"//@dataTypes.2\"/>\n" +
	                "    <relations name=\"products\" type=\"//@dataTypes.7\" cardinality=\"zero_many\" opposite=\"//@dataTypes.8/@relations.0\"/>\n" +
	                "    <relations name=\"users\" type=\"//@dataTypes.9\" cardinality=\"one\"/>\n" +
	                "    <relations name=\"paidWith\" type=\"//@dataTypes.11\" cardinality=\"one\"/>\n" +
	                "  </dataTypes>\n" +
	                "  <dataTypes xsi:type=\"typhonml:Entity\" name=\"User\">\n" +
	                "    <attributes name=\"id\" type=\"//@dataTypes.1\"/>\n" +
	                "    <attributes name=\"name\" type=\"//@dataTypes.1\"/>\n" +
	                "    <attributes name=\"surname\" type=\"//@dataTypes.1\"/>\n" +
	                "    <relations name=\"comments\" type=\"//@dataTypes.10\" cardinality=\"zero_many\" isContainment=\"true\"/>\n" +
	                "    <relations name=\"paymentsDetails\" type=\"//@dataTypes.11\" cardinality=\"zero_many\" isContainment=\"true\"/>\n" +
	                "    <relations name=\"orders\" type=\"//@dataTypes.8\" cardinality=\"zero_many\"/>\n" +
	                "  </dataTypes>\n" +
	                "  <dataTypes xsi:type=\"typhonml:Entity\" name=\"Comment\">\n" +
	                "    <attributes name=\"id\" type=\"//@dataTypes.1\"/>\n" +
	                "    <relations name=\"responses\" type=\"//@dataTypes.10\" cardinality=\"zero_many\" isContainment=\"true\"/>\n" +
	                "  </dataTypes>\n" +
	                "  <dataTypes xsi:type=\"typhonml:Entity\" name=\"CreditCard\">\n" +
	                "    <attributes name=\"id\" type=\"//@dataTypes.1\"/>\n" +
	                "    <attributes name=\"number\" type=\"//@dataTypes.1\"/>\n" +
	                "    <attributes name=\"expiryDate\" type=\"//@dataTypes.0\"/>\n" +
	                "  </dataTypes>\n" +
	                "  <dataTypes xsi:type=\"typhonml:Entity\" name=\"TestRelational\">\n" +
	                "    <attributes name=\"id\" type=\"//@dataTypes.1\"/>\n" +
	                "    <relations name=\"relationalToDocument\" type=\"//@dataTypes.13\" cardinality=\"zero_many\" isContainment=\"false\"/>\n" +
	                "  </dataTypes>\n" +
	                "  <dataTypes xsi:type=\"typhonml:Entity\" name=\"TestDocument\">\n" +
	                "    <attributes name=\"id\" type=\"//@dataTypes.1\"/>\n" +
	                "  </dataTypes>\n" +
	                "</typhonml:Model>\n");
	        JSONObject jsonObject = new JSONObject(map);
			
	        String json = jsonObject.toString();
	        System.out.println(jsonObject.toString());
	        String test = "\"name\"";
			System.out.println(test);
			ClientResponse response = webResource.type("application/json")
					.header("Authorization", "Basic " + authStringEnc).post(ClientResponse.class, jsonObject.toString());

			if (response.getStatus() != 200) {
				System.out.println("response:" + response);
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}

			System.out.println("Output from Server .... \n");
			String output = response.getEntity(String.class);
			System.out.println(output);
			System.out.println("END");

		} catch (

		Exception e) {

			e.printStackTrace();

		}
	}

	public static void main2(String[] args) {

		SparkSession spark = SparkSession.builder().appName("Java Spark SQL basic example")
				.config("spark.master", "local").getOrCreate();

//		Dataset<Row> payload = spark.read().format("jdbc").option("url", "jdbc:mariadb://localhost:3306/COMMANDE")
//				.option("driver", "org.mariadb.jdbc.Driver").option("dbtable", "client").option("user", "root")
//				.option("password", "admin").load();

		Dataset<Row> payload = spark.read().format("jdbc")
				.option("url", "jdbc:sqlserver://localhost;databaseName=ACCS_LOG;integratedSecurity=true;")
				.option("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver").option("dbtable", "dbo.test")
				.option("user", "").option("password", "").load();

		System.out.println("END:" + payload.count());
		System.out.println(payload.collectAsList());
	}

}
