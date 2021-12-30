package com.example.btcexchange;

import com.example.btcexchange.utils.IBitcoinNetParam;
import org.bitcoinj.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NetworkContext {

    final Context context;

    @Autowired
    public NetworkContext(IBitcoinNetParam iBitcoinNetParam) {
        context = new Context(iBitcoinNetParam.btcNetParams());
        Context.propagate(context);
    }

    public Context getContext() {
        return context;
    }
}
