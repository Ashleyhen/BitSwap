package com.example.btcexchange;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.springframework.stereotype.Service;

@Service
public class NetworkParamConfig {
    public void config() {
        NetworkParameters networkParameters = TestNet3Params.get();
    }

}
