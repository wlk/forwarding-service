package com.varwise.btc.forwarding

import java.math.BigInteger

import org.bitcoinj.core.{Address, ECKey}
import org.bitcoinj.params.{RegTestParams, TestNet3Params}

class ForwardingPeerGroupFactorySpec extends UnitSpec {
  "ForwardingPeerGroupFactory" should "get PeerGroup" in {
    val fs = new ForwardingService(RegTestParams.get, List(ECKey.fromPrivate(BigInteger.ONE)), new Address(TestNet3Params.get, "msoGGRkYLWxvEwFYUJtpUiNjMALyM5xyLf"))

    val pg = ForwardingPeerGroupFactory.get(fs.wallet, RegTestParams.get)

    pg.numConnectedPeers() should be(0)
    pg.getPendingPeers.get(0).toString should be("[127.0.0.1]:18444")
  }

}
