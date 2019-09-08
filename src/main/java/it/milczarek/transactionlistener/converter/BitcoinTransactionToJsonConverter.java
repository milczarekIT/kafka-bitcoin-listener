package it.milczarek.transactionlistener.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.milczarek.transactionlistener.domain.BitcoinTransaction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BitcoinTransactionToJsonConverter implements Converter<BitcoinTransaction, String> {

    private final ObjectMapper objectMapper;

    public BitcoinTransactionToJsonConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String convert(BitcoinTransaction bitcoinTransaction) {
        try {
            return objectMapper.writeValueAsString(bitcoinTransaction);
        } catch (JsonProcessingException e) {
            log.error("Unable to serialize {}", bitcoinTransaction, e);
            return null;
        }
    }
}
