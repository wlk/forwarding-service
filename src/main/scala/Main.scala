package com.varwise.btc.forwarding

import java.io.File

import com.typesafe.config._
import org.bitcoinj.core._
import org.bitcoinj.params.{RegTestParams, TestNet3Params, MainNetParams}

import scala.io.Source

object Main extends App {
  override def main(args: Array[String]): Unit = {
    if(args.length != 1){
      Console.println("USAGE: sbt  sbt \"run-main com.varwise.btc.forwarding.Main regtest\"")
    }
    else{
      val configFile: String = args(0) match {
        case "main" => "application.conf"
        case "testnet" => "application-testnet.conf"
        case "regtest" => "application-testnet.conf"
      }

      val params: NetworkParameters = args(0) match {
        case "main" => MainNetParams.get
        case "testnet" => TestNet3Params.get
        case "regtest" => RegTestParams.get
      }

      val conf = ConfigFactory.load(configFile)
      val destination = new Address(params, conf.getString("destination-address"))

      val file = Source.fromFile(new File(conf.getString("private-key-directory.0")))

      val eckeys = file.getLines().toList map { //private keys in WIF format
        key => addressToKey(params, key.split(",")(0))
      }

      Console.println("address is: " + destination)

      val fs = new ForwardingService(params, eckeys, destination)

      Console.println("forwarding service:\n" + fs)
    }
  }

  def addressToKey(params: NetworkParameters, sourceAddress: String): ECKey = {
    sourceAddress match {
      case _ if sourceAddress.length == 51 || sourceAddress.length == 52 => new DumpedPrivateKey(params, sourceAddress).getKey
      case _ => ECKey.fromPrivate(Base58.decodeToBigInteger(sourceAddress))
    }
  }
}