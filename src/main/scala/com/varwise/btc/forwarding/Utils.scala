package com.varwise.btc.forwarding

import java.math.BigInteger

import org.bitcoinj.core.{Base58, DumpedPrivateKey, ECKey, NetworkParameters}

object Utils {
  def addressToKey(params: NetworkParameters, sourceAddress: String): ECKey = {
    sourceAddress match {
      case _ if sourceAddress.length == 51 || sourceAddress.length == 52 => new DumpedPrivateKey(params, sourceAddress).getKey //WIF
      case _ if sourceAddress.length == 64 => ECKey.fromPrivate(new BigInteger(sourceAddress, 16)) //hex encoded private key
      case _ => ECKey.fromPrivate(Base58.decodeToBigInteger(sourceAddress)) //base58
    }
  }

}
