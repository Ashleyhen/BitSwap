package com.example.btcexchange.service;

import com.example.btcexchange.ContextStates;
import com.example.btcexchange.DTO.WalletDto;
import com.example.btcexchange.interfaces.IBitcoinNetParam;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.core.listeners.TransactionReceivedInBlockListener;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.net.discovery.MultiplexingDiscovery;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PeerDiscoveryService {
    private static final String fileName = "temp";
    private final IBitcoinNetParam iBitcoinNetParam;
    private final ContextStates contextStates;


    private static CheckedFunction0<SPVBlockStore> storageSetUp(IBitcoinNetParam bitcoinNetParams) {
        CheckedFunction0<SPVBlockStore> spvBlockStoreCheckedFunction0 = () -> new SPVBlockStore(bitcoinNetParams.btcNetParams(), bitcoinNetParams.getBlockChainFile());
        return spvBlockStoreCheckedFunction0.memoized();
    }

    public CheckedFunction0<PeerGroup> findPeer(List<Wallet> wallet) {

        return storageSetUp(iBitcoinNetParam)
                .andThen(spvBlockStore -> {
                    BlockChain blockChain = new BlockChain(contextStates.getContext(), wallet, spvBlockStore);
                    wallet.stream().forEach(w -> {
                        log.info(w.getDescription());
                        w.addCoinsReceivedEventListener((executor) -> {
                            executor.run();
                        }, (wallet1, transaction, previous, next) -> {
                            log.info(wallet1.getDescription());
                            log.info(transaction.getMemo());
                            log.info(previous.toFriendlyString());
                            log.info(next.toFriendlyString());
                        });

                    });
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

    public Try<WalletDto> addTrackAddress(String nameId, Wallet wallet) {

        Try<WalletDto> result = contextStates.propagateContext(context -> Try.of(() -> {
//             Blockstore is responsible for saving this data

            SPVBlockStore spvBlockStore = new SPVBlockStore(context, iBitcoinNetParam.getBlockChainFile());
//            blockchain is responsible for parsing and validating blocks
            BlockChain blockChain = new BlockChain(contextStates.getContext(), wallet, spvBlockStore);
//            PeerGroup object responsible for actually getting this information from the bitcoin network
            PeerGroup peerGroup = new PeerGroup(context, blockChain);
            MultiplexingDiscovery multiplexingDiscovery = DnsDiscovery.forServices(context, VersionMessage.NODE_BLOOM);
            peerGroup.addWallet(wallet);
            blockChain.addWallet(wallet);
            peerGroup.addPeerDiscovery(multiplexingDiscovery);
            wallet.addCoinsReceivedEventListener(
                    (wallet1, tx, prevBalance, newBalance) -> {
                        log.warn(wallet1.getDescription());
                        log.warn(tx.getMemo());
                        log.warn(prevBalance.toFriendlyString());
                        log.warn(newBalance.toFriendlyString());

                    }
            );

            peerGroup.startAsync();
            peerGroup.downloadBlockChain();
            wallet.saveToFile(iBitcoinNetParam.getStoredWallet(nameId));
            return new WalletDto().toWalletDto(wallet);
        }));
        log.info("test");
        return result;
    }

}
