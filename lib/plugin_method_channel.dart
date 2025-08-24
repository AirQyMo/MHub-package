import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'plugin_platform_interface.dart';

/// An implementation of [PluginPlatform] that uses method channels.
class MethodChannelPlugin extends PluginPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('plugin');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<void> initMobileHub() async {
    await methodChannel.invokeMethod<void>('initMobileHub');
  }

  @override
  Future<void> startMobileHub() async {
    await methodChannel.invokeMethod<void>('startMobileHub');
  }

  @override
  Future<void> stopMobileHub() async {
    await methodChannel.invokeMethod<void>('stopMobileHub');
  }

  @override
  Future<bool?> isMobileHubStarted() async {
    final isStarted = await methodChannel.invokeMethod<bool>('isMobileHubStarted');
    return isStarted;
  }
}