package com.varwise.btc.forwarding

import java.net.InetAddress

import org.bitcoinj.core._
import org.bitcoinj.store.MemoryBlockStore

object ForwardingPeerGroupFactory {
  def get(wallet: Wallet, params: NetworkParameters): PeerGroup = {
    val blockStore: MemoryBlockStore = new MemoryBlockStore(params)
    val chain: BlockChain = new BlockChain(params, wallet, blockStore)
    val peerGroup: PeerGroup = new PeerGroup(params, chain)
    peerGroup.addWallet(wallet)
    peerGroup.setUseLocalhostPeerWhenPossible(false)
    peerGroup.addAddress(new PeerAddress(InetAddress.getByName("127.0.0.1"), 9333)) //btcd
    peerGroup.addAddress(new PeerAddress(InetAddress.getByName("127.0.0.1"), 8333)) //bitcoin-qt

    //if (params.equals(RegTestParams.get)) {
    Console.println("connecting on " + params + " net to localhost")
    //peerGroup.connectToLocalHost()
    //}
    //else {
    //  Console.println("connecting on " + params + " net via dns discovery")
    //  peerGroup.addPeerDiscovery(new DnsDiscovery(params))
    //}

    peerGroup
  }
}
