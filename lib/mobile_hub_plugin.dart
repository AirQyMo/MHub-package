// You have generated a new plugin project without specifying the `--platforms`
// flag. A plugin project with no platform support was generated. To add a
// platform, run `flutter create -t plugin --platforms <platforms> .` under the
// same directory. You can also find a detailed instruction on how to add
// platforms in the `pubspec.yaml` at
// https://flutter.dev/to/pubspec-plugin-platforms.

import 'dart:async';
import 'dart:convert'; // For JSON encoding/decoding

import 'package:flutter/services.dart';

/// A class to interact with the native Mobile Hub functionalities.
class MobileHubPlugin {
  // Define the Method Channel for invoking methods on the native side.
  static const MethodChannel _methodChannel =
      MethodChannel('mobile_hub/methods');

  // Define Event Channels for streaming data from the native side.
  static const EventChannel _messageEventChannel =
      EventChannel('mobile_hub/events/messages');
  static const EventChannel _bleDiscoveredEventChannel =
      EventChannel('mobile_hub/events/ble_discovered_devices');
  static const EventChannel _sensorDataEventChannel =
      EventChannel('mobile_hub/events/sensor_data');
  static const EventChannel _connectionStatusEventChannel =
      EventChannel('mobile_hub/events/connection_status');
  static const EventChannel _hubEventsEventChannel =
      EventChannel('mobile_hub/events/hub_events');

  /// Initializes the Mobile Hub.
  /// Returns true if initialization was successful, false otherwise.
  Future<bool> initHub() async {
    try {
      final bool? result = await _methodChannel.invokeMethod('initHub');
      return result ?? false;
    } on PlatformException catch (e) {
      print("Failed to initialize Mobile Hub: '${e.message}'.");
      return false;
    }
  }

  /// Starts the Mobile Hub service.
  /// Returns true if the service started successfully, false otherwise.
  Future<bool> startHub() async {
    try {
      final bool? result = await _methodChannel.invokeMethod('startHub');
      return result ?? false;
    } on PlatformException catch (e) {
      print("Failed to start Mobile Hub: '${e.message}'.");
      return false;
    }
  }

  /// Stops the Mobile Hub service.
  /// Returns true if the service stopped successfully, false otherwise.
  Future<bool> stopHub() async {
    try {
      final bool? result = await _methodChannel.invokeMethod('stopHub');
      return result ?? false;
    } on PlatformException catch (e) {
      print("Failed to stop Mobile Hub: '${e.message}'.");
      return false;
    }
  }

  /// Publishes a message to ContextNet.
  /// [topic]: The topic of the message.
  /// [payload]: The message content (will be JSON encoded).
  /// [qos]: Quality of Service level (e.g., 0, 1, 2).
  /// Returns true if the message was published successfully, false otherwise.
  Future<bool> publishMessage(String topic, Map<String, dynamic> payload, int qos) async {
    try {
      final bool? result = await _methodChannel.invokeMethod(
        'publishMessage',
        {
          'topic': topic,
          'payload': jsonEncode(payload), // Encode payload to JSON string
          'qos': qos,
        },
      );
      return result ?? false;
    } on PlatformException catch (e) {
      print("Failed to publish message: '${e.message}'.");
      return false;
    }
  }

  /// Publishes any messages currently queued in ContextNet.
  /// Returns true if queued messages were published, false otherwise.
  Future<bool> publishQueuedMessages() async {
    try {
      final bool? result = await _methodChannel.invokeMethod('publishQueuedMessages');
      return result ?? false;
    } on PlatformException catch (e) {
      print("Failed to publish queued messages: '${e.message}'.");
      return false;
    }
  }

  /// Starts scanning for BLE devices.
  /// Returns true if scan started successfully, false otherwise.
  Future<bool> startBleScan() async {
    try {
      final bool? result = await _methodChannel.invokeMethod('startScan');
      return result ?? false;
    } on PlatformException catch (e) {
      print("Failed to start BLE scan: '${e.message}'.");
      return false;
    }
  }

  /// Stops scanning for BLE devices.
  /// Returns true if scan stopped successfully, false otherwise.
  Future<bool> stopBleScan() async {
    try {
      final bool? result = await _methodChannel.invokeMethod('stopBleScan');
      return result ?? false;
    } on PlatformException catch (e) {
      print("Failed to stop BLE scan: '${e.message}'.");
      return false;
    }
  }

  /// Connects to a specific BLE device.
  /// [deviceId]: The unique ID (e.g., MAC address) of the device to connect to.
  /// Returns true if connection initiated successfully, false otherwise.
  Future<bool> connectBleDevice(String deviceId) async {
    try {
      final bool? result = await _methodChannel.invokeMethod(
        'connectBleDevice',
        {'deviceId': deviceId},
      );
      return result ?? false;
    } on PlatformException catch (e) {
      print("Failed to connect to BLE device '$deviceId': '${e.message}'.");
      return false;
    }
  }

  /// Disconnects from a specific BLE device.
  /// [deviceId]: The unique ID (e.g., MAC address) of the device to disconnect from.
  /// Returns true if disconnection initiated successfully, false otherwise.
  Future<bool> disconnectBleDevice(String deviceId) async {
    try {
      final bool? result = await _methodChannel.invokeMethod(
        'disconnectBleDevice',
        {'deviceId': deviceId},
      );
      return result ?? false;
    } on PlatformException catch (e) {
      print("Failed to disconnect from BLE device '$deviceId': '${e.message}'.");
      return false;
    }
  }

  /// Subscribes to sensor data from a connected BLE device.
  /// [deviceId]: The unique ID of the connected device.
  /// Returns true if subscription initiated successfully, false otherwise.
  Future<bool> subscribeToSensorData(String deviceId) async {
    try {
      final bool? result = await _methodChannel.invokeMethod(
        'subscribeToSensorData',
        {'deviceId': deviceId},
      );
      return result ?? false;
    } on PlatformException catch (e) {
      print("Failed to subscribe to sensor data for '$deviceId': '${e.message}'.");
      return false;
    }
  }

  /// Reads a single sensor data point from a connected BLE device.
  /// [deviceId]: The unique ID of the connected device.
  /// [serviceName]: The name of the service to read data from.
  /// Returns a Map representing the sensor data, or null on failure.
  Future<Map<String, dynamic>?> readSensorData(String deviceId, String serviceName) async {
    try {
      final Map<Object?, Object?>? result = await _methodChannel.invokeMethod(
        'readSensorData',
        {'deviceId': deviceId, 'serviceName': serviceName},
      );
      // Cast to Map<String, dynamic> for easier use in Dart
      return result?.cast<String, dynamic>();
    } on PlatformException catch (e) {
      print("Failed to read sensor data for '$deviceId' on service '$serviceName': '${e.message}'.");
      return null;
    }
  }

  /// Adds a new mobile object driver.
  /// [driverConfigJson]: A JSON string representing the driver configuration.
  /// Returns true if the driver was added successfully, false otherwise.
  Future<bool> addMobileObjectDriver(String driverConfigJson) async {
    try {
      final bool? result = await _methodChannel.invokeMethod(
        'addMobileObjectDriver',
        {'driverConfigJson': driverConfigJson},
      );
      return result ?? false;
    } on PlatformException catch (e) {
      print("Failed to add mobile object driver: '${e.message}'.");
      return false;
    }
  }

  /// Stream of incoming ContextNet messages.
  /// Each event is a Map<String, dynamic> representing the message.
  Stream<Map<String, dynamic>> get onMessageReceived {
    return _messageEventChannel.receiveBroadcastStream().map((event) {
      // Assuming event is a Map<String, dynamic> from Kotlin
      return (event as Map<Object?, Object?>).cast<String, dynamic>();
    });
  }

  /// Stream of discovered BLE devices during a scan.
  /// Each event is a Map<String, dynamic> representing a MobileObject.
  Stream<Map<String, dynamic>> get onBleDeviceDiscovered {
    return _bleDiscoveredEventChannel.receiveBroadcastStream().map((event) {
      return (event as Map<Object?, Object?>).cast<String, dynamic>();
    });
  }

  /// Stream of sensor data from subscribed BLE devices.
  /// Each event is a Map<String, dynamic> representing SensorData.
  Stream<Map<String, dynamic>> get onSensorDataReceived {
    return _sensorDataEventChannel.receiveBroadcastStream().map((event) {
      return (event as Map<Object?, Object?>).cast<String, dynamic>();
    });
  }

  /// Stream of ContextNet connection status changes.
  /// Each event is a String indicating the status (e.g., "CONNECTED", "DISCONNECTED").
  Stream<String> get onConnectionStatusChanged {
    return _connectionStatusEventChannel.receiveBroadcastStream().map((event) {
      return event as String;
    });
  }

  /// Stream of general Mobile Hub events.
  /// Each event is a String indicating the event type (e.g., "HUB_STARTED", "HUB_STOPPED").
  Stream<String> get onMobileHubEvent {
    return _hubEventsEventChannel.receiveBroadcastStream().map((event) {
      return event as String;
    });
  }
}

