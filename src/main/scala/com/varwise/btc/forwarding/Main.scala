package com.varwise.btc.forwarding

import java.io.File

import org.bitcoinj.core._
import org.bitcoinj.params.{MainNetParams, RegTestParams, TestNet3Params}

import scala.io.Source

object Main extends App {
  override def main(args: Array[String]): Unit = {
    if(args.length != 3){
      Console.println("USAGE: sbt \"run-main com.varwise.btc.forwarding.Main regtest|main|testnet\"")
    }
    else{

      val params: NetworkParameters = args(0) match {
        case "main" => MainNetParams.get
        case "testnet" => TestNet3Params.get
        case "regtest" => RegTestParams.get
      }

      val destination = new Address(params, args(1))

      val file = Source.fromFile(new File(args(2)))

      val ECkeys = file.getLines().toList map {
        key => key match {
          case _ if key.contains(",") => Utils.addressToKey(params, key.split(",")(0))
          case _ => Utils.addressToKey(params, key)
        }
      }

      Console.println("address is: " + destination)

      val fs = new ForwardingService(params, ECkeys, destination)

      fs.start
    }
  }
}