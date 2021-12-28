package com.example.btcexchange.service;

import com.example.btcexchange.utils.IBitcoinNetParam;
import com.google.common.collect.ImmutableList;
import lombok.extern.log4j.Log4j2;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.wallet.SendRequest;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Log4j2
public record WalletService(IBitcoinNetParam bitcoinNetParam) {


    public TransactionOutput payToWallet() {
        ECKey clientKey = new ECKey();
        ECKey serverKey = new ECKey();
        Transaction contract = new Transaction(bitcoinNetParam.btcNetParams());
        List<ECKey> keys = ImmutableList.of(clientKey, serverKey);
        Script script = ScriptBuilder.createMultiSigOutputScript(2, keys);
        log.debug(script.getScriptType());
        Coin amount = Coin.valueOf(0, 50);
        TransactionOutput transactionOutput = contract.addOutput(amount, script);
        SendRequest sendRequest = SendRequest.forTx(contract);
        sendRequest.toString();
        return transactionOutput;
    }


}
