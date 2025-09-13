import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'plugin_method_channel.dart';

abstract class PluginPlatform extends PlatformInterface {
  /// Constructs a PluginPlatform.
  PluginPlatform() : super(token: _token);

  static final Object _token = Object();

  static PluginPlatform _instance = MethodChannelPlugin();

  /// The default instance of [PluginPlatform] to use.
  ///
  /// Defaults to [MethodChannelPlugin].
  static PluginPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [PluginPlatform] when
  /// they register themselves.
  static set instance(PluginPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('getPlatformVersion() has not been implemented.');
  }

  Future<void> initMobileHub() {
    throw UnimplementedError('initMobileHub() has not been implemented.');
  }

  Future<void> startMobileHub() {
    throw UnimplementedError('startMobileHub() has not been implemented.');
  }

  Future<void> stopMobileHub() {
    throw UnimplementedError('stopMobileHub() has not been implemented.');
  }

  Future<bool?> isMobileHubStarted() {
    throw UnimplementedError('isMobileHubStarted() has not been implemented.');
  }

  Future<void> startListening({String? uuid}) {
    throw UnimplementedError('startListening() has not been implemented.');
  }

  Future<void> stopListening() {
    throw UnimplementedError('stopListening() has not been implemented.');
  }

  Future<bool?> isScanning() {
    throw UnimplementedError('isScanning() has not been implemented.');
  }

  Stream<bool> get onScanningStateChanged {
    throw UnimplementedError(
      'onScanningStateChanged has not been implemented.',
    );
  }

  Stream<Map<dynamic, dynamic>> get onMessageReceived {
    throw UnimplementedError('onMessageReceived has not been implemented.');
  }
}
