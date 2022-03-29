package com.example.btcexchange;


import com.example.btcexchange.ldk.Constants;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;

import java.io.File;

public class BtcExchangeApplication {
    public static void main(String[] args) {
        System.out.println("hello");
        WalletAppKit walletAppKit = new WalletAppKit(Constants.networkParameters,new File("testnet"),"segwit");
        walletAppKit.startAsync();
        walletAppKit.awaitRunning();
        System.out.println("end "+walletAppKit.wallet().getBalance());

    }

//    http://localhost:8080/swagger-ui/#/btc-request-end-point
}
