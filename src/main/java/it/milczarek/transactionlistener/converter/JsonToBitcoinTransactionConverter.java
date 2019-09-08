package it.milczarek.transactionlistener.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.milczarek.transactionlistener.domain.BitcoinTransaction;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class JsonToBitcoinTransactionConverter implements Converter<String, BitcoinTransaction> {

    private final ObjectMapper objectMapper;

    public JsonToBitcoinTransactionConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public BitcoinTransaction convert(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, BitcoinTransaction.class);
        } catch (IOException e) {
            log.error("Unable to deserialize {}", jsonString, e);
            return null;
        }
    }
}
