package capture.mains;

import java.util.Base64;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

import ac.york.typhon.analytics.commons.datatypes.events.PostEvent;
import ac.york.typhon.analytics.commons.datatypes.events.PreEvent;
import capture.flickKafkaUtils.QueueProducer;
import db.AnalyticsDB;
import model.TyphonModel;

public class ExecuteQueries {

	// Make sure you put the local ip address of your computer
	final static String IP_ADDRESS = "192.168.1.15";

	public static void main2(String[] args) {
		if (!AnalyticsDB.initConnection(ConsumePostEvents.ANALYTICS_DB_IP, ConsumePostEvents.ANALYTICS_DB_PORT,
				ConsumePostEvents.ANALYTICS_DB_USER, ConsumePostEvents.ANALYTICS_DB_PWD,
				ConsumePostEvents.ANALYTICS_DB_NAME))
			System.exit(1);
		TyphonModel.initWebService(ConsumePostEvents.WEBSERVICE_URL, ConsumePostEvents.WEBSERVICE_USERNAME,
				ConsumePostEvents.WEBSERVICE_PASSWORD);
		RandomQueryGenerator g = new RandomQueryGenerator(TyphonModel.getCurrentModel());

		String query = "from Address x0, User x1 select x0, x1 where x0.user == x1, x0.city == \"London\"";
		simulateQuery(query);
	}

	public static void main3(String[] args) {
		if (!AnalyticsDB.initConnection(ConsumePostEvents.ANALYTICS_DB_IP, ConsumePostEvents.ANALYTICS_DB_PORT,
				ConsumePostEvents.ANALYTICS_DB_USER, ConsumePostEvents.ANALYTICS_DB_PWD,
				ConsumePostEvents.ANALYTICS_DB_NAME))
			System.exit(1);
		TyphonModel.initWebService(ConsumePostEvents.WEBSERVICE_URL, ConsumePostEvents.WEBSERVICE_USERNAME,
				ConsumePostEvents.WEBSERVICE_PASSWORD);
		RandomQueryGenerator g = new RandomQueryGenerator(TyphonModel.getCurrentModel());

		for (int i = 0; i < 100; i++) {
			String query = g.randomQuery();
			simulateQuery(query);
		}
	}

	public static void main(String[] args) {
		
		TyphonModel.initWebService(ConsumePostEvents.WEBSERVICE_URL, ConsumePostEvents.WEBSERVICE_USERNAME,
				ConsumePostEvents.WEBSERVICE_PASSWORD);
		RandomQueryGenerator g = new RandomQueryGenerator(TyphonModel.getCurrentModel());
		for(int i = 0; i < 2512; i++) {
			System.out.println("select: " + i + "/2512");
			simulateQuery(g.getRandomSelectQuery());
		}
		
		for(int i = 0; i < 1232; i++) {
			simulateQuery(g.getRandomInsertQuery());
			System.out.println(i + "/1232");
		}
		
		for(int i = 0; i < 67; i++)
			simulateQuery(g.getRandomUpdateQuery());
		
		for(int i = 0; i < 95; i++)
			simulateQuery(g.getRandomDeleteQuery());
		
		
		
		simulateQuery("from Employees e, EmployeeAddress a select e.HomePhone, a.Address where e.EmployeeAddress== a && a.City == \"Brussels\"", 2125);
		simulateQuery("from Employees e, EmployeeAddress a select e.HomePhone, a.Address where e.EmployeeAddress== a && a.City == \"New York\"", 3224);
		simulateQuery("from Employees e, EmployeeAddress a select e.HomePhone, a.Address where e.EmployeeAddress== a && a.City == \"Rome\"", 3847);
		simulateQuery("from Employees e, EmployeeAddress a select e.HomePhone, a.Address where e.EmployeeAddress== a && a.City == \"Berlin\"", 4513);
		simulateQuery("from Employees e, EmployeeAddress a select e.HomePhone, a.Address where e.EmployeeAddress== a && a.City == \"Armsterdam\"", 5891);
		simulateQuery("from Employees e, EmployeeAddress a select e.HomePhone, a.Address where e.EmployeeAddress== a && a.City == \"Paris\"", 6234);
		simulateQuery("from Employees e, EmployeeAddress a select e.HomePhone, a.Address where e.EmployeeAddress== a && a.City == \"Madrid\"", 7401);
		simulateQuery("from Employees e, EmployeeAddress a select e.HomePhone, a.Address where e.EmployeeAddress== a && a.City == \"London\"", 8610);
		
		
			
	}

	private static void simulateQuery(String query, int time) {

		try {
			PostEvent ev = generateRandomPostEvent(query, time);
			produce(ev);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void simulateQuery(String query) {

		try {
			PostEvent ev = generateRandomPostEvent(query);
			produce(ev);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void executeQuery(String query) {
		if (query.toLowerCase().startsWith("from")) {
			executeSelectQuery(query);
		}

		if (query.toLowerCase().startsWith("insert") || query.toLowerCase().startsWith("update")
				|| query.toLowerCase().startsWith("delete")) {
			executeUpdateInsertDeleteQuery(query);
		}
	}

	private static void executeUpdateInsertDeleteQuery(String query) {
//		String url = "http://localhost:8080/api/update";
//		String name = "admin";
//		String password = "admin1@";
//		String authString = name + ":" + password;
//		String authStringEnc = Base64.getEncoder().encodeToString((authString).getBytes());
//		Client restClient = Client.create();
//		WebResource webResource = restClient.resource(url);
//
//		// Start timing for calculating execution time
//		Date startTime = new Date();
//		System.out.println("executing query...");
//		ClientResponse resp = webResource.accept("application/json")
//				.header("Authorization", "Basic " + authStringEnc).post(ClientResponse.class, query);
//		if (resp.getStatus() != 200) {
//			System.err.println("Unable to connect to the server");
//		} else {
//			System.out.println("query successfully executed in " + (new Date().getTime() - startTime.getTime()) + "ms");
//			String output = resp.getEntity(String.class);
//		}

	}

	private static void executeSelectQuery(String query) {

//		String url = "http://localhost:8080/api/query";
//		String name = "admin";
//		String password = "admin1@";
//		String authString = name + ":" + password;
//		String authStringEnc = Base64.getEncoder().encodeToString((authString).getBytes());
//		Client restClient = Client.create();
//		WebResource webResource = restClient.resource(url);
//
//		// Start timing for calculating execution time
//		System.out.println("executing query ...");
//		Date startTime = new Date();
//		ClientResponse resp = webResource.accept("application/json").header("Authorization", "Basic " + authStringEnc)
//				.post(ClientResponse.class, query);
//		if (resp.getStatus() != 200) {
//			System.err.println("Unable to connect to the server");
//		} else {
//			Date endTime = new Date();
//			System.out.println("query successfully executed : " + (endTime.getTime() - startTime.getTime() + "ms"));
//			String output = resp.getEntity(String.class);
//		}

	}

	private static PostEvent generateRandomPostEvent(String query) {
		PreEvent event = new PreEvent();

		event.setId(UUID.randomUUID().toString());
		event.setQuery(query);
		event.setDbUser("user");
		event.setAuthenticated(true);

		PostEvent post = new PostEvent();
		post.setId(UUID.randomUUID().toString());
		post.setQuery(query);
		post.setPreEvent(event);
		post.setSuccess(true);
		post.setStartTime(new Date());
		post.setEndTime(new Date(new Date().getTime() + getRandomExecutionTime()));

		return post;
	}

	private static PostEvent generateRandomPostEvent(String query, int time) {
		PreEvent event = new PreEvent();

		event.setId(UUID.randomUUID().toString());
		event.setQuery(query);
		event.setDbUser("user");
		event.setAuthenticated(true);

		PostEvent post = new PostEvent();
		post.setId(UUID.randomUUID().toString());
		post.setQuery(query);
		post.setPreEvent(event);
		post.setSuccess(true);
		post.setStartTime(new Date());
		post.setEndTime(new Date(new Date().getTime() + time));

		return post;
	}

	private static int getRandomExecutionTime() {
		return getRandomNumberInRange(75, 2315);
	}

	private static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

	public static void produce(PostEvent postEvent) throws Exception {
		String kafkaConnection = IP_ADDRESS + ":29092";
		QueueProducer qp = new QueueProducer(kafkaConnection);
		qp.produce("POST", postEvent);
	}

}
