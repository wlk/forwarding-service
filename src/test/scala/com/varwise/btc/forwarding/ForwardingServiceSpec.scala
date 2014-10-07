package com.varwise.btc.forwarding

import java.math.BigInteger

import org.bitcoinj.core.{Address, ECKey}
import org.bitcoinj.params.{RegTestParams, TestNet3Params}

class ForwardingServiceSpec extends UnitSpec{
  "ForwardingService" should "constructor should create wallet with one key" in {
    val key = ECKey.fromPrivate(BigInteger.ONE)
    val fs = new ForwardingService(TestNet3Params.get, List(key), new Address(RegTestParams.get, "msoGGRkYLWxvEwFYUJtpUiNjMALyM5xyLf"))

    val wallet = fs.wallet
    wallet.getImportedKeys.get(0) should be (key)
    wallet.getImportedKeys.size() should be (1)
  }

  "ForwardingService" should "setup listeners" in {
    val key = ECKey.fromPrivate(BigInteger.ONE)
    val fs = new ForwardingService(TestNet3Params.get, List(key), new Address(RegTestParams.get, "msoGGRkYLWxvEwFYUJtpUiNjMALyM5xyLf"))

    val wallet = fs.wallet
    //not available in bitcoinj 0.12
  }

}
