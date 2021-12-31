package com.example.btcexchange.service;

import com.example.btcexchange.ContextState;
import com.example.btcexchange.DTO.WalletDto;
import com.example.btcexchange.utils.IBitcoinNetParam;
import com.google.common.collect.ImmutableList;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.KeyChain;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.springframework.stereotype.Service;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.io.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public record WalletService(IBitcoinNetParam iBitcoinNetParam, ContextState netWorkContextState) {


    public String payToWallet() {
        ECKey clientKey = new ECKey();
        ECKey serverKey = new ECKey();

        Transaction contract = new Transaction(iBitcoinNetParam.btcNetParams());
        List<ECKey> keys = ImmutableList.of(clientKey, serverKey);
        Script script = ScriptBuilder.createMultiSigOutputScript(2, keys);
        Coin amount = Coin.valueOf(0, 50);
        TransactionOutput transactionOutput = contract.addOutput(amount, script);
        Wallet wallet = Wallet.createDeterministic(netWorkContextState.getContext(), Script.ScriptType.P2WPKH);

        SendRequest req = netWorkContextState.propagateContext(() -> SendRequest.forTx(contract));

        log.info(String.valueOf(wallet.getKeyChainSeed()));
        try {
            wallet.completeTx(req);
        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
        }
        return transactionOutput.toString();
    }

    public Try<List<WalletDto>> createWallet(String nameId, String passphrase) {

        DeterministicSeed deterministicSeed = new DeterministicSeed(new SecureRandom(), 128, passphrase);
        Wallet wallet = netWorkContextState.propagateContext(() ->
                Wallet.fromSeed(iBitcoinNetParam.btcNetParams(), deterministicSeed, Script.ScriptType.P2WPKH));
        DeterministicKey pubKey = wallet.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        Try<List<WalletDto>> walletDtoTry = Try.of(() -> {
            File file = new File("temp.wallet");
            ArrayList<WalletDto> walletDtoArrayList = new ArrayList<>();
            if (file.exists()) {
                FileInputStream fi = new FileInputStream(file);
                ObjectInputStream oi = new ObjectInputStream(fi);
                walletDtoArrayList = (ArrayList<WalletDto>) oi.readObject();
            }

            Optional<String> duplicateKeyCheck = walletDtoArrayList.stream().map(WalletDto::name).filter(name -> name.equals(nameId)).findAny();
            duplicateKeyCheck.ifPresent(t -> {
                throw new KeyAlreadyExistsException("please choose a name that doesn't exist in the list " + nameId);
            });

            WalletDto walletDto = new WalletDto(nameId, pubKey.getPublicKeyAsHex(), deterministicSeed.getMnemonicString());
            FileOutputStream fileOutput = new FileOutputStream(file);
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutput);
            walletDtoArrayList.add(walletDto);
            outputStream.writeObject(walletDtoArrayList);
            fileOutput.close();
            outputStream.close();
            return walletDtoArrayList;
        });
        return walletDtoTry;
    }

}
