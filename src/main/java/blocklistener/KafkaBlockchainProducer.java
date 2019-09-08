package blocklistener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

@Slf4j
public class KafkaBlockchainProducer {

    private Producer<String, String> producer;

    public KafkaBlockchainProducer() throws UnknownHostException {

        // create a new kafka configuration
        Properties config = new Properties();
        config.put("client.id", InetAddress.getLocalHost().getHostName());
        config.put("bootstrap.servers", "localhost:9092");
        config.put("acks", "0");
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // create the producer
        producer = new KafkaProducer<>(config);
    }

    public void sendData(String topic, String key, String value) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
        producer.send(record);
        log.trace("Send message to topic: {}. Key: {} - value: {}", topic, key, value);
    }


}
