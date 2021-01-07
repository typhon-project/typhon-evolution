package capture.mains;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ac.york.typhon.analytics.commons.datatypes.events.Event;
import ac.york.typhon.analytics.commons.datatypes.events.PostEvent;
import ac.york.typhon.analytics.commons.serialization.EventSchema;

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

	// The parameters below are the ones required for connecting the event queue and
	// the analytics db.
	// The below affected values are the default values. If they exist, the values
	// of the environment variables (listed in EnvironmentVariable class) will
	// replace the default ones.
	private static long WAKEUP_TIME_MS_FREQUENCY = 10000;
	private static String KAFKA_CHANNEL_IP = "192.168.1.15";
	private static String KAFKA_CHANNEL_PORT = "29092";
	static String WEBSERVICE_URL = "http://localhost:8080/";
	static String WEBSERVICE_USERNAME = "admin";
	static String WEBSERVICE_PASSWORD = "admin1@";
	static String ANALYTICS_DB_IP = "localhost";
	static String ANALYTICS_DB_PORT = "5500";
	static String ANALYTICS_DB_USER = "admin";
	static String ANALYTICS_DB_PWD = "admin";
	static String ANALYTICS_DB_NAME = "Analytics";
	
	private static List<Long> times = new ArrayList<Long>();
	private static int counter = 0;

	public static void main(String[] args) throws Exception {
		// it reads the environment variables and affects their value to the connection
		// parameters above
		readEnvironmentVariables();
		
		if (args == null || args.length == 0) {
			// QL query capture system
			startService();
		} else {
			// recommendation system call by the node.js backend
			recommendQueryImprovements(args[0]);
		}

	}

	private static void readEnvironmentVariables() {
		String wakeup = getEnvironmentVariable(EnvironmentVariable.WAKEUP_TIME_MS_FREQUENCY);
		setWakeUpTime(wakeup);
		String kafka_ip = getEnvironmentVariable(EnvironmentVariable.KAFKA_CHANNEL_IP);
		setKafkaIp(kafka_ip);
		String kafka_port = getEnvironmentVariable(EnvironmentVariable.KAFKA_CHANNEL_PORT);
		setKafkaPort(kafka_port);
		String ws_url = getEnvironmentVariable(EnvironmentVariable.WEBSERVICE_URL);
		setWSUrl(ws_url);
		String ws_usr = getEnvironmentVariable(EnvironmentVariable.WEBSERVICE_USERNAME);
		setWSUsername(ws_usr);
		String ws_pwd = getEnvironmentVariable(EnvironmentVariable.WEBSERVICE_PASSWORD);
		setWSPassword(ws_pwd);
		String analytics_db_ip = getEnvironmentVariable(EnvironmentVariable.ANALYTICS_DB_IP);
		setAnalyticsDBIP(analytics_db_ip);
		String analytics_db_port = getEnvironmentVariable(EnvironmentVariable.ANALYTICS_DB_PORT);
		setAnalyticsDBPort(analytics_db_port);
		String analytics_db_usr = getEnvironmentVariable(EnvironmentVariable.ANALYTICS_DB_USER);
		setAnalyticsDBUserName(analytics_db_usr);
		String analytics_db_pwd = getEnvironmentVariable(EnvironmentVariable.ANALYTICS_DB_PWD);
		setAnalyticsDBPassword(analytics_db_pwd);
		String analytics_db_name = getEnvironmentVariable(EnvironmentVariable.ANALYTICS_DB_NAME);
		setAnalyticsDBName(analytics_db_name);

	}

	private static String getEnvironmentVariable(String variableName) {
		try {
			return System.getenv(variableName);
		} catch (Exception | Error e) {
			return null;
		}
	}

	private static void setAnalyticsDBName(String analytics_db_name) {
		if (analytics_db_name != null) {
			ANALYTICS_DB_NAME = analytics_db_name;
			logger.info("ANALYTICS_DB_NAME: " + ANALYTICS_DB_NAME);
			return;
		}
		logger.info("DEFAULT ANALYTICS_DB_NAME: " + ANALYTICS_DB_NAME);
	}

	private static void setAnalyticsDBPassword(String analytics_db_pwd) {
		if (analytics_db_pwd != null) {
			ANALYTICS_DB_PWD = analytics_db_pwd;
			logger.info("ANALYTICS_DB_PWD: " + ANALYTICS_DB_PWD);
			return;
		}
		logger.info("DEFAULT ANALYTICS_DB_PWD: " + ANALYTICS_DB_PWD);
	}

	private static void setAnalyticsDBUserName(String analytics_db_usr) {
		if (analytics_db_usr != null) {
			ANALYTICS_DB_USER = analytics_db_usr;
			logger.info("ANALYTICS_DB_USER: " + ANALYTICS_DB_USER);
			return;
		}
		logger.info("DEFAULT ANALYTICS_DB_USER: " + ANALYTICS_DB_USER);
	}

	private static void setAnalyticsDBPort(String analytics_db_port) {
		if (analytics_db_port != null) {
			ANALYTICS_DB_PORT = analytics_db_port;
			logger.info("ANALYTICS_DB_PORT: " + ANALYTICS_DB_PORT);
			return;
		}
		logger.info("DEFAULT ANALYTICS_DB_PORT: " + ANALYTICS_DB_PORT);
	}

	private static void setAnalyticsDBIP(String analytics_db_ip) {
		if (analytics_db_ip != null) {
			ANALYTICS_DB_IP = analytics_db_ip;
			logger.info("ANALYTICS_DB_IP: " + ANALYTICS_DB_IP);
			return;
		}
		logger.info("DEFAULT ANALYTICS_DB_IP: " + ANALYTICS_DB_IP);
	}

	private static void setWSPassword(String ws_pwd) {
		if (ws_pwd != null) {
			WEBSERVICE_PASSWORD = ws_pwd;
			logger.info("WEBSERVICE_PASSWORD: " + WEBSERVICE_PASSWORD);
			return;
		}
		logger.info("DEFAULT WEBSERVICE_PASSWORD: " + WEBSERVICE_PASSWORD);
	}

	private static void setWSUsername(String ws_usr) {
		if (ws_usr != null) {
			WEBSERVICE_USERNAME = ws_usr;
			logger.info("WEBSERVICE_USERNAME: " + WEBSERVICE_USERNAME);
			return;
		}
		logger.info("DEFAULT WEBSERVICE_USERNAME: " + WEBSERVICE_USERNAME);
	}

	private static void setWSUrl(String ws_url) {
		if (ws_url != null) {
			WEBSERVICE_URL = ws_url;
			logger.info("WEBSERVICE_URL: " + WEBSERVICE_URL);
			return;
		}
		logger.info("DEFAULT WEBSERVICE_URL: " + WEBSERVICE_URL);
	}

	private static void setKafkaPort(String kafka_port) {
		if (kafka_port != null) {
			KAFKA_CHANNEL_PORT = kafka_port;
			logger.info("KAFKA_PORT: " + KAFKA_CHANNEL_PORT);
			return;
		}
		logger.info("DEFAULT KAFKA_PORT: " + KAFKA_CHANNEL_PORT);

	}

	private static void setKafkaIp(String kafka_ip) {
		if (kafka_ip != null) {
			KAFKA_CHANNEL_IP = kafka_ip;
			logger.info("KAFKA IP: " + KAFKA_CHANNEL_IP);
			return;
		}
		logger.info("DEFAULT KAFKA_IP: " + KAFKA_CHANNEL_IP);

	}

	private static void setWakeUpTime(String wakeup) {
		if (wakeup != null) {
			try {
				Long time = Long.parseLong(wakeup);
				WAKEUP_TIME_MS_FREQUENCY = time;
				logger.info("WAKE UP TIME FREQUENCY: " + WAKEUP_TIME_MS_FREQUENCY);
				return;
			} catch (Exception e) {
			}
		}
		logger.info("DEFAULT WAKE UP TIME FREQUENCY: " + WAKEUP_TIME_MS_FREQUENCY);
	}

	private static void recommendQueryImprovements(String file) throws IOException, ClassNotFoundException {
		try {

			String serializedQuery = new String(Files.readAllBytes(Paths.get(file)));
			Query q = QueryParsing.deserializeQuery(serializedQuery);

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
				Files.write(Paths.get(file), "There is no recommendation for this query.".getBytes());
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
		properties.setProperty("group.id", UUID.randomUUID().toString());
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
				Date d1 = new Date();
				logger.info("receiving post event...");
				logger.info(event);
				try {

					if (event instanceof PostEvent) {
						PostEvent postEvent = (PostEvent) event;
						// TODO currently, success is always equal to NULL. One needs to check if
						// resultset is equal to null
//						boolean success = postEvent.getSuccess() == null ? postEvent.getResultSet() != null
//								: postEvent.getSuccess();
						boolean success = true;
						if (success) {
							logger.debug("Captured query: " + postEvent.getPreEvent().getQuery());
							captureQuery(postEvent);
						}
					}
				} catch (Exception | Error e) {
					logger.error("Problem happened consuming the following post event: " + event + "\nCause: ");
					e.printStackTrace();
				}
				Date d2 = new Date();
				long ms = d2.getTime() - d1.getTime();
				times.add(ms);
				counter++;
				
				if(counter == 10000) {
					long avg = getAvg(times);
					System.out.println("avg: " + avg);
					System.out.println("size:" + times.size());
				}
				
				
				return "";
			}

		});

		logger.info("Kafka consumer created");
		env.execute();
	}

	protected static long getAvg(List<Long> array) {
		long total = 0;
		for(long a : array)
			total += a;
		System.out.println(total + "/" + array.size());
		return (long) total / array.size();
	}

	private static void startSavingGeneralInformationThread() {
		new Thread() {
			public void run() {
				while (true) {

					try {
						updateGeneralInformation();
					} catch (Exception | Error e) {
						e.printStackTrace();
					}
					
					synchronized (this) {
						try {
							wait(WAKEUP_TIME_MS_FREQUENCY);
						} catch (InterruptedException e) {
						}
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
