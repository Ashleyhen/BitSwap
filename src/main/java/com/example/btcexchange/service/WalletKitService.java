package com.example.btcexchange.service;

import com.example.btcexchange.ContextStates;
import com.example.btcexchange.interfaces.IImportExportWallet;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.KeyChain;
import org.bitcoinj.wallet.KeyChainGroupStructure;
import org.bitcoinj.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.File;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Primary
public class WalletKitService implements IImportExportWallet<WalletAppKit> {

    private volatile static WalletAppKit walletAppKit;
    private final ContextStates contextStates;

    public static WalletAppKit getWalletAppKit() {
        return walletAppKit;
    }

    @Override
    public Try<WalletAppKit> extractWallet(String walletName, String passphrase) {
        if (walletAppKit != null && walletAppKit.isRunning()) {
            return Try.of(() -> walletAppKit);
        }
        walletAppKit = contextStates.propagateContext(context ->
                new WalletAppKit(context, Script.ScriptType.P2WPKH, KeyChainGroupStructure.DEFAULT, new File(ContextStates.dir), walletName)
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
            Date date = simpleDateFormat.parse("2022-02-14");

            walletAppKit = contextStates.propagateContext(context ->
                    new WalletAppKit(context, Script.ScriptType.P2WPKH, KeyChainGroupStructure.DEFAULT, new File(ContextStates.dir), walletName)
                            .restoreWalletFromSeed(new DeterministicSeed(mnemonicPhrase, null, passphrase, date.getTime())));
            walletAppKit.setWalletFactory((networkParameters, keyChainGroup) -> {
                Wallet wallet = new Wallet(networkParameters, keyChainGroup);
                wallet.addWatchedAddress(wallet.currentAddress(KeyChain.KeyPurpose.RECEIVE_FUNDS));
                return wallet;
            });
            walletAppKit.startAsync();
            walletAppKit.awaitRunning();
            walletAppKit.store();
            return walletAppKit;
        });

    }

    @Override
    public Try<WalletAppKit> createWallet(String walletName, String passphrase) {
        DeterministicSeed deterministicSeed = new DeterministicSeed(new SecureRandom(), DeterministicSeed.MAX_SEED_ENTROPY_BITS, passphrase);
        walletAppKit = contextStates.propagateContext(context ->
                new WalletAppKit(context, Script.ScriptType.P2WPKH, KeyChainGroupStructure.DEFAULT, new File(ContextStates.dir), walletName)
        ).restoreWalletFromSeed(deterministicSeed);
        walletAppKit.startAsync();
        walletAppKit.awaitRunning();
        return Try.of(() -> walletAppKit);
    }

    @Override
    public void terminate() {
        walletAppKit.store();
        walletAppKit.stopAsync();
    }
}
