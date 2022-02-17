package com.example.btcexchange.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bitcoinj.wallet.Wallet;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WalletDto implements Serializable {
    private String nameId;
    private String publicKey;
    private List<String> privateKey;
    private String passphrase;
    private String currentBalance;

    public WalletDto(Wallet wallet, String nameId, String passphrase) {
        publicKey = wallet.currentReceiveAddress().toString();
        privateKey = wallet.getKeyChainSeed().getMnemonicCode();
        this.passphrase = passphrase;
        this.nameId = nameId;
    }

    public WalletDto(Wallet wallet) {
        publicKey = wallet.currentReceiveAddress().toString();
        privateKey = wallet.getKeyChainSeed().getMnemonicCode();
        currentBalance = wallet.getBalance().toFriendlyString();
    }

    public WalletDto setNameId(String nameId) {
        this.nameId = nameId;
        return this;
    }

    public WalletDto setPassphrase(String passphrase) {
        this.passphrase = passphrase;
        return this;
    }

}