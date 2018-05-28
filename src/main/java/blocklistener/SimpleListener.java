package blocklistener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.bitcoinj.core.*;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.params.MainNetParams;

/**
 * Experimental Bitcoin listener that sends events in the Bitcoin P2P network into Kafka.
 */
public class SimpleListener {
    // bitcoinj config
    private NetworkParameters params;
    private Context context;
    private PeerGroup peerGroup;

    // kafka config
    private KafkaBlockchainProducer producer;

    // resuse a single jackson mapper for efficiency
    private ObjectMapper mapper;



    public static void main(String[] args) throws Exception {
//        BriefLogFormatter.init();
        new SimpleListener();
    }


    public SimpleListener() throws Exception {
        setupNetwork();
        setupListeners();

        producer = new KafkaBlockchainProducer();

        mapper = new ObjectMapper();

        peerGroup.start();
        Thread.sleep(1000 * 60 * 30);
        peerGroup.stop();

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
            try {
                producer.sendData(
                        "transactions",
                        "newTransaction",
                        "txJSON: " + mapper.writeValueAsString(transaction));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        });

        // peerGroup.addDiscoveredEventListener(peerAddresses -> {
        //     for (PeerAddress addr : peerAddresses) {
        //         System.out.println(addr);
        //     }
        // });
    }
}

