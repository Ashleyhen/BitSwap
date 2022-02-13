package com.example.btcexchange.service;

import com.example.btcexchange.ContextStates;
import com.example.btcexchange.DTO.WalletDto;
import com.example.btcexchange.interfaces.IBitcoinNetParam;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;
import org.jsoup.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WalletService {
    protected static final String walletName = "temp";
    private final IBitcoinNetParam iBitcoinNetParam;
    private final ContextStates netWorkContextStates;

    public Try<List<WalletDto>> getWallets(String name) {
        return Try.of(this::_extractCredentials);
    }

    public Try<WalletDto> createWallet(String nameId, String passphrase) {
        return Try.of(() -> _createWallet(nameId, passphrase));
    }


    public Wallet extractWalletFromId(String walletId) throws
            IOException, ClassNotFoundException, UnreadableWalletException {

        Map<String, WalletDto> walletMap = _extractCredentials().stream().collect(Collectors.toMap(WalletDto::getNameId, Function.identity()));

        WalletDto toWalletDto = walletMap.get(walletId);

        return convertDtoToWallet(toWalletDto);

    }

    protected Wallet convertDtoToWallet(WalletDto toWalletDto) throws UnreadableWalletException {
        DeterministicSeed deterministicSeed = new DeterministicSeed(StringUtil.join(toWalletDto.getPrivateKey(), " "), null, "", 1409478661L);
        return netWorkContextStates.propagateContext(() ->
                Wallet.fromSeed(iBitcoinNetParam.btcNetParams(), deterministicSeed, Script.ScriptType.P2WPKH)
        );

    }

    private WalletDto _createWallet(String nameId, String passphrase) throws IOException, ClassNotFoundException {
        ArrayList<WalletDto> walletDtoArrayList = _extractCredentials();

        Optional<String> duplicateKeyCheck = walletDtoArrayList.stream().map(WalletDto::getNameId).filter(name -> name.equals(nameId)).findAny();

        String id = "";
        if (!duplicateKeyCheck.isEmpty()) {
            id = nameId + "_" + walletDtoArrayList.size();
        } else {
            id = nameId;
        }
        return _createAWallet(passphrase, walletDtoArrayList, id);
    }

    private WalletDto _createAWallet(String passphrase, ArrayList<WalletDto> walletDtoArrayList, String id) throws IOException {
        DeterministicSeed deterministicSeed = new DeterministicSeed(new SecureRandom(), DeterministicSeed.MAX_SEED_ENTROPY_BITS, passphrase);
        Wallet wallet = netWorkContextStates.propagateContext(() ->
                Wallet.fromSeed(iBitcoinNetParam.btcNetParams(), deterministicSeed, Script.ScriptType.P2WPKH));
        WalletDto walletDto = new WalletDto(id, wallet.currentReceiveAddress().toString(), deterministicSeed.getMnemonicCode(), passphrase, deterministicSeed.getCreationTimeSeconds());
        FileOutputStream fileOutput = new FileOutputStream(iBitcoinNetParam.getBlockChainFile());
        ObjectOutputStream outputStream = new ObjectOutputStream(fileOutput);
        walletDtoArrayList.add(walletDto);
        outputStream.writeObject(walletDtoArrayList);
        fileOutput.close();
        outputStream.close();
        return walletDto;
    }

    protected ArrayList<WalletDto> _extractCredentials() throws IOException, ClassNotFoundException {
        ArrayList<WalletDto> walletDtoArrayList = new ArrayList<>();
        FileInputStream fi = new FileInputStream("storage/temp.wallet");
        ObjectInputStream oi = new ObjectInputStream(fi);
        walletDtoArrayList = (ArrayList<WalletDto>) oi.readObject();
        fi.close();
        oi.close();
        return walletDtoArrayList;
    }

    Map<String, WalletDto> extractCredentialsMap() throws IOException, ClassNotFoundException {
        return _extractCredentials().stream().collect(Collectors.toMap(WalletDto::getNameId, Function.identity()));
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