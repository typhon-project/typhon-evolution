package be.unamur.typhonevo.sqlinjector;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Scanner;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class QLQueryInjector {
	private static Logger logger = LoggerFactory.getLogger(QLQueryInjector.class);
	private static final JerseyClient restClient = JerseyClientBuilder.createClient();
	private static WebResource webResource = null;
	private static String authStringEnc = null;

	public static String polystoreServiceUrl = null;
	public static String polystoreServiceQueryUpdate = null;
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
		boolean failed = false;
		try {
			WebTarget webTarget = restClient.target(polystoreServiceUrl).path(polystoreServiceQueryUpdate);
//	        String query = "from User u select u";
	        Response resp = webTarget
	                .request(MediaType.APPLICATION_JSON)
	                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((polystoreServiceLogin + ":" + polystoreServicePassword).getBytes()))
	                .post(Entity.entity(ql, MediaType.APPLICATION_JSON));
			
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

}
