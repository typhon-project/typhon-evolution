

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ac.york.typhon.analytics.commons.datatypes.events.Event;
import ac.york.typhon.analytics.commons.datatypes.events.PostEvent;
import ac.york.typhon.analytics.commons.serialization.EventSchema;
import capture.mains.QueryParsing;

//import capture.commons.Event;
//import capture.commons.EventSchema;
//import capture.commons.PostEvent;

import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import db.AnalyticsDB;
import model.TyphonModel;
import query.Query;
import recommendations.Recommendation;
import recommendations.RecommendationMgr;
import typhonml.Entity;
import typhonml.Relation;

public class ConsumePostEvents {
	private static Logger logger = Logger.getLogger(ConsumePostEvents.class);

	private static final long WAKEUP_TIME_MS_FREQUENCY = 10000;
	private static final String KAFKA_CHANNEL_IP = "192.168.1.15";
	private static final String KAFKA_CHANNEL_PORT = "29092";
	static final String WEBSERVICE_URL = "http://localhost:8080/";
	static final String WEBSERVICE_USERNAME = "admin";
	static final String WEBSERVICE_PASSWORD = "admin1@";
	static final String ANALYTICS_DB_IP = "localhost";
	static final int ANALYTICS_DB_PORT = 5502;
	static final String ANALYTICS_DB_USER = "username";
	static final String ANALYTICS_DB_PWD = "password";
	static final String ANALYTICS_DB_NAME = "Analytics";

	static {
		try {
			PropertyConfigurator.configure(System.getProperty("user.dir") + File.separator + "resources"
					+ File.separator + "log4j.properties");
		} catch (Exception | Error e) {
			System.err.println("cannot initialize log4j");
		}

	}

	public static void main(String[] args) throws Exception {
//		args = new String[] { (
//				"rO0ABXNyABNjYXB0dXJlLm1haW5zLlF1ZXJ5+3Ewr9ipd4YCAAtMAAthbGxFbnRpdGllc3QAD0xqYXZhL3V0aWwvU2V0O0wAEmF0dHJpYnV0ZVNlbGVjdG9yc3QAEExqYXZhL3V0aWwvTGlzdDtMABBkaXNwbGF5YWJsZVF1ZXJ5dAASTGphdmEvbGFuZy9TdHJpbmc7TAAHaW5zZXJ0c3EAfgACTAAFam9pbnNxAH4AAkwADG1haW5FbnRpdGllc3EAfgACTAAFbW9kZWx0ABJMamF2YS9sYW5nL09iamVjdDtMAA9ub3JtYWxpemVkUXVlcnlxAH4AA0wADW9yaWdpbmFsUXVlcnlxAH4AA0wACXF1ZXJ5VHlwZXEAfgADTAAPc2VyaWFsaXplZFF1ZXJ5cQB+AAN4cHNyABFqYXZhLnV0aWwuSGFzaFNldLpEhZWWuLc0AwAAeHB3DAAAABA/QAAAAAAAAXQADE9yZGVyUHJvZHVjdHhzcgATamF2YS51dGlsLkFycmF5TGlzdHiB0h2Zx2GdAwABSQAEc2l6ZXhwAAAAAXcEAAAAAXNyAB9jYXB0dXJlLm1haW5zLkF0dHJpYnV0ZVNlbGVjdG9yBfHuCWYzyZICAARMAAphdHRyaWJ1dGVzcQB+AAJMAAplbnRpdHlOYW1lcQB+AANMAA1pbXBsaWNpdEpvaW5zcQB+AAJMAAtpbXBsaWNpdFNlbHQAIUxjYXB0dXJlL21haW5zL0F0dHJpYnV0ZVNlbGVjdG9yO3hwc3EAfgAJAAAAAXcEAAAAAXQADHByb2R1Y3RfZGF0ZXhxAH4ACHBweHQAVHVwZGF0ZSBPcmRlclByb2R1Y3QgeDAgd2hlcmUgeDAucHJvZHVjdF9kYXRlID09ICI/IiBzZXQge3Byb2R1Y3RfZGF0ZTogIj8iLCBpZDogIj8ifXNxAH4ACQAAAAB3BAAAAAB4c3EAfgAJAAAAAHcEAAAAAHhzcQB+AAkAAAABdwQAAAABcQB+AAh4cHQASXVwZGF0ZU9yZGVyUHJvZHVjdHgwd2hlcmV4MC5wcm9kdWN0X2RhdGU9PSI/InNldHtwcm9kdWN0X2RhdGU6Ij8iLGlkOiI/In10AGx1cGRhdGUgT3JkZXJQcm9kdWN0IHgwIHdoZXJlIHgwLnByb2R1Y3RfZGF0ZSA9PSAiWXNPV3NlaUlKIiBzZXQge3Byb2R1Y3RfZGF0ZTogIk9iQktJTk5XIiwgaWQ6ICJ1QTZlNzBqb2JtIn10AAZVUERBVEVw") };
//		args = new String[] { "C:\\Users\\lmeurice\\Desktop\\test.txt" };

		if (args == null || args.length == 0) {
			startService();
		} else {
			recommendQueryImprovements(args[0]);
		}

	}

	private static void recommendQueryImprovements(String file) throws IOException, ClassNotFoundException {
		try {

			String serializedQuery = new String(Files.readAllBytes(Paths.get(file)));
			Query q = QueryParsing.deserializeQuery(serializedQuery);
			System.out.println("Query to recommend: " + q.getDisplayableQuery());

			TyphonModel.initWebService(WEBSERVICE_URL, WEBSERVICE_USERNAME, WEBSERVICE_PASSWORD);
			TyphonModel m = TyphonModel.getCurrentModel();
			q.setModel(m);
			List<Recommendation> recommendations = RecommendationMgr.getRecommendations(q);
//			file = file + "TEST";
			if (recommendations.size() > 0) {
				Document document = getHtml(recommendations);

				// create the xml file
				// transform the DOM Object to an XML File
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource domSource = new DOMSource(document);
				StreamResult streamResult = new StreamResult(new File(file));

				// If you use
				// StreamResult result = new StreamResult(System.out);
				// the output will be pushed to the standard output ...
				// You can use that for debugging

				transformer.transform(domSource, streamResult);
			} else {
				Files.write(Paths.get(file),
						"There is no recommendation for this query."
								.getBytes());
			}

		} catch (Exception | Error e) {
			e.printStackTrace();
			try {
				Files.write(Paths.get(file),
						"An unexpected error happened. Impossible to propose recommendations for this query."
								.getBytes());
			} catch (IOException e2) {

			}
		}

	}

	public static Document getHtml(List<Recommendation> recommendations) throws ParserConfigurationException {
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

		DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

		Document document = documentBuilder.newDocument();

		Element root = document.createElement("div");
		root.setAttribute("class", "recommendationDiv");
		document.appendChild(root);
		for (Recommendation r : recommendations) {
			System.out.println(r);
			root.appendChild(r.getHTMLElement(document, null, false));
		}
		return document;
	}

	private static JSONObject getJSON(List<Recommendation> recommendations) {
		JSONObject res = new JSONObject();
		JSONArray array = new JSONArray();
		for (Recommendation r : recommendations)
			array.put(r.getJSON());
		res.put("ok", true);
		res.put("recommendations", array);

		return res;
	}


	private static void startService() throws Exception {
		if (!initializeQueryParsingPlugin())
			System.exit(1);

		if (!AnalyticsDB.initConnection(ANALYTICS_DB_IP, ANALYTICS_DB_PORT, ANALYTICS_DB_USER, ANALYTICS_DB_PWD,
				ANALYTICS_DB_NAME))
			System.exit(1);

		TyphonModel.initWebService(WEBSERVICE_URL, WEBSERVICE_USERNAME, WEBSERVICE_PASSWORD);

		startSavingGeneralInformationThread();

		logger.info("Creating new kafka consumer ...");
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", KAFKA_CHANNEL_IP + ":" + KAFKA_CHANNEL_PORT);
		properties.setProperty("group.id", "namur");
		properties.setProperty("auto.offset.reset", "earliest");

		DataStream<Event> PostEventStream = env
				.addSource(new FlinkKafkaConsumer<Event>("POST", new EventSchema(PostEvent.class), properties));

		// This is where you need to write your code in Flink - which is actual Java
		// with some extra
		// operators that allow you to deal with streams of data. If you are not
		// familiar with Flink
		// I have a simple map function below. Just type your java code in there. If you
		// don't want
		// to work with Flink, then search online on other ways to consumer Kafka queues
		// (Kafka itself has
		// connectors that allow you consume queues). Flink though offers all the
		// infrastructure for
		// automatic distribution of the tasks. But selecting the technology that you
		// will us to write
		// your evolution tasks is up to you.
		PostEventStream.map(new MapFunction<Event, String>() {

			@Override
			public String map(Event event) throws Exception {
				logger.info("receiving post event...");
				logger.info(event);
				try {

					if (event instanceof PostEvent) {
						PostEvent postEvent = (PostEvent) event;
						// TODO currently, success is always equal to NULL. One needs to check if
						// resultset is equal to null
						boolean success = postEvent.getSuccess() == null ? postEvent.getResultSet() != null
								: postEvent.getSuccess();
						if (success) {
							logger.debug("Captured query: " + postEvent.getPreEvent().getQuery());
							captureQuery(postEvent);
						}
					}
				} catch (Exception | Error e) {
					logger.error("Problem happened consuming the following post event: " + event + "\nCause: ");
					e.printStackTrace();
				}

				return "";
			}

		});

		logger.info("Kafka consumer created");
		env.execute();
	}

	private static void startSavingGeneralInformationThread() {
		new Thread() {
			public void run() {
				while (true) {

					try {
						updateGeneralInformation();
						synchronized (this) {
							wait(WAKEUP_TIME_MS_FREQUENCY);
						}
					} catch (Exception | Error e) {
						e.printStackTrace();
					}

				}
			}

		}.start();

		logger.info("General information saving Thread started");

	}

	protected static void updateGeneralInformation() {
		TyphonModel.getCurrentModelWithStats(false);
//		logger.debug("General information updated");
	}

	protected static void captureQuery(PostEvent postEvent) {

		String query = postEvent.getPreEvent().getQuery();
		Date startDate = postEvent.getStartTime();
		Date endDate = postEvent.getEndTime();
		long diff = endDate.getTime() - startDate.getTime();
		TyphonModel m = TyphonModel.checkIfNewModelWasLoaded();

		Query q = QueryParsing.eval(query, m);

		saveAnalyzedQueryInAnalyticsDB(q, startDate, diff);

	}

	private static void saveAnalyzedQueryInAnalyticsDB(Query q, Date startDate, long executionTime) {
		AnalyticsDB.saveExecutedQuery(q, startDate, executionTime);

	}

	private static boolean initializeQueryParsingPlugin() {
		return QueryParsing.init();
	}

}