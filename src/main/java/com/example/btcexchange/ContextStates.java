package com.example.btcexchange;

import com.example.btcexchange.service.PeerDiscoveryService;
import com.example.btcexchange.utils.IBitcoinNetParam;
import io.vavr.CheckedFunction0;
import io.vavr.Function2;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.Context;
import org.bitcoinj.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
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
