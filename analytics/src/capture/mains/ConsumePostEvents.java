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

public class ConsumePostEvents {
	private static Logger logger = Logger.getLogger(ConsumePostEvents.class);

	static {
		PropertyConfigurator.configure(
				System.getProperty("user.dir") + File.separator + "resources" + File.separator + "log4j.properties");
	}

	public static void main(String[] args) throws Exception {
		if (!initializeQueryParsingPlugin())
			System.exit(1);

		logger.info("Creating new kafka consumer ...");
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", "192.168.1.15:29092");
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
				System.out.println("receiving post event...");
				
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

	protected static void captureQuery(PostEvent postEvent) {
		String query = postEvent.getQuery();
		Date startDate = postEvent.getStartTime();
		Date endDate = postEvent.getEndTime();
		long diff = endDate.getTime() - startDate.getTime();
		Query q = QueryParsing.eval(query);
		
		saveAnalyzedQueryInAnalyticsDB(q);

	}

	private static void saveAnalyzedQueryInAnalyticsDB(Query q) {
		// TODO Auto-generated method stub
		
	}

	private static boolean initializeQueryParsingPlugin() {
		return QueryParsing.init();
	}

}
