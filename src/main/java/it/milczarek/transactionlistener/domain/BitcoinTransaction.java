package it.milczarek.transactionlistener.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
public class BitcoinTransaction {

    private final String txId;
    private final List<String> inputs;
    private final List<String> outputs;

    public BitcoinTransaction(Transaction transaction) {
        this.txId = transaction.getTxId().toString();
        this.inputs = transaction.getInputs().stream().map(TransactionInput::toString).collect(Collectors.toList());
        this.outputs = transaction.getOutputs().stream().map(TransactionOutput::toString).collect(Collectors.toList());
    }

    @JsonCreator
    public BitcoinTransaction(@JsonProperty("txId") String txId, @JsonProperty("inputs") List<String> inputs,
            @JsonProperty("outputs") List<String> outputs) {
        this.txId = txId;
        this.inputs = inputs;
        this.outputs = outputs;
    }
}
