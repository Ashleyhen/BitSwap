package com.example.btcexchange;

import com.example.btcexchange.interfaces.IBitcoinNetParam;
import org.bitcoinj.params.AbstractBitcoinNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.File;

@Configuration
@Primary
public class NetworkConfig implements IBitcoinNetParam {

    @Bean
    public AbstractBitcoinNetParams btcNetParams() {
        return TestNet3Params.get();
    }

    @Bean
    public File getBlockChainFile() {
        return new File("storage/blockchain.bin");

    }

    public File getStoredWallet(String walletName) {
        String dir = "wallet/";
        File file = new File(dir + walletName + ".bin");
        return file;

    }


}
