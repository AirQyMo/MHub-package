import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'mobile_hub_plugin_method_channel.dart';

abstract class MobileHubPluginPlatform extends PlatformInterface {
  /// Constructs a MobileHubPluginPlatform.
  MobileHubPluginPlatform() : super(token: _token);

  static final Object _token = Object();

  static MobileHubPluginPlatform _instance = MethodChannelMobileHubPlugin();

  /// The default instance of [MobileHubPluginPlatform] to use.
  ///
  /// Defaults to [MethodChannelMobileHubPlugin].
  static MobileHubPluginPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [MobileHubPluginPlatform] when
  /// they register themselves.
  static set instance(MobileHubPluginPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
