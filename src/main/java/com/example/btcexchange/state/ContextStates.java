package com.example.btcexchange.state;

import org.bitcoinj.core.Context;
import org.bitcoinj.params.AbstractBitcoinNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class ContextStates {

    public static final String dir = "testnet";
    private static volatile Context context;
    private final TestNet3Params testNet3Params = new TestNet3Params();


    @Autowired
    public ContextStates() {
        context = new Context(testNet3Params);
        Context.propagate(context);
    }

    public <T> T propagateContext(Function<AbstractBitcoinNetParams, T> prop) {
        Context.propagate(context);
        return prop.apply(testNet3Params);
    }

}
