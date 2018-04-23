package blocklistener;

import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Coin;

public class BlockchainTransaction {

    private Sha256Hash hash;
    private String hashAsString;
    private Coin fee;
    private Coin inputSum;
    private Coin outputSum;

    public BlockchainTransaction(Transaction transaction) {
        hash = transaction.getHash();
        hashAsString = transaction.getHashAsString();
        fee = transaction.getFee();
        inputSum = transaction.getInputSum();
        outputSum = transaction.getOutputSum();
    }

    public Sha256Hash getHash() {
        return hash;
    }

    public String getHashAsString() {
        return hashAsString;
    }

    public Coin getInputSum() {
        return inputSum;
    }

    public Coin getOutputSum() {
        return outputSum;
    }
}
