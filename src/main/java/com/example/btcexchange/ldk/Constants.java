package com.example.btcexchange.ldk;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.ldk.enums.Network;

public class Constants {
    public final static NetworkParameters networkParameters =TestNet3Params.get();
    public final static Network ldkNetowrk=Network.LDKNetwork_Testnet;
}
