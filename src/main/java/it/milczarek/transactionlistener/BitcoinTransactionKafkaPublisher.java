package it.milczarek.transactionlistener;

import it.milczarek.transactionlistener.converter.Converter;
import it.milczarek.transactionlistener.domain.BitcoinTransaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.Closeable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Random;

import static org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.CLIENT_ID_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

@Slf4j
public class BitcoinTransactionKafkaPublisher implements Closeable {

    private static final String KAFKA_TRANSACTIONS_TOPIC_NAME = "transactions";
    private final Converter<BitcoinTransaction, String> converter;
    private final Producer<String, String> producer;

    public BitcoinTransactionKafkaPublisher(Converter<BitcoinTransaction, String> converter) {
        this.producer = new KafkaProducer<>(producerProperties());
        this.converter = converter;
    }

    private Properties producerProperties() {
        final Properties props = new Properties();
        props.put(CLIENT_ID_CONFIG, clientId());
        props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ACKS_CONFIG, "0");
        props.put(KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        return props;
    }

    private String clientId() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.warn("UnknownHostException", e);
            return "client-" + new Random().nextInt(1000);
        }
    }

    public void publish(BitcoinTransaction bitcoinTransaction) {
        final ProducerRecord<String, String> record = toRecord(bitcoinTransaction);
        if (record != null) {
            producer.send(record);
            log.trace("Publish key: {} - value: {}", record.key(), record.value());
        } else {
            log.warn("Unable to convert {} to record", bitcoinTransaction);
        }
    }

    private ProducerRecord<String, String> toRecord(BitcoinTransaction bitcoinTransaction) {
        final String convertedValue = converter.convert(bitcoinTransaction);
        if (convertedValue == null) {
            return null;
        } else {
            return new ProducerRecord<>(KAFKA_TRANSACTIONS_TOPIC_NAME, bitcoinTransaction.getTxId(), convertedValue);
        }
    }

    @Override
    public void close() {
        if (producer != null) {
            producer.close();
        }
    }
}
