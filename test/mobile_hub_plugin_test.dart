import 'package:flutter_test/flutter_test.dart';
// import 'package:mobile_hub_plugin/mobile_hub_plugin.dart';
import 'package:mobile_hub_plugin/mobile_hub_plugin_platform_interface.dart';
import 'package:mobile_hub_plugin/mobile_hub_plugin_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockMobileHubPluginPlatform
    with MockPlatformInterfaceMixin
    implements MobileHubPluginPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final MobileHubPluginPlatform initialPlatform = MobileHubPluginPlatform.instance;

  test('$MethodChannelMobileHubPlugin is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelMobileHubPlugin>());
  });
}
