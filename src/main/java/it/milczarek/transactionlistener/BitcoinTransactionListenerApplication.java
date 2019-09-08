package it.milczarek.transactionlistener;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.milczarek.transactionlistener.converter.BitcoinTransactionToJsonConverter;
import it.milczarek.transactionlistener.converter.Converter;
import it.milczarek.transactionlistener.domain.BitcoinTransaction;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.params.MainNetParams;

/**
 * An experimental Bitcoin transaction listener that sends transactions events from Bitcoin P2P network into Kafka topic.
 */
@Slf4j
public class BitcoinTransactionListenerApplication extends Thread {

    private final BitcoinTransactionKafkaPublisher kafkaPublisher;
    private PeerGroup peerGroup;

    public BitcoinTransactionListenerApplication() {
        final Converter<BitcoinTransaction, String> converter = new BitcoinTransactionToJsonConverter(new ObjectMapper());
        kafkaPublisher = new BitcoinTransactionKafkaPublisher(converter);

        setupNetwork();
        setupListeners();

        Runtime.getRuntime().addShutdownHook(new Thread("kafka-bitcoin-listener-hook") {
            @Override
            public void run() {
                log.info("Closing application");
                peerGroup.stop();
                kafkaPublisher.close();
            }
        });
    }

    public static void main(String[] args) {
        new BitcoinTransactionListenerApplication().start();
    }

    @Override
    public void run() {
        peerGroup.start();
        while (true) {
            try {
                Thread.sleep(1000L * 10);
            } catch (InterruptedException e) {
                log.error("InterruptedException", e);
                System.exit(-1);
            }
        }
    }

    private void setupNetwork() {
        // create network params
        NetworkParameters params = MainNetParams.get();

        // create a context object to hold the parameters
        Context context = new Context(params);

        // set up the peer group
        peerGroup = new PeerGroup(context);
        peerGroup.setUserAgent(getClass().getSimpleName(), "1.0");
        peerGroup.setMaxConnections(1);
        peerGroup.addPeerDiscovery(new DnsDiscovery(params));
    }

    private void setupListeners() {
        peerGroup.addOnTransactionBroadcastListener((peer, transaction) -> {
            final BitcoinTransaction bitcoinTransaction = new BitcoinTransaction(transaction);

            log.debug(">>> transaction: {}", transaction);
            log.debug(">>> bitcoinTransaction: {}", bitcoinTransaction);
            kafkaPublisher.publish(bitcoinTransaction);
        });
    }
}

