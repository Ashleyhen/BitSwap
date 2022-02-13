package com.example.btcexchange.service;

import com.example.btcexchange.ContextStates;
import com.example.btcexchange.DTO.TransferDto;
import com.example.btcexchange.interfaces.IBitcoinNetParam;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import static org.bitcoinj.core.Coin.MILLICOIN;

@Component
@Slf4j
@Primary
public class TransferService extends WalletService  {
    private final IBitcoinNetParam iBitcoinNetParam;
    private final ContextStates netWorkContextStates;
    private final PeerDiscoveryService peerDiscoveryService;
    protected PeerGroup peerGroup;


    @Autowired
    public TransferService(IBitcoinNetParam iBitcoinNetParam, ContextStates netWorkContextStates, PeerDiscoveryService peerDiscoveryService) {
        super(iBitcoinNetParam, netWorkContextStates);
        this.iBitcoinNetParam = iBitcoinNetParam;
        this.netWorkContextStates = netWorkContextStates;
        this.peerDiscoveryService = peerDiscoveryService;
    }

    /*
        @PostConstruct
        private void postConstruct() {

            CheckedFunction0<Stream<org.bitcoinj.wallet.Wallet>> extractWalletDtoStream = () ->
                    this._extractCredentials().stream().map(walletDto -> CheckedFunction0.of(() -> convertDtoToWallet(walletDto)).unchecked().apply());

            Try<PeerGroup> blockChainTry = Try.withResources(extractWalletDtoStream)
                    .of(walletStream -> peerDiscoveryService.findPeer(walletStream.collect(Collectors.toList())).apply());
            peerGroup = blockChainTry.get();
        }
    */
    public Try<String> transferTo(TransferDto transferToDto) {

        CheckedFunction0<org.bitcoinj.wallet.Wallet> tryExtractingToWallet = () -> extractWalletFromId(transferToDto.fromWallet());
//        CheckedFunction0<BlockChain> connectToBlockChain = tryExtractingToWallet
//                .andThen(wallet -> peerDiscoveryService.findPeer(wallet).apply());


//        Try.of(connectToBlockChain).get();

        CheckedFunction0<String> transferFunds = tryExtractingToWallet.andThen(fromWallet ->
                _payTo(fromWallet, extractCredentialsMap().get(transferToDto.toWallet()).getPublicKey()));

        return Try.of(transferFunds);
    }

    public Try<Wallet> importWallet(String walletName, String seed, String passphrase) {
        return null;
    }

    private String _payTo(org.bitcoinj.wallet.Wallet wallet, String passphrase) {
        Transaction contract = new Transaction(iBitcoinNetParam.btcNetParams());
        Address address = Address.fromString(iBitcoinNetParam.btcNetParams(), passphrase);

        Script script = ScriptBuilder.createP2WPKHOutputScript(address.getHash());
        Coin coinAmount = Coin.ofBtc(MILLICOIN.toBtc());
        try {
            String str = wallet.getBalance(org.bitcoinj.wallet.Wallet.BalanceType.ESTIMATED).toFriendlyString();
            log.info(str);
            wallet.sendCoins(peerGroup, SendRequest.to(address, coinAmount));
        } catch (InsufficientMoneyException insufficientMoneyException) {

            ListenableFuture<Coin> listenableFuture = wallet.getBalanceFuture(coinAmount, org.bitcoinj.wallet.Wallet.BalanceType.AVAILABLE);
            FutureCallback<Coin> callback = new FutureCallback<>() {
                @Override
                public void onSuccess(Coin result) {
                    log.info("wallet: " + result.toFriendlyString());
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                    log.error(t.getMessage());

                }
            };

            Futures.addCallback(listenableFuture, callback, (execute) -> {
                execute.run();
                TransactionOutput transactionOutput = contract.addOutput(coinAmount, script);
                SendRequest req = netWorkContextStates.propagateContext(() -> SendRequest.forTx(contract));
                log.info(String.valueOf(wallet.getKeyChainSeed()));


                try {
                    wallet.completeTx(req);
                } catch (InsufficientMoneyException e) {
                    e.printStackTrace();
                }

            });

        }

        /*
        TransactionOutput transactionOutput = contract.addOutput(coinAmount, script);
        SendRequest req = netWorkContextStates.propagateContext(() -> SendRequest.forTx(contract));
        log.info(String.valueOf(wallet.getKeyChainSeed()));


        if (wallet.getBalance().isLessThan(coinAmount)) {
            throw new InsufficientMoneyException(coinAmount, "Insufficient funds " + wallet.getBalance().subtract(coinAmount));
        } else {
            wallet.completeTx(req);
        } */
        return "Success";
    }

}
