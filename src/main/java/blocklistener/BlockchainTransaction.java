package blocklistener;

import lombok.Getter;
import lombok.ToString;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;

@Getter
@ToString
public class BlockchainTransaction {

    private String txId;
    private Coin fee;
    private Coin inputSum;
    private Coin outputSum;

    public BlockchainTransaction(Transaction transaction) {
        txId = transaction.getTxId().toString();
        fee = transaction.getFee();
        inputSum = transaction.getInputSum();
        outputSum = transaction.getOutputSum();
    }

}
