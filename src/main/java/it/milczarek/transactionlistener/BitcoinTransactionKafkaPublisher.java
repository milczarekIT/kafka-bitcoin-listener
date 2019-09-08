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
        props.put("client.id", clientId());
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "0");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
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
