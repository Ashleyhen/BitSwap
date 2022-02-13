package com.example.btcexchange;

import com.example.btcexchange.interfaces.IBitcoinNetParam;
import org.bitcoinj.core.Context;
import org.bitcoinj.params.AbstractBitcoinNetParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;
import java.util.function.Supplier;

@Configuration
public class ContextStates {

    private static volatile Context context;
    private final IBitcoinNetParam iBitcoinNetParam;


    @Autowired
    public ContextStates(IBitcoinNetParam iBitcoinNetParam) {
        this.iBitcoinNetParam = iBitcoinNetParam;
        context = new Context(iBitcoinNetParam.btcNetParams());
        Context.propagate(context);
    }

    public Context getContext() {
        return context;
    }

    public <T> T propagateContext(Supplier<T> prop) {
        Context.propagate(context);
        return prop.get();
    }

    public <T> T propagateContext(Function<AbstractBitcoinNetParams, T> prop) {
        Context.propagate(context);
        return prop.apply(iBitcoinNetParam.btcNetParams());
    }

}
