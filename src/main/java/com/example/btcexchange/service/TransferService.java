package com.example.btcexchange.service;

import com.example.btcexchange.service.wallet.WalletKitService;
import com.example.btcexchange.state.ContextStates;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.net.discovery.SeedPeers;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.bouncycastle.crypto.engines.SEEDEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransferService {

    private final ContextStates contextStates;

    public String transferTo(String toAddress, Long sats, Boolean drain) {
        WalletAppKit walletAppKit = WalletKitService.getWalletAppKit();
        Address address = contextStates.propagateContext(context -> Address.fromString(context, toAddress));
        Wallet wallet = walletAppKit.wallet();

        return Try.of(() -> {

            SendRequest req = SendRequest.to(address, Coin.ofSat(sats));
            req.setFeePerVkb(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE);
            req.changeAddress = drain ? address : wallet.currentChangeAddress();
            req.memo = "hello world";
            Wallet.SendResult result = wallet.sendCoins(walletAppKit.peerGroup(), req);
            Transaction transaction = result.broadcastComplete.get();
            log.info(transaction.toString());
            walletAppKit.store();
            return wallet.getBalance(Wallet.BalanceType.AVAILABLE).toFriendlyString();
        }).get();


    }
}
//private key
//1d454c6ab705f999d97e6465300a79a9595fb5ae1186ae20e33e12bea606c094