lifx-sdk-java aims to be a feature complete implementation of the LIFX protocol for Java. It supports turning lights on/off, colors, labels, groups and alarms (persisted in the bulb).

> If you run into a bug please don't hesitate to create an [issue](https://github.com/besherman/lifx-sdk-java/issues), so that we can try and make this library as stable as possible!

### Examples
Print all lights:
```java
LFXClient client = new LFXClient();        
client.open(true);
try {
    for(LFXLight light: client.getLights()) {
        System.out.format("Light: %n");
        System.out.format("\tid=%s%n", light.getID());
        System.out.format("\tlabel=%s%n", light.getLabel());
        System.out.format("\tpower=%s%n", light.isPower());
        System.out.format("\ttime=%s%n", light.getTime());
        System.out.format("\tcolor=%s%n", light.getColor());                
    }
} finally {
    client.close();
}
```

Setting an alarm on a light. The alarm is saved in the light and does not require the program to be running for the alarm to go off. In the current firmware only two alarms per light is allowed:
```java
LFXClient client = new LFXClient();
client.open(true);
try {            
    LFXLight light = client.getLights().getLightByLabel("my light");
    Date time = new Date(System.currentTimeMillis() + 30 * 1000);
    LFXAlarmCollection alarms = light.getAlarms();                        
    alarms.set(0, new LFXAlarm(time, new LFXHSBKColor(Color.BLUE), 7 * 1000));
    Thread.sleep(2 * 1000);
} finally {
    client.close();
}
```


### Documentation 
* [Javadoc](http://besherman.github.io/lifx-sdk-java/apidocs/master/index.html) for for the master branch 
* [Protocol documentation](https://docs.google.com/spreadsheets/d/1L4UBEpUuUmWIlIUyGKa9fPxNTEriz3l51T9QisAXi54/edit?usp=sharing) created from reading the source of [LIFX Android SDK](https://github.com/LIFX/lifx-sdk-android) and [LIFX SDK for Objective-C](https://github.com/LIFX/LIFXKit) 

### Maven
Version 2.0 not release yet. For the old lifx-sdk-android compatible release see the [release-1.0](https://github.com/besherman/lifx-sdk-java/tree/release-1.0) branch 

### History
Version 1.0 of lifx-sdk-java was a simple port of [lifx-sdk-android](https://github.com/LIFX/lifx-sdk-android) and tried to keep as close to the original as possible. But as time has passed lifx-sdk-android has not seen much activity or updates and an official Java implementation does not seem likely. So the upcoming version 2.0 of lifx-sdk-java has been largely rewritten for a simpler codebase and many new features has been implemented.