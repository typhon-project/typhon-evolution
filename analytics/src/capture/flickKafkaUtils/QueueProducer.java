package capture.flickKafkaUtils;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;

import ac.york.typhon.analytics.commons.datatypes.events.Event;
import ac.york.typhon.analytics.commons.serialization.EventSchema;


public class QueueProducer {

	private Producer<Long, Event> producer;
	
    public QueueProducer(String connectionString) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, connectionString);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "polystore-api");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, EventSchema.class);
                
        producer = new KafkaProducer<>(props);
    }
    
    public void produce(String topic, Event message) {
    	ProducerRecord<Long, Event> record 
			= new ProducerRecord<Long, Event>(topic, message);
		try {
			producer.send(record).get();
		} catch (ExecutionException e) {
			System.out.println("Error in sending record");
			System.out.println(e);
		} catch (InterruptedException e) {
	          System.out.println("Error in sending record");
	          System.out.println(e);
		}
    }
}

