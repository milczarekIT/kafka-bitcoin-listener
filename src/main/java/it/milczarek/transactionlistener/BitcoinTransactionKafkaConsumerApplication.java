package it.milczarek.transactionlistener;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.milczarek.transactionlistener.converter.Converter;
import it.milczarek.transactionlistener.converter.JsonToBitcoinTransactionConverter;
import it.milczarek.transactionlistener.domain.BitcoinTransaction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BitcoinTransactionKafkaConsumerApplication extends Thread {

    private final BitcoinTransactionKafkaConsumer kafkaConsumer;

    public BitcoinTransactionKafkaConsumerApplication() {
        super(BitcoinTransactionKafkaConsumerApplication.class.getSimpleName() + "-thread");
        final Converter<String, BitcoinTransaction> converter = new JsonToBitcoinTransactionConverter(new ObjectMapper());
        this.kafkaConsumer = new BitcoinTransactionKafkaConsumer(converter);

        final String className = getClass().getSimpleName();
        Runtime.getRuntime().addShutdownHook(new Thread(className + " Hook") {
            @Override
            public void run() {
                log.info("Closing application: " + className);
                kafkaConsumer.close();
            }
        });
    }

    public static void main(String[] args) {
        new BitcoinTransactionKafkaConsumerApplication().start();
    }

    @Override
    public void run() {
        kafkaConsumer.start();
        while (true) {
            try {
                Thread.sleep(1000L * 10);
            } catch (InterruptedException e) {
                log.error("InterruptedException", e);
                System.exit(-1);
            }
        }
    }
}
