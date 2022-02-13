package com.example.btcexchange.service;

import com.example.btcexchange.ContextStates;
import com.example.btcexchange.NetworkConfig;
import com.example.btcexchange.interfaces.IBitcoinNetParam;
import com.example.btcexchange.service.wallet.IImportExportWallet;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.bitcoinj.kits.WalletAppKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component("kit")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WalletKitService implements IImportExportWallet<WalletAppKit> {

    private final NetworkConfig networkConfig;
    private final ContextStates contextStates;
    private final IBitcoinNetParam iBitcoinNetParam;


    @Override
    public Try<WalletAppKit> extractWallet(String walletName, String passphrase) {
        WalletAppKit walletAppKit = contextStates.propagateContext(context ->
                new WalletAppKit(context, new File("storage"), iBitcoinNetParam.getStoredWallet(walletName) + ".binary")
        );
        walletAppKit.startAsync();
        return Try.of(() -> walletAppKit);
    }

    @Override
    public Try<WalletAppKit> importWallet(String walletName, String mnemonicString, String passphrase) {
        return null;
    }

    @Override
    public Try<WalletAppKit> createWallet(String walletName, String passphrase) {
        return null;
    }
}
