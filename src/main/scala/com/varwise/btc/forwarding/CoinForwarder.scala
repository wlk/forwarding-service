package com.varwise.btc.forwarding

import com.google.common.base.Preconditions._
import com.google.common.util.concurrent.MoreExecutors
import org.bitcoinj.core._
import org.bitcoinj.crypto.KeyCrypterException

class CoinForwarder(params: NetworkParameters, wallet: Wallet, peerGroup: PeerGroup, destination: Address) {
  def forwardAllCoins(): Unit = {
    try {
      val value = wallet.getBalance
      Console.println("Sending " + value.toFriendlyString)
      val tx: Transaction = new Transaction(params)
      val request: Wallet.SendRequest = Wallet.SendRequest.forTx(tx)
      request.emptyWallet = true

      tx.addOutput(value, destination)

      wallet.completeTx(request)
      wallet.commitTx(request.tx)

      peerGroup.broadcastTransaction(request.tx).get
      Console.println("Sent all coins! Transaction hash is " + tx.getHashAsString)
      Console.println("Total coins: " + wallet.getBalance.toFriendlyString)

      Console.println("Sending ...")
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

  def forwardTransaction(tx: Transaction): Unit = {
    try {
      val value: Coin = tx.getValueSentToMe(wallet)
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
