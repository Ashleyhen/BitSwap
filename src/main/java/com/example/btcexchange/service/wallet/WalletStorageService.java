package com.example.btcexchange.service.wallet;

import com.example.btcexchange.ContextStates;
import com.example.btcexchange.interfaces.IBitcoinNetParam;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.bitcoinj.core.Utils;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.File;
import java.security.SecureRandom;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Primary
public class WalletStorageService implements IImportExportWallet<Wallet> {
    private final IBitcoinNetParam iBitcoinNetParam;
    private final ContextStates netWorkContextStates;

    @Override
    public Try<Wallet> extractWallet(String walletName, String passphrase) {
        CheckedFunction0<Wallet> checkedFunction0 = () -> Wallet.loadFromFile(iBitcoinNetParam.getStoredWallet(walletName));
        return Try.of(netWorkContextStates.propagateContext(() -> checkedFunction0));
    }

    @Override
    public Try<Wallet> importWallet(String walletName, String mnemonicString, String passphrase) {
        List<String> mnemonicPhrase = List.of(mnemonicString.split(" "));

        return Try.of(() -> {
            DeterministicSeed deterministicSeed = new DeterministicSeed(mnemonicPhrase, null, passphrase, Utils.now().getTime());
            Wallet wallet = netWorkContextStates.propagateContext((context) ->
                    Wallet.fromSeed(context, deterministicSeed, Script.ScriptType.P2WPKH));
            File file = iBitcoinNetParam.getStoredWallet(walletName);
            if (!file.exists()) {
                file.createNewFile();
            }
            wallet.saveToFile(file);
            return wallet;
        });

    }

    @Override
    public Try<Wallet> createWallet(String walletName, String passphrase) {
        DeterministicSeed deterministicSeed = new DeterministicSeed(new SecureRandom(), DeterministicSeed.MAX_SEED_ENTROPY_BITS, passphrase);
        Wallet wallet = netWorkContextStates.propagateContext((context) ->
                Wallet.fromSeed(context, deterministicSeed, Script.ScriptType.P2WPKH));
        CheckedFunction0<Wallet> checkedFunction0 = () -> {
            File file = iBitcoinNetParam.getStoredWallet(walletName);
            if (!file.exists()) {
                file.createNewFile();
            }
            wallet.saveToFile(file);
            return wallet;
        };
        return Try.of(checkedFunction0);
    }

    @Override
    public String terminate() {
        return null;
    }

}
