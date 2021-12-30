package com.example.btcexchange.service;

import com.example.btcexchange.NetworkContext;
import com.example.btcexchange.utils.IBitcoinNetParam;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.wallet.Wallet;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public record WalletService(IBitcoinNetParam iBitcoinNetParam, NetworkContext netWorkContext) {


    public String payToWallet() {
        ECKey clientKey = new ECKey();
        ECKey serverKey = new ECKey();

        Transaction contract = new Transaction(iBitcoinNetParam.btcNetParams());
        List<ECKey> keys = ImmutableList.of(clientKey, serverKey);
        Script script = ScriptBuilder.createMultiSigOutputScript(2, keys);
        Coin amount = Coin.valueOf(0, 50);
        TransactionOutput transactionOutput = contract.addOutput(amount, script);

        Wallet wallet = Wallet.createDeterministic(netWorkContext().getContext(), Script.ScriptType.P2WPKH);
        log.info(String.valueOf(wallet.getKeyChainSeed()));
//        log.debug(wallet.getKeyChainSeed().getMnemonicString());
        return transactionOutput.toString();
    }


}
