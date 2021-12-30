package com.example.btcexchange.service;

import com.example.btcexchange.utils.IBitcoinNetParam;
import org.bitcoinj.wallet.KeyChainGroup;
import org.bitcoinj.wallet.Wallet;
import org.springframework.stereotype.Service;

// Simple Payment Verification runs
@Service
public record SPVBlockChainService(IBitcoinNetParam iBitcoinNetParam) {
    String simpleConnect() {

        Wallet wallet = new Wallet(iBitcoinNetParam.btcNetParams(), KeyChainGroup.createBasic(iBitcoinNetParam().btcNetParams()));

//        BlockChain blockChain = new BlockChain(iBitcoinNetParam.btcNetParams(), );
        return wallet.getKeyChainSeed().getMnemonicString();
    }

}
