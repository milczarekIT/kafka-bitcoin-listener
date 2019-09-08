package it.milczarek.transactionlistener.converter;

public interface Converter<I, O> {

    O convert(I input);
}
