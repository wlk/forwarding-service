package com.varwise.btc.forwarding

import java.io.File
import java.util.concurrent.{Executors, TimeUnit}

import org.bitcoinj.core._
import org.bitcoinj.params.{MainNetParams, RegTestParams, TestNet3Params}

import scala.io.Source

object Main extends App {
  override def main(args: Array[String]): Unit = {
    if (args.length != 4) {
      Console.println("USAGE: sbt \"run-main com.varwise.btc.forwarding.Main regtest|main|testnet <dest address> <file> <threads>\"")
    }
    else {

      val params: NetworkParameters = args(0) match {
        case "main" => MainNetParams.get
        case "testnet" => TestNet3Params.get
        case "regtest" => RegTestParams.get
      }

      val destination = new Address(params, args(1))

      val file = Source.fromFile(new File(args(2)))

      val ECkeys = file.getLines().toList.distinct map {
        case key if key.contains(",") => Utils.addressToKey(params, key.split(",")(0))
        case key => Utils.addressToKey(params, key)
      }

      val threads = args(3).toInt

      val es = Executors.newCachedThreadPool()

      ECkeys.grouped(ECkeys.size / threads) foreach {
        sublist => {
          Thread.sleep(2000) //workaround for possible bug in bitcoinj
          es.execute(new Runnable {
            def run() {
              new ForwardingService(params, sublist, destination).start()
            }
          })
        }
      }

      Console.println("all threads started")
      es.shutdown()
      Console.println("shutting down Executor, awaiting termination")
      es.awaitTermination(Long.MaxValue, TimeUnit.NANOSECONDS)
      Console.println("Terminated")
    }
  }
}