package com.example.btcexchange.service;

import com.example.btcexchange.ContextStates;
import com.example.btcexchange.NetworkConfig;
import com.example.btcexchange.interfaces.IBitcoinNetParam;
import com.example.btcexchange.service.wallet.IImportExportWallet;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.KeyChain;
import org.bitcoinj.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Component("kit")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WalletKitService implements IImportExportWallet<WalletAppKit> {

    private volatile static WalletAppKit walletAppKit;
    private final NetworkConfig networkConfig;
    private final ContextStates contextStates;
    private final IBitcoinNetParam iBitcoinNetParam;

    @Override
    public Try<WalletAppKit> extractWallet(String walletName, String passphrase) {
        if (walletAppKit != null) {
            return Try.of(() -> walletAppKit);
        }
        walletAppKit = contextStates.propagateContext(context ->
                new WalletAppKit(context, new File(NetworkConfig.dir), walletName)
        );
        if (!walletAppKit.isRunning()) {
            walletAppKit.startAsync();
            walletAppKit.awaitRunning();
            Wallet wallet = walletAppKit.wallet();
            wallet.addWatchedAddress(wallet.currentAddress(KeyChain.KeyPurpose.RECEIVE_FUNDS));
        }
        walletAppKit.store();
        return Try.of(() -> walletAppKit);
    }

    @Override
    public Try<WalletAppKit> importWallet(String walletName, String mnemonicString, String passphrase) {
        List<String> mnemonicPhrase = List.of(mnemonicString.split(" "));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
//        P2PKH
        return Try.of(() -> {
            Date date = simpleDateFormat.parse("2022-01-17");
            walletAppKit = contextStates.propagateContext(context ->
                    new WalletAppKit(context, new File(NetworkConfig.dir), walletName)
                            .restoreWalletFromSeed(new DeterministicSeed(mnemonicPhrase, null, passphrase, date.getTime())));
            walletAppKit.startAsync();
            walletAppKit.awaitRunning();
            walletAppKit.store();
            return walletAppKit;
        });

    }

    @Override
    public Try<WalletAppKit> createWallet(String walletName, String passphrase) {
        walletAppKit = contextStates.propagateContext(context ->
                new WalletAppKit(context, Script.ScriptType.P2TR, null, new File(NetworkConfig.dir), walletName)
        );
        walletAppKit.startAsync();
        walletAppKit.awaitRunning();
        return Try.of(() -> walletAppKit);
    }
}
