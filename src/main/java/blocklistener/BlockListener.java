package blocklistener;


public class BlockListener {

    public static void main(String[] args) throws Exception {
        KafkaBlockchainProducer producer = new KafkaBlockchainProducer();

        for (int i = 0; i < 100; i++)
            producer.sendData("transactions", Integer.toString(i), Integer.toString(i));

    }
}
