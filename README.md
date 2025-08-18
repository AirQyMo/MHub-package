# MobileHub

The Mobile Hub is a library that enables mobile personal devices (Android smartphones and tablets) to become the propagator nodes (i.e. gateways to the Internet) for the simpler IoT objects or Mobile Objects (M-OBJ) (sensors/actuators) with only short-range WPAN interfaces. It provides context information such as current local time and/or the (approximate) location to the data obtained from the M-OBJs to which it is connected. 

Currently supported technologies: 

* WLAN: MRUDP and MQTT
* WPAN: BLE
* CEP: Asper

## Known Issues

Due to the use of assisted injection, sometimes there is a problem at building. Just clean and build (rebuild) the project and execute again.

## API Reference

### Initialize
```java
MobileHub.init(context).build();
```

### More Options
```java
MobileHub.init(this)
  .server("192.168.1.1", "1883") // ip / port 
  .setWlanTechnology(MqttWLAN)
  .addWpanTechnology(BleWPAN)
  .setCepTechnology(AsperCEP)
  .setAutoConnect(true)
  .setLog(true)
  .build();
```

### Use
Start the service
```java
MobileHub.start();
```
Stops the service
```java
MobileHub.stop();
```

## Libraries
* [RxAndroidBle](https://github.com/Polidea/RxAndroidBle)
* [Dagger](https://github.com/google/dagger)

## License
[Apache 2.0](https://choosealicense.com/licenses/apache-2.0/)

### Contributing to Mobile Hub
Just make pull request!
