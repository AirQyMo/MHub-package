import 'plugin_platform_interface.dart';

class Plugin {
  Future<String?> getPlatformVersion() {
    return PluginPlatform.instance.getPlatformVersion();
  }

  Future<void> initMobileHub() {
    return PluginPlatform.instance.initMobileHub();
  }

  Future<void> startMobileHub() {
    return PluginPlatform.instance.startMobileHub();
  }

  Future<void> stopMobileHub() {
    return PluginPlatform.instance.stopMobileHub();
  }

  Future<bool?> isMobileHubStarted() {
    return PluginPlatform.instance.isMobileHubStarted();
  }
}