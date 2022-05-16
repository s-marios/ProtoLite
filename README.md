# ProtoLite
One of the first ECHONET Lite implementations, now finally free.

### Getting Started
Build the library using maven: `mvn package` or open in your favourite java IDE.
The resulting .jar file is located in the `target/` subdirectory.

### Network Scanner
The basic ECHONET Lite network scanner application can be invoked by
running the redistributable jar archive. Using `java -jar protolite.jar`
will invoke the network scanner. If you want to listen to a specific network
interface, provide its IPv4/v6 address as an additional argument, like so:

```
java -jar protolite.jar 192.168.0.1
```
