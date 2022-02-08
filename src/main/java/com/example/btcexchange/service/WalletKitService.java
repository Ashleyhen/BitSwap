package com.example.btcexchange.service;

import com.example.btcexchange.ContextStates;
import com.example.btcexchange.DTO.TransferToDto;
import com.example.btcexchange.DTO.WalletDto;
import com.example.btcexchange.utils.IBitcoinNetParam;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.SegwitAddress;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.DeterministicSeed;
import org.jsoup.internal.StringUtil;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class WalletKitService extends WalletService {

    private final IBitcoinNetParam iBitcoinNetParam;
    private final ContextStates netWorkContextStates;

    public WalletKitService(IBitcoinNetParam iBitcoinNetParam, ContextStates netWorkContextStates) {
        super(iBitcoinNetParam, netWorkContextStates);
        this.iBitcoinNetParam = iBitcoinNetParam;
        this.netWorkContextStates = netWorkContextStates;
    }

    public Try<SegwitAddress> sendTransaction(TransferToDto transferToDto) {
        NetworkParameters parameters = iBitcoinNetParam.btcNetParams();
        WalletAppKit kit = new WalletAppKit(parameters, new File("storage/blockchain.binary"), ".");

        kit.startAsync();
        kit.awaitRunning();
        Coin value = Coin.MILLICOIN;
        CheckedFunction0<WalletDto> checkWallet = () -> extractCredentialsMap().get(transferToDto.fromWallet());
        CheckedFunction0<DeterministicSeed> seedCheckedFunction0 = checkWallet.andThen(t -> new DeterministicSeed(StringUtil.join(t.getPrivateKey(), " "), null, "", 1409478661L));
        return null;


    }


}
