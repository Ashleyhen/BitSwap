package com.example.btcexchange.service;

import com.example.btcexchange.ContextStates;
import com.example.btcexchange.utils.IBitcoinNetParam;
import io.vavr.CheckedFunction0;
import lombok.AllArgsConstructor;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.VersionMessage;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.net.discovery.PeerDiscovery;
import org.bitcoinj.net.discovery.PeerDiscoveryException;
import org.bitcoinj.params.AbstractBitcoinNetParams;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class PeerDiscoveryService {
    private final static File storageFile = new File("storage/blockchain.binary");
    private final IBitcoinNetParam iBitcoinNetParam;
    private final ContextStates contextStates;

    public static CheckedFunction0<SPVBlockStore> storageSetUp(AbstractBitcoinNetParams bitcoinNetParams) {
        return () -> new SPVBlockStore(bitcoinNetParams, storageFile);
    }

    public CheckedFunction0<BlockChain> findPeer(List<Wallet> wallet) {
        return storageSetUp(iBitcoinNetParam.btcNetParams())
                .andThen(spvBlockStore -> new BlockChain(contextStates.getContext(), wallet, spvBlockStore));
    }

    public String connectToClient(Wallet wallet, IBitcoinNetParam iBitcoinNetParam) {
        PeerDiscovery multiplexingDiscovery = DnsDiscovery.forServices(iBitcoinNetParam.btcNetParams(), VersionMessage.NODE_BLOOM);
        try {

            List<InetSocketAddress> inetSocketAddresses = multiplexingDiscovery.getPeers(VersionMessage.NODE_BLOOM, 1, TimeUnit.MINUTES);
            return inetSocketAddresses.stream().map(InetSocketAddress::toString).collect(Collectors.joining(","));
        } catch (PeerDiscoveryException e) {
            e.printStackTrace();
        }


        return "";
    }

}
