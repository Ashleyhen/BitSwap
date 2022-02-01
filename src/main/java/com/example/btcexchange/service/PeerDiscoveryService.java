package com.example.btcexchange.service;

import com.example.btcexchange.ContextStates;
import com.example.btcexchange.utils.IBitcoinNetParam;
import io.vavr.CheckedFunction0;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.PeerAddress;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.VersionMessage;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.net.discovery.MultiplexingDiscovery;
import org.bitcoinj.params.AbstractBitcoinNetParams;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PeerDiscoveryService {
    private final static File storageFile = new File("storage/blockchain.binary");
    private final IBitcoinNetParam iBitcoinNetParam;
    private final ContextStates contextStates;

    @Autowired
    PeerDiscoveryService(IBitcoinNetParam iBitcoinNetParam, ContextStates contextStates) {
        this.iBitcoinNetParam = iBitcoinNetParam;
        this.contextStates = contextStates;
        storageFile.deleteOnExit();

    }

    private static CheckedFunction0<SPVBlockStore> storageSetUp(AbstractBitcoinNetParams bitcoinNetParams) {
        CheckedFunction0<SPVBlockStore> spvBlockStoreCheckedFunction0 = () -> new SPVBlockStore(bitcoinNetParams, storageFile);
        return spvBlockStoreCheckedFunction0.memoized();
    }

    public CheckedFunction0<PeerGroup> findPeer(List<Wallet> wallet) {

        return storageSetUp(iBitcoinNetParam.btcNetParams())
                .andThen(spvBlockStore -> {
                    BlockChain blockChain = new BlockChain(contextStates.getContext(), wallet, spvBlockStore);
                    return configPeerGroup(blockChain, wallet);

                });
    }

    private PeerGroup configPeerGroup(BlockChain blockChain, List<Wallet> walletsList) throws UnknownHostException {
        PeerGroup peerGroup = new PeerGroup(iBitcoinNetParam.btcNetParams(), blockChain);
        peerGroup.setUserAgent("bit swap", "1.0");
        walletsList.forEach(peerGroup::addWallet);
//        walletsList.forEach(blockChain::addWallet);
        MultiplexingDiscovery multiplexingDiscovery = DnsDiscovery.forServices(iBitcoinNetParam.btcNetParams(), VersionMessage.NODE_BLOOM);
        peerGroup.addPeerDiscovery(multiplexingDiscovery);
        peerGroup.addAddress(new PeerAddress(iBitcoinNetParam.btcNetParams(), InetAddress.getByName("127.0.0.1")));
        DownloadProgressTracker bListener = new DownloadProgressTracker() {
            @Override
            public void doneDownload() {
                log.info("\n\nblockchain download complete!!!\n");
            }
        };

        peerGroup.start();
        peerGroup.startBlockChainDownload(bListener);
//        bListener.await();
        log.info(walletsList.stream().map(wallet -> wallet.getBalance().toFriendlyString()).collect(Collectors.joining(", ")));


        return peerGroup;
    }


}
