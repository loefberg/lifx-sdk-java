This is a quick and dirty port of "LIFX Android SDK" to Java. It is almost the exact same code as the Android SDK, with the addition of an implementation of the android packages. So the API is the same, making it easy to change to the official version when LIFX releases the Java SDK. See more details at https://github.com/LIFX/lifx-sdk-android

### Differences to lifx-sdk-android
The implementation of the `Context` class has a no-argument constructor. So to create a `LFXNetworkContext` we simply call `new Context()`.

```Java
LFXNetworkContext localNetworkContext = LFXClient.getSharedInstance(new Context()).getLocalNetworkContext();
```

Other differences:
* System.out.print() and System.out.println() has been changed to log() calls.
* Asks the runtime for the broadcast address instead of calculating it.

### Output
The output is quite chatty, so to show only warnings and errors put this in logging.properties:

```
lifx.java.android.level = WARNING
```
And then tell the log manager to read it.
```
LogManager.getLogManager().readConfiguration(getClass().getResourceAsStream("/logging.properties"));
```