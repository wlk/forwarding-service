package com.varwise.btc.forwarding

import java.io.File

import com.typesafe.config._
import org.bitcoinj.core._
import org.bitcoinj.params.{MainNetParams, RegTestParams, TestNet3Params}

import scala.io.Source

object Main extends App {
  override def main(args: Array[String]): Unit = {
    if(args.length != 1){
      Console.println("USAGE: sbt \"run-main com.varwise.btc.forwarding.Main regtest|main|testnet\"")
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

      val ECkeys = file.getLines().toList map {
        key => key match {
          case _ if key.contains(",") => Utils.addressToKey(params, key.split(",")(0))
          case _ => Utils.addressToKey(params, key)
        }
      }

      Console.println("address is: " + destination)

      val fs = new ForwardingService(params, ECkeys, destination)

      Console.println("forwarding service:\n" + fs)

      fs.start
    }
  }
}