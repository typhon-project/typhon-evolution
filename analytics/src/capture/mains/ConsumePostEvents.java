package capture.mains;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import capture.commons.Event;
import capture.commons.EventSchema;
import capture.commons.PostEvent;
import db.AnalyticsDB;
import model.TyphonModel;

public class ConsumePostEvents {
	private static Logger logger = Logger.getLogger(ConsumePostEvents.class);

	private static final long WAKEUP_TIME_MS_FREQUENCY = 5000000;
	private static final String KAFKA_CHANNEL_IP = "192.168.1.15";
	private static final String KAFKA_CHANNEL_PORT = "29092";
	private static final String WEBSERVICE_URL = "http://localhost:8080/";
	private static final String WEBSERVICE_USERNAME = "admin";
	private static final String WEBSERVICE_PASSWORD = "admin1@";
	private static final String ANALYTICS_DB_IP = "localhost";
	private static final int ANALYTICS_DB_PORT = 27018;
	private static final String ANALYTICS_DB_USER = "username";
	private static final String ANALYTICS_DB_PWD = "password";
	private static final String ANALYTICS_DB_NAME = "Analytics";

	static {
		PropertyConfigurator.configure(
				System.getProperty("user.dir") + File.separator + "resources" + File.separator + "log4j.properties");

	}

	public static void main(String[] args) throws Exception {
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

				try {

					if (event instanceof PostEvent) {
						PostEvent postEvent = (PostEvent) event;
						if (postEvent.getSuccess() != null && postEvent.getSuccess() == true) {
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
		String query = postEvent.getQuery();
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
