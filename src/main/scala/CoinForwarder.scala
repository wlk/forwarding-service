package com.varwise.btc.forwarding

import com.google.common.base.Preconditions._
import com.google.common.util.concurrent.MoreExecutors
import org.bitcoinj.core._
import org.bitcoinj.crypto.KeyCrypterException

class CoinForwarder(params: NetworkParameters, wallet: Wallet, peerGroup: PeerGroup, destination: Address) {
  def forwardAllCoins(): Unit = {
    try {
      val value = wallet.getBalance
      Console.println("Forwarding " + value.toFriendlyString)
      val amountToSend: Coin = value.subtract(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE)
      val sendResult: Wallet.SendResult = wallet.sendCoins(peerGroup, destination, amountToSend)
      checkNotNull(sendResult)
      Console.println("Sending ...")
      sendResult.broadcastComplete.addListener(new Runnable {
        def run {
          Console.println("Sent coins onwards! Transaction hash is " + sendResult.tx.getHashAsString)
          Console.println("Total coins: " + wallet.getBalance.toFriendlyString)
        }
      }, MoreExecutors.sameThreadExecutor)
    }
    catch {
      case e: KeyCrypterException => {
        throw new RuntimeException(e)
      }
      case e: InsufficientMoneyException => {
        throw new RuntimeException(e)
      }
    }
  }
}
