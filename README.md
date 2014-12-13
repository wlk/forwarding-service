##Responsibility
This is a service that has one responsibility: sending bitcoins from multiple addresses to one address

Sending bitcoins (later called forwarding) will happen in 2 cases:  
1. After initial blockchain sync it will check if there is any balance left, if yes then it will be forwarded (we actually don't forward if there is less then 5 satoshis)  
2. After each block is mined we check if there are any incoming transactions, if yes we are forwarding coins to destination address  

Tool will pay default fee.
 
##Configuration
There are 2 configuration parameters:   
1. destination address  
2. file that points to a CSV file with addresses  

##CSV format
We look only at the first field in CSV file, other fields can serve as comment, or anything else.

##Building runnable jar
just run:
```
sbt assembly
```

This will create forwarding-service.jar in target/scala-2.11/ directory, you can run it like this:

```
java -jar target/scala-2.11/forwarding-service.jar main
```

##Running:
```
w@virtualbox ~/forwarding-service (master *) $ sbt "run-main com.varwise.btc.forwarding.Main regtest"
```

Running on main net:
```
java -jar target/scala-2.11/forwarding-service.jar main <address> <file> <threads>
```

###More examples
```
time java -Xmx1000m -jar target/scala-2.11/forwarding-service.jar main address /home/w/private-keys-1k 2
```

##Testing
Just run:  
```
w@virtualbox ~/forwarding-service (master) $ sbt test
[info] Loading project definition from /home/w/forwarding-service/project
[info] Set current project to forwarding-service (in build file:/home/w/forwarding-service/)
[info] Compiling 1 Scala source to /home/w/forwarding-service/target/scala-2.11/test-classes...
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
connecting on regtest net to localhost
connecting on org.bitcoinj.params.TestNet3Params@14ccf24b net via dns discovery
connecting on regtest net to localhost
[info] ForwardingPeerGroupFactorySpec:
[info] ForwardingPeerGroupFactory
[info] - should get PeerGroup
connecting on org.bitcoinj.params.TestNet3Params@14ccf24b net via dns discovery
[info] ForwardingServiceSpec:
[info] ForwardingService
[info] - should constructor should create wallet with one key
[info] ForwardingService
[info] - should setup listeners
[info] Run completed in 2 seconds, 370 milliseconds.
[info] Total number of tests run: 3
[info] Suites: completed 2, aborted 0
[info] Tests: succeeded 3, failed 0, canceled 0, ignored 0, pending 0
[info] All tests passed.
[success] Total time: 11 s, completed Oct 7, 2014 4:41:49 PM
```
