package com.example.btcexchange.ldk;

import org.bitcoinj.core.Transaction;
import org.bitcoinj.kits.WalletAppKit;
import org.ldk.batteries.ChannelManagerConstructor;
import org.ldk.enums.ConfirmationTarget;
import org.ldk.enums.Network;
import org.ldk.structs.*;

import java.util.concurrent.ExecutionException;

public class LightningConfig {
     final static java.util.logging.Logger logger  = java.util.logging.Logger.getLogger(LightningConfig.class.getName());
     final Logger.LoggerInterface loggerImpl;
     final FeeEstimator.FeeEstimatorInterface feeEstimatorImpl;
     final BroadcasterInterface.BroadcasterInterfaceInterface broadcasterImpl;
     final NetworkGraph networkGraph;
     final Persist persister;

     LightningConfig(WalletAppKit walletAppKit){
          loggerImpl=(a)-> logger.info(a.toString());
          feeEstimatorImpl = (confTarget)-> estimateFee(confTarget);
          broadcasterImpl = (tx)-> broadcastTransaction(walletAppKit, tx);
          networkGraph=NetworkGraph.of(BestBlock.from_genesis(Constants.ldkNetowrk).block_hash());
          persister=Persist.new_impl(persistToStorage());
          ChannelManagerConstructor.EventHandler channelManagerConstructor= channelManagerConstructor();

     }

     private ChannelManagerConstructor.EventHandler channelManagerConstructor() {
          return new ChannelManagerConstructor.EventHandler() {
               @Override
               public void handle_event(Event events) {
                    switch (events){
                         case Event.FundingGenerationReady e->{ System.out.println(""); }
                         case Event.PaymentReceived e->{ System.out.println(""); }
                         case Event.PaymentSent e->{ System.out.println(""); }
                         case Event.PaymentFailed e->{ System.out.println(""); }
                         case Event.PendingHTLCsForwardable e->{ System.out.println(""); }
                         case Event.SpendableOutputs e->{ System.out.println(""); }
                         case Event.PaymentForwarded e->{ System.out.println(""); }
                         case Event.ChannelClosed e->{ System.out.println(""); }
                         default -> throw new IllegalStateException("Unexpected value: " + events);
                    }

               }

               @Override
               public void persist_manager(byte[] channel_manager_bytes) {

               }
          };
     }

     private Persist.PersistInterface persistToStorage() {
          return new Persist.PersistInterface() {
               @Override
               public Result_NoneChannelMonitorUpdateErrZ persist_new_channel(OutPoint channel_id, ChannelMonitor data, MonitorUpdateId update_id) {
                    return null;
               }

               @Override
               public Result_NoneChannelMonitorUpdateErrZ update_persisted_channel(OutPoint channel_id, ChannelMonitorUpdate update, ChannelMonitor data, MonitorUpdateId update_id) {
                    return null;
               }
          };
     }

     private int estimateFee(ConfirmationTarget confTarget) {
          return switch (confTarget) {
               case LDKConfirmationTarget_Background -> 1000;
               case LDKConfirmationTarget_Normal -> 100000;
               case LDKConfirmationTarget_HighPriority -> 10000000;
               default -> 10000;
          };
     }

     private void broadcastTransaction(WalletAppKit walletAppKit, byte[] tx) {
          Transaction transaction =new Transaction(Constants.networkParameters, tx);
          try {
               Transaction result= walletAppKit.peerGroup().broadcastTransaction(transaction).broadcast().get();
               System.out.println(result);
          } catch (InterruptedException e) {
               e.printStackTrace();
          } catch (ExecutionException e) {
               e.printStackTrace();
          }
     }

     ;

}
