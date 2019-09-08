package blocklistener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.params.MainNetParams;

/**
 * Experimental Bitcoin listener that sends events in the Bitcoin P2P network into Kafka.
 */
@Slf4j
public class SimpleListener {
    // bitcoinj config
    private NetworkParameters params;
    private Context context;
    private PeerGroup peerGroup;

    // kafka config
    private KafkaBlockchainProducer producer;

    // resuse a single jackson mapper for efficiency
    private ObjectMapper mapper;


    public SimpleListener() throws Exception {
        setupNetwork();
        setupListeners();

        producer = new KafkaBlockchainProducer();

        mapper = new ObjectMapper();

        peerGroup.start();
        Thread.sleep(1000 * 60 * 30);
        peerGroup.stop();

    }

    public static void main(String[] args) throws Exception {
        new SimpleListener();
    }

    private void setupNetwork() {
        // create network params
        params = MainNetParams.get();

        // create a context object to hold the parameters
        context = new Context(params);

        // set up the peer group
        peerGroup = new PeerGroup(context);
        peerGroup.setUserAgent("SimpleListener", "1.0");
        peerGroup.setMaxConnections(1);
        peerGroup.addPeerDiscovery(new DnsDiscovery(params));
    }

    private void setupListeners() {
        // callback for peer connection
        peerGroup.addConnectedEventListener((peer, peerCount) -> {
            System.out.println("Peer connected: " + peer);
            producer.sendData("transactions", "newTransaction", "peer connected: " + peer.toString());
        });

        // callback for peer disconnection
        peerGroup.addDisconnectedEventListener((peer, peerCount) -> {
            System.out.println("Peer disconnected: " + peer);
            producer.sendData("transactions", "newTransaction", "peer disconnected: " + peer.toString());

        });

        peerGroup.addOnTransactionBroadcastListener((peer, transaction) -> {
            // System.out.println("New transaction " + transaction);

            // simple produce
            // producer.sendData("transactions", "newTransaction", "transaction: " + transaction.toString());

            // json producer
            BlockchainTransaction tx = new BlockchainTransaction(transaction);

            log.info(">>> transaction: {}", transaction);
            log.info(">>> tx: {}", tx);
            try {
                producer.sendData(
                        "transactions",
                        "newTransaction",
                        "txJSON: " + mapper.writeValueAsString(tx));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                System.exit(-1);
            }

        });

        // peerGroup.addDiscoveredEventListener(peerAddresses -> {
        //     for (PeerAddress addr : peerAddresses) {
        //         System.out.println(addr);
        //     }
        // });
    }
}

