package com.example.btcexchange.interfaces;

import io.vavr.control.Try;

public interface IImportExportWallet<T> {

    Try<T> extractWallet(String walletName, String passphrase);

    Try<T> importWallet(String walletName, String mnemonicString, String passphrase);

    Try<T> createWallet(String walletName, String passphrase);

    void terminate();
}
