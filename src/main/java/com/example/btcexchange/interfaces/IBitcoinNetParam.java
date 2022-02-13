package com.example.btcexchange.interfaces;

import org.bitcoinj.params.AbstractBitcoinNetParams;

import java.io.File;

public interface IBitcoinNetParam {
    AbstractBitcoinNetParams btcNetParams();
    File getBlockChainFile();
    File getStoredWallet(String name);
}
