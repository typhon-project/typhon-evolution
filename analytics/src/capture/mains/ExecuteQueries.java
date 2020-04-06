package capture.mains;

import java.util.Base64;
import java.util.Date;
import java.util.Scanner;
import java.util.UUID;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import capture.flickKafkaUtils.QueueProducer;

import capture.commons.PostEvent;

public class ExecuteQueries {

	// Make sure you put the local ip address of your computer
	final String IP_ADDRESS = "192.168.1.15";

	public static void main(String[] args) throws Exception {

		Scanner in = new Scanner(System.in);
		while (true) {
			System.out.println("Enter your query");
			String query = in.nextLine();

			try {
				ExecuteQueries eq = new ExecuteQueries();
				ExecuteQueries.Utils utils = eq.new Utils();
				utils.executeQueryAndReturnPostvent(query);
			} catch (Exception | Error e) {
				e.printStackTrace();
			}
		}

//		utils.executeUpdateAndReturnPostvent("insert user { name: \"1234\" }");
	}

	class Utils {
		// Executes a select query
		public void executeQueryAndReturnPostvent(String query) throws Exception {
			// This is the REST url that executes a select query. Authentication is done
			// using the polystore's credentials.
			String url = "http://localhost:8080/api/query";
			String name = "admin";
			String password = "admin1@";
			String authString = name + ":" + password;
			String authStringEnc = Base64.getEncoder().encodeToString((authString).getBytes());
			System.out.println("Base64 encoded auth string: " + authStringEnc);
			Client restClient = Client.create();
			WebResource webResource = restClient.resource(url);

			// Start timing for calculating execution time
			boolean success = true;
			System.out.println("executing query ...");
			Date startTime = new Date();
			ClientResponse resp = webResource.accept("application/json")
					.header("Authorization", "Basic " + authStringEnc).post(ClientResponse.class, query);
			if (resp.getStatus() != 200) {
				System.err.println("Unable to connect to the server");
//				success = false;
			}
			Date endTime = new Date();
			
			System.out.println("query executed : " + (endTime.getTime() - startTime.getTime() + "ms"));

			String output = resp.getEntity(String.class);

			// Get date/time when query execution has finished
			

			System.out.println("Response: " + output);

			PostEvent postEvent = new PostEvent();
			postEvent.setId(UUID.randomUUID().toString());
			postEvent.setQuery(query);
			postEvent.setSuccess(success);
			postEvent.setResultSet(output);
			postEvent.setStartTime(startTime);
			postEvent.setEndTime(endTime);
			System.out.println(postEvent);
			
			System.out.println("Sending postEvent:" + new Date());

			// Publish PostEvent to POST queue
			produce(postEvent);
			
			System.out.println("Post event sent:" + new Date());
			

		}

		// Executes an insert/delete/update query
		public void executeUpdateAndReturnPostvent(String query) throws Exception {
			// This is the REST url that executes a select query. Authentication is done
			// using the polystore's credentials.
			String url = "http://localhost:8080/api/update";
			String name = "admin";
			String password = "admin1@";
			String authString = name + ":" + password;
			String authStringEnc = Base64.getEncoder().encodeToString((authString).getBytes());
			System.out.println("Base64 encoded auth string: " + authStringEnc);
			Client restClient = Client.create();
			WebResource webResource = restClient.resource(url);

			// Start timing for calculating execution time
			Date startTime = new Date();
			ClientResponse resp = webResource.accept("application/json")
					.header("Authorization", "Basic " + authStringEnc).post(ClientResponse.class, query);
			if (resp.getStatus() != 200) {
				System.err.println("Unable to connect to the server");
			}

			String output = resp.getEntity(String.class);

			// Get date/time when query execution has finished
			Date endTime = new Date();

			System.out.println("response: " + output);

			PostEvent postEvent = new PostEvent();
			postEvent.setId(UUID.randomUUID().toString());
			postEvent.setQuery(query);
			postEvent.setSuccess(true);
			postEvent.setResultSet(output);
			postEvent.setStartTime(startTime);
			postEvent.setEndTime(endTime);
			System.out.println(postEvent);

			// Publish PostEvent to POST queue
			produce(postEvent);

		}

		public void produce(PostEvent postEvent) throws Exception {
			String kafkaConnection = IP_ADDRESS + ":29092";
			QueueProducer qp = new QueueProducer(kafkaConnection);
			qp.produce("POST", postEvent);
		}
	}

}
