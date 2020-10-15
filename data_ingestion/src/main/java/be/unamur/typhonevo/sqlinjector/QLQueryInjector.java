package be.unamur.typhonevo.sqlinjector;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class QLQueryInjector {
	private static Logger logger = LoggerFactory.getLogger(QLQueryInjector.class);
	private static WebResource webResource = null;
	private static String authStringEnc = null;

	public static String polystoreServiceUrl = null;
	public static String polystoreServiceLogin = null;
	public static String polystoreServicePassword = null;
	
	public static int numberOfFailingQuery = 0;

	public static void inject(String filePath) throws IOException, ClassNotFoundException, SQLException {
		FileInputStream inputStream = null;
		Scanner sc = null;
		try {
			inputStream = new FileInputStream(filePath);
			sc = new Scanner(inputStream, "UTF-8");
			while (sc.hasNextLine()) {
				String ql = sc.nextLine();
				executeQLQuery(ql);
			}

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

	}

	private static void executeQLQuery(String ql) {
		connect();
		boolean failed = false;
		try {
			ClientResponse resp = webResource.accept("application/json")
					.header("Authorization", "Basic " + authStringEnc).header("content-type", "application/json").post(ClientResponse.class, ql);
			if (resp.getStatus() != 200) {
				System.out.println(resp);
				logger.error("Unable to execute the following prepared query: " + ql);
				failed = true;
			}
		} catch (Exception | Error e) {
			logger.error("Error while connecting the polystore web service: " + e.getMessage());
			failed = true;
		}
		
		if(failed) {
			numberOfFailingQuery++;
			System.err.println(ql);
		}

	}

	private static void connect() {
		if (webResource == null) {

			try {
				String authString = polystoreServiceLogin + ":" + polystoreServicePassword;
				authStringEnc = Base64.getEncoder().encodeToString((authString).getBytes());
				Client restClient = Client.create();
				webResource = restClient.resource(polystoreServiceUrl);
				logger.debug("Connected to " + polystoreServiceUrl);
				logger.debug("Username:" + polystoreServiceLogin);
				logger.debug("Password:" + polystoreServicePassword);
				
			} catch (Exception | Error e) {
				logger.error("Cannot connect the polystore web service: " + polystoreServiceUrl + " (login: "
						+ polystoreServiceLogin + ", pwd: " + polystoreServicePassword + ")");
			}
		}
	}

}
