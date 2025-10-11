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

  Stream<bool>? _onScanningStateChanged;
  Stream<Map<dynamic, dynamic>>? _onMessageReceived;

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
  Future<void> startListening({String? uuid}) async {
    await methodChannel.invokeMethod<void>(
      'startListening',
      {'uuid': uuid},
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
  Stream<Map<dynamic, dynamic>> get onMessageReceived {
    _onMessageReceived ??= _onMessageReceivedChannel
        .receiveBroadcastStream()
        .map((event) => event as Map<dynamic, dynamic>);
    return _onMessageReceived!;
  }
}
