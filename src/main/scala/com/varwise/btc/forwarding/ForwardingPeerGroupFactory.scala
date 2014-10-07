package com.varwise.btc.forwarding

import org.bitcoinj.core.{BlockChain, NetworkParameters, PeerGroup, Wallet}
import org.bitcoinj.net.discovery.DnsDiscovery
import org.bitcoinj.params.RegTestParams
import org.bitcoinj.store.MemoryBlockStore

object ForwardingPeerGroupFactory {
  def get(wallet: Wallet, params: NetworkParameters): PeerGroup = {
    val blockStore: MemoryBlockStore = new MemoryBlockStore(params)
    val chain: BlockChain = new BlockChain(params, wallet, blockStore)
    val peerGroup: PeerGroup = new PeerGroup(params, chain)
    peerGroup.addWallet(wallet)
    if (params.equals(RegTestParams.get)) {
      Console.println("connecting on regtest net to localhost")
      peerGroup.connectToLocalHost()
    }
    else {
      Console.println("connecting on " + params + " net via dns discovery")
      peerGroup.addPeerDiscovery(new DnsDiscovery(params))
    }

    peerGroup
  }
}
