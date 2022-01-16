package com.example.btcexchange.service;

import com.example.btcexchange.ContextState;
import com.example.btcexchange.DTO.TransferToDto;
import com.example.btcexchange.DTO.WalletDto;
import com.example.btcexchange.utils.IBitcoinNetParam;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.KeyChain;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.bitcoinj.core.Coin.MICROCOIN;

@Slf4j
@Service
public record WalletService(IBitcoinNetParam iBitcoinNetParam, ContextState netWorkContextState) {
    private final static File file = new File("temp.wallet");

    public Try<List<WalletDto>> getWallets() {
        return Try.of(this::extractCredentials);
    }

    public Try<List<WalletDto>> createWallet(String nameId, String passphrase) {
        return Try.of(() -> addWallet(nameId, passphrase));
    }

    public Try<String> transferTo(TransferToDto transferToDto) {
        CheckedFunction0<Wallet> tryExtractingToWallet = () -> extractWalletFromId(transferToDto.toWallet());

        CheckedFunction0<String> transferFunds = tryExtractingToWallet.andThen(toWallet ->
                payTo(toWallet, extractCredentialsMap().get(transferToDto.fromWallet()).getPublicKey()));


        return Try.of(transferFunds);
    }

    private String payTo(Wallet wallet, String passphrase) throws InsufficientMoneyException {
        Transaction contract = new Transaction(iBitcoinNetParam.btcNetParams());
        Script script = ScriptBuilder.createP2WPKHOutputScript(DeterministicKey.fromPublicOnly(Utils.HEX.decode(passphrase)));
        Coin coinAmount = Coin.ofBtc(MICROCOIN.toBtc());
        TransactionOutput transactionOutput = contract.addOutput(coinAmount, script);
        SendRequest req = netWorkContextState.propagateContext(() -> SendRequest.forTx(contract));
        log.info(String.valueOf(wallet.getKeyChainSeed()));
        wallet.completeTx(req);


        return "Success";
    }


    private Wallet extractWalletFromId(String walletId) throws
            IOException, ClassNotFoundException, MnemonicException.MnemonicWordException, MnemonicException.MnemonicChecksumException, MnemonicException.MnemonicLengthException {

        Map<String, WalletDto> walletMap = extractCredentials().stream().collect(Collectors.toMap(WalletDto::getNameId, Function.identity()));

        WalletDto toWalletDto = walletMap.get(walletId);

        byte[] entropy = MnemonicCode.INSTANCE.toEntropy(toWalletDto.getPrivateKey());
        DeterministicSeed deterministicSeed = new DeterministicSeed(entropy, toWalletDto.getPrivateKey(), toWalletDto.getCreationTime());

        return netWorkContextState.propagateContext(() ->
                Wallet.fromSeed(iBitcoinNetParam.btcNetParams(), deterministicSeed, Script.ScriptType.P2WPKH)
        );

    }

    private ArrayList<WalletDto> addWallet(String nameId, String passphrase) throws IOException, ClassNotFoundException {
        ArrayList<WalletDto> walletDtoArrayList = extractCredentials();

        Optional<String> duplicateKeyCheck = walletDtoArrayList.stream().map(WalletDto::getNameId).filter(name -> name.equals(nameId)).findAny();

        String id = "";
        if (!duplicateKeyCheck.isEmpty()) {
            id = nameId + "_" + walletDtoArrayList.size();
        } else {
            id = nameId;
        }
        createAWallet(passphrase, walletDtoArrayList, id);
        return walletDtoArrayList;
    }

    private WalletDto createAWallet(String passphrase, ArrayList<WalletDto> walletDtoArrayList, String id) throws IOException {
        DeterministicSeed deterministicSeed = new DeterministicSeed(new SecureRandom(), DeterministicSeed.MAX_SEED_ENTROPY_BITS, passphrase);
        Wallet wallet = netWorkContextState.propagateContext(() ->
                Wallet.fromSeed(iBitcoinNetParam.btcNetParams(), deterministicSeed, Script.ScriptType.P2WPKH));
        DeterministicKey pubKey = wallet.freshKey(KeyChain.KeyPurpose.RECEIVE_FUNDS);
        WalletDto walletDto = new WalletDto(id, pubKey.getPublicKeyAsHex(), deterministicSeed.getMnemonicCode(), passphrase, deterministicSeed.getCreationTimeSeconds());
        FileOutputStream fileOutput = new FileOutputStream(file);
        ObjectOutputStream outputStream = new ObjectOutputStream(fileOutput);
        walletDtoArrayList.add(walletDto);
        outputStream.writeObject(walletDtoArrayList);
        fileOutput.close();
        outputStream.close();
        return walletDto;
    }

    private ArrayList<WalletDto> extractCredentials() throws IOException, ClassNotFoundException {
        ArrayList<WalletDto> walletDtoArrayList = new ArrayList<>();
        if (file.exists()) {
            FileInputStream fi = new FileInputStream(file);
            ObjectInputStream oi = new ObjectInputStream(fi);
            walletDtoArrayList = (ArrayList<WalletDto>) oi.readObject();
            fi.close();
            oi.close();
        }
        return walletDtoArrayList;
    }

    private Map<String, WalletDto> extractCredentialsMap() throws IOException, ClassNotFoundException {
        return extractCredentials().stream().collect(Collectors.toMap(WalletDto::getNameId, Function.identity()));
    }

}

/*
netWorkContextState.propagateContext(()->SendRequest.forTx())

        Transaction contract = new Transaction(iBitcoinNetParam.btcNetParams());
//        new DeterministicSeed()
//    DeterministicSeed.DEFAULT_SEED_ENTROPY_BITS


        List<ECKey> keys = ImmutableList.of(clientKey, serverKey);
        Script script = ScriptBuilder.createMultiSigOutputScript(2, keys);
        Coin coinAmount = Coin.valueOf(0, 50);
        TransactionOutput transactionOutput = contract.addOutput(coinAmount, script);
        Wallet wallet = Wallet.createDeterministic(netWorkContextState.getContext(), Script.ScriptType.P2WPKH);
        SendRequest req = netWorkContextState.propagateContext(() -> SendRequest.forTx(contract));

        log.info(String.valueOf(wallet.getKeyChainSeed()));
        wallet.completeTx(req);

        return new ArrayList<>();
 */