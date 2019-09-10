package it.milczarek.transactionlistener;

import it.milczarek.transactionlistener.converter.Converter;
import it.milczarek.transactionlistener.domain.BitcoinTransaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

@Slf4j
public class BitcoinTransactionKafkaConsumer extends Thread implements Closeable {

    private static final String KAFKA_TRANSACTIONS_TOPIC_NAME = "transactions";

    private final Consumer<String, String> consumer;
    private final Converter<String, BitcoinTransaction> converter;
    private boolean running;

    public BitcoinTransactionKafkaConsumer(Converter<String, BitcoinTransaction> converter) {
        this.converter = converter;
        this.consumer = new KafkaConsumer<>(consumerProperties());
        this.consumer.subscribe(Collections.singletonList(KAFKA_TRANSACTIONS_TOPIC_NAME));
    }

    private Properties consumerProperties() {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(GROUP_ID_CONFIG, "test");
        props.put(ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        return props;
    }

    @Override
    public void run() {
        this.running = true;
        while (this.running) {
            final ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            records.forEach(this::handleRecord);
        }
        consumer.close();
    }

    private void handleRecord(ConsumerRecord<String, String> record) {
        final BitcoinTransaction bitcoinTransaction = converter.convert(record.value());
        log.debug("offset= {}, key= {}, value= {}", record.offset(), record.key(), bitcoinTransaction);
    }

    @Override
    public void close() {
        this.running = false;
    }
}
