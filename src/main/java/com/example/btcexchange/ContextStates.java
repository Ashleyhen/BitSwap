package com.example.btcexchange;

import com.example.btcexchange.utils.IBitcoinNetParam;
import org.bitcoinj.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
public class ContextStates {

    private final Context context;
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

}
