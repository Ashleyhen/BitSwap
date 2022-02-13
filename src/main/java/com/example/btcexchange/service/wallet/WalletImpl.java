package com.example.btcexchange.service.wallet;

import com.example.btcexchange.DTO.WalletDto;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.bitcoinj.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("impl")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WalletImpl implements IImportExportWallet<WalletDto> {

    private final IImportExportWallet<Wallet> walletStorageService;

    public Try<WalletDto> extractWallet(String walletName, String passphrase) {
        return walletStorageService.extractWallet(walletName, passphrase).map(wallet -> new WalletDto().toWalletDto(wallet).setNameId(walletName).setPassphrase(passphrase));
    }

    public Try<WalletDto> importWallet(String walletName, String mnemonicString, String passphrase) {
        return walletStorageService.importWallet(walletName, mnemonicString, passphrase).map(wallet -> new WalletDto().toWalletDto(wallet).setNameId(walletName).setPassphrase(passphrase));
    }

    public Try<WalletDto> createWallet(String walletName, String passphrase) {
        return walletStorageService.createWallet(walletName, passphrase).map(wallet -> new WalletDto().toWalletDto(wallet).setNameId(walletName).setPassphrase(passphrase));
    }
}
