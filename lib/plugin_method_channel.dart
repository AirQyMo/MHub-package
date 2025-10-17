import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'plugin_platform_interface.dart';

/// An implementation of [PluginPlatform] that uses method channels.
class MethodChannelPlugin extends PluginPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('plugin');

  static const EventChannel _onScanningStateChangedChannel = EventChannel(
    'onScanningStateChanged',
  );
  static const EventChannel _onMessageReceivedChannel = EventChannel(
    'onMessageReceived',
  );
  static const EventChannel _onBleDataReceivedChannel = EventChannel(
    'onBleDataReceived',
  );

  Stream<bool>? _onScanningStateChanged;
  Stream<String>? _onMessageReceived;
  Stream<Map<dynamic, dynamic>>? _onBleDataReceived;

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>(
      'getPlatformVersion',
    );
    return version;
  }

  @override
  Future<void> initMobileHub() async {
    await methodChannel.invokeMethod<void>('initMobileHub');
  }

  @override
  Future<void> startMobileHub({
    required String ipAddress,
    required int port,
  }) async {
    await methodChannel.invokeMethod<void>('startMobileHub', {
      'ipAddress': ipAddress,
      'port': port,
    });
  }

  @override
  Future<void> updateContext({required List<String> devices}) async {
    await methodChannel.invokeMethod<void>('updateContext', {
      'devices': devices,
    });
  }

  @override
  Future<void> stopMobileHub() async {
    await methodChannel.invokeMethod<void>('stopMobileHub');
  }

  @override
  Future<bool?> isMobileHubStarted() async {
    final isStarted = await methodChannel.invokeMethod<bool>(
      'isMobileHubStarted',
    );
    return isStarted;
  }

  @override
  Future<void> startListening({List<String>? uuids}) async {
    await methodChannel.invokeMethod<void>(
      'startListening',
      {'uuids': uuids},
    );
  }

  @override
  Future<void> stopListening() async {
    await methodChannel.invokeMethod<void>('stopListening');
  }

  @override
  Future<bool?> isScanning() async {
    final isScanning = await methodChannel.invokeMethod<bool>('isScanning');
    return isScanning;
  }

  @override
  Stream<bool> get onScanningStateChanged {
    _onScanningStateChanged ??= _onScanningStateChangedChannel
        .receiveBroadcastStream()
        .map((event) => event as bool);
    return _onScanningStateChanged!;
  }

  @override
  Stream<String> get onMessageReceived {
    _onMessageReceived ??= _onMessageReceivedChannel
        .receiveBroadcastStream()
        .map((event) => event as String);
    return _onMessageReceived!;
  }

  @override
  Stream<Map<dynamic, dynamic>> get onBleDataReceived {
    _onBleDataReceived ??= _onBleDataReceivedChannel
        .receiveBroadcastStream()
        .map((event) => event as Map<dynamic, dynamic>);
    return _onBleDataReceived!;
  }
}
