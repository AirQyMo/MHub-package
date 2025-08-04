import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'mobile_hub_plugin_platform_interface.dart';

/// An implementation of [MobileHubPluginPlatform] that uses method channels.
class MethodChannelMobileHubPlugin extends MobileHubPluginPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('mobile_hub_plugin');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
