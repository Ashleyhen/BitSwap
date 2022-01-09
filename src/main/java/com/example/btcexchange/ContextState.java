package com.example.btcexchange;

import com.example.btcexchange.utils.IBitcoinNetParam;
import org.bitcoinj.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class ContextState {

    final Context context;

    @Autowired
    public ContextState(IBitcoinNetParam iBitcoinNetParam) {
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
