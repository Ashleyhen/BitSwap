package com.example.btcexchange.discovery;

import com.example.btcexchange.utils.IBitcoinNetParam;
import org.bitcoinj.core.VersionMessage;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.net.discovery.PeerDiscovery;
import org.bitcoinj.net.discovery.PeerDiscoveryException;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public record ConnectToDNS(IBitcoinNetParam iBitcoinNetParam) {
    public String connectToClient() {


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
