package com.varwise.btc.forwarding

import com.google.common.util.concurrent.{FutureCallback, Futures}
import org.bitcoinj.core.{Address, NetworkParameters, _}

import scala.collection.JavaConverters._
import scala.concurrent.duration._

class ForwardingService(params: NetworkParameters, ECkeys: List[ECKey], destination: Address) {
  val startTime = System.currentTimeMillis()
  val wallet = new Wallet(params)
  addKeys(ECkeys)
  wallet.allowSpendingUnconfirmedTransactions()


  val peerGroup = ForwardingPeerGroupFactory.get(wallet, params)
  val coinForwarder = new CoinForwarder(params, wallet, peerGroup, destination)

  def addKeys(keys: List[ECKey]): Unit = {
    val imported = wallet.importKeys(keys.asJava)
    Console.println("imported: " + imported + " keys")
  }

  def start() = {
    peerGroup.startAsync
    Console.println("Starting blockchain download")
    peerGroup.downloadBlockChain()
    Console.println("Blockchain download done")

    Console.println("Total coins: " + wallet.getBalance.toFriendlyString)
    val initialSyncTime = System.currentTimeMillis() - startTime
    Console.println("Initial sync took: " + Duration(initialSyncTime, MILLISECONDS).toMinutes + " min")

    setupListeners()
  }

  def setupListeners(): Unit = {
    wallet.addEventListener(new AbstractWalletEventListener {
      override def onCoinsReceived(w: Wallet, tx: Transaction, prevBalance: Coin, newBalance: Coin) {
        Console.println("Total coins: " + wallet.getBalance.toFriendlyString)
        val value: Coin = tx.getValueSentToMe(w)
        Console.println("Received tx for " + value.toFriendlyString + ": " + tx)
        Console.println("Transaction will be forwarded after it confirms.")
        Futures.addCallback(tx.getConfidence.getDepthFuture(1), new FutureCallback[Transaction] {
          def onSuccess(result: Transaction) {
            coinForwarder.forwardTransaction(result)
          }

          def onFailure(t: Throwable) {
            throw new RuntimeException(t)
          }
        })
      }
    })
    Console.println("Send coins to: " + wallet.currentReceiveKey.toAddress(params))
    Console.println("Waiting for coins to arrive. Press Ctrl-C to quit.")
    Console.println("Total coins: " + wallet.getBalance.toFriendlyString)
  }

  override def toString: String = {
    "ForwardingService{ " + params + ", " + destination + ", " + ECkeys + "}"
  }
}
