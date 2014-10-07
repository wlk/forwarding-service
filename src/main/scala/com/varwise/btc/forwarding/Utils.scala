package com.varwise.btc.forwarding

import org.bitcoinj.core.{Base58, DumpedPrivateKey, ECKey, NetworkParameters}

object Utils {
  def addressToKey(params: NetworkParameters, sourceAddress: String): ECKey = {
    sourceAddress match {
      case _ if sourceAddress.length == 51 || sourceAddress.length == 52 => new DumpedPrivateKey(params, sourceAddress).getKey
      case _ => ECKey.fromPrivate(Base58.decodeToBigInteger(sourceAddress))
    }
  }

}
