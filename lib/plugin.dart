import 'plugin_platform_interface.dart';

class Plugin {
  Future<String?> getPlatformVersion() {
    return PluginPlatform.instance.getPlatformVersion();
  }

  Future<void> initMobileHub() {
    return PluginPlatform.instance.initMobileHub();
  }

  Future<void> startMobileHub({
    required String ipAddress,
    required int port,
  }) {
    return PluginPlatform.instance.startMobileHub(
      ipAddress: ipAddress,
      port: port,
    );
  }

  Future<void> stopMobileHub() {
    return PluginPlatform.instance.stopMobileHub();
  }

  Future<bool?> isMobileHubStarted() {
    return PluginPlatform.instance.isMobileHubStarted();
  }

  Future<void> startListening({String? uuid}) {
    return PluginPlatform.instance.startListening(uuid: uuid);
  }

  Future<void> stopListening() {
    return PluginPlatform.instance.stopListening();
  }

  Future<bool?> isScanning() {
    return PluginPlatform.instance.isScanning();
  }

  Stream<bool> get onScanningStateChanged {
    return PluginPlatform.instance.onScanningStateChanged;
  }

  Stream<Map<dynamic, dynamic>> get onMessageReceived {
    return PluginPlatform.instance.onMessageReceived;
  }
}
