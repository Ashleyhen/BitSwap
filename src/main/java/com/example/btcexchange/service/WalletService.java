package com.example.btcexchange.service;

import com.example.btcexchange.ContextStates;
import com.example.btcexchange.DTO.TransferToDto;
import com.example.btcexchange.DTO.WalletDto;
import com.example.btcexchange.utils.IBitcoinNetParam;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.bitcoinj.core.Coin.MILLICOIN;

@Slf4j
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class WalletService {
    private final static File file = new File("storage/temp.wallet");
    private final IBitcoinNetParam iBitcoinNetParam;
    private final ContextStates netWorkContextStates;
    private final PeerDiscoveryService peerDiscoveryService;

    @PostConstruct
    private void postConstruct() {

        CheckedFunction0<Stream<Wallet>> extractWalletDtoStream = () ->
                this._extractCredentials().stream().map(walletDto -> CheckedFunction0.of(() -> convertDtoToWallet(walletDto)).unchecked().apply());

        Try<BlockChain> blockChainTry = Try.withResources(extractWalletDtoStream)
                .of(walletStream -> peerDiscoveryService.findPeer(walletStream.collect(Collectors.toList())).apply());


        blockChainTry.get();


    }


    public Try<List<WalletDto>> getWallets() {
        return Try.of(this::_extractCredentials);
    }

    public Try<WalletDto> createWallet(String nameId, String passphrase) {
        return Try.of(() -> _createWallet(nameId, passphrase));
    }

    public Try<String> transferTo(TransferToDto transferToDto) {

        CheckedFunction0<Wallet> tryExtractingToWallet = () -> _extractWalletFromId(transferToDto.fromWallet());
//        CheckedFunction0<BlockChain> connectToBlockChain = tryExtractingToWallet
//                .andThen(wallet -> peerDiscoveryService.findPeer(wallet).apply());


//        Try.of(connectToBlockChain).get();

        CheckedFunction0<String> transferFunds = tryExtractingToWallet.andThen(fromWallet ->
                _payTo(fromWallet, _extractCredentialsMap().get(transferToDto.toWallet()).getPublicKey()));

        return Try.of(transferFunds);
    }

    private String _payTo(Wallet wallet, String passphrase) throws InsufficientMoneyException {
        Transaction contract = new Transaction(iBitcoinNetParam.btcNetParams());
        Address address = Address.fromString(iBitcoinNetParam.btcNetParams(), passphrase);

        Script script = ScriptBuilder.createP2WPKHOutputScript(address.getHash());
        Coin coinAmount = Coin.ofBtc(MILLICOIN.toBtc());
        TransactionOutput transactionOutput = contract.addOutput(coinAmount, script);
        SendRequest req = netWorkContextStates.propagateContext(() -> SendRequest.forTx(contract));
        log.info(String.valueOf(wallet.getKeyChainSeed()));

        if (wallet.getBalance().isLessThan(coinAmount)) {
            throw new InsufficientMoneyException(coinAmount, "Insufficient funds " + wallet.getBalance().subtract(coinAmount));
        } else {
            wallet.completeTx(req);
        }


        return "Success";
    }


    private Wallet _extractWalletFromId(String walletId) throws
            IOException, ClassNotFoundException, MnemonicException.MnemonicWordException, MnemonicException.MnemonicChecksumException, MnemonicException.MnemonicLengthException {

        Map<String, WalletDto> walletMap = _extractCredentials().stream().collect(Collectors.toMap(WalletDto::getNameId, Function.identity()));

        WalletDto toWalletDto = walletMap.get(walletId);

        return convertDtoToWallet(toWalletDto);

    }

    private Wallet convertDtoToWallet(WalletDto toWalletDto) throws MnemonicException.MnemonicLengthException, MnemonicException.MnemonicWordException, MnemonicException.MnemonicChecksumException {
        byte[] entropy = MnemonicCode.INSTANCE.toEntropy(toWalletDto.getPrivateKey());
        DeterministicSeed deterministicSeed = new DeterministicSeed(entropy, toWalletDto.getPrivateKey(), toWalletDto.getCreationTime());
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
        FileOutputStream fileOutput = new FileOutputStream(file);
        ObjectOutputStream outputStream = new ObjectOutputStream(fileOutput);
        walletDtoArrayList.add(walletDto);
        outputStream.writeObject(walletDtoArrayList);
        fileOutput.close();
        outputStream.close();
        return walletDto;
    }

    private ArrayList<WalletDto> _extractCredentials() throws IOException, ClassNotFoundException {
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

    private Map<String, WalletDto> _extractCredentialsMap() throws IOException, ClassNotFoundException {
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