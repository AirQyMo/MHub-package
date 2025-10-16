import 'dart:async';

import 'package:flutter/material.dart';
import 'package:plugin/plugin.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'BLE Message Receiver Example',
      theme: ThemeData(primarySwatch: Colors.blue, useMaterial3: true),
      home: const BleHomePage(),
    );
  }
}

class MobileHubScreen extends StatefulWidget {
  final Future<void> Function() onStartBleScanning;
  final Future<void> Function() onStopBleScanning;
  final Future<void> Function() onStartPeriodicContextUpdate;
  final Future<void> Function() onStopPeriodicContextUpdate;


  const MobileHubScreen({super.key, required this.onStartBleScanning, required this.onStopBleScanning, required this.onStartPeriodicContextUpdate, required this.onStopPeriodicContextUpdate});

  @override
  State<MobileHubScreen> createState() => _MobileHubScreenState();
}

class _MobileHubScreenState extends State<MobileHubScreen> {
  final _plugin = Plugin();
  final _ipAddressController = TextEditingController(text: '192.168.0.154');
  final _portController = TextEditingController(text: '6200');
  bool _isMobileHubRunning = false;

  @override
  void dispose() {
    _ipAddressController.dispose();
    _portController.dispose();
    super.dispose();
  }

  Future<void> _startMobileHub() async {
    try {
      final ipAddress = _ipAddressController.text;
      final port = int.tryParse(_portController.text);

      if (port == null) {
        _showSnackBar('Invalid port number.');
        return;
      }

      await _plugin.startMobileHub(ipAddress: ipAddress, port: port);
      _showSnackBar('Mobile hub started.');
      setState(() {
        _isMobileHubRunning = true;
      });

      // Start BLE scanning when Mobile Hub starts
      widget.onStartBleScanning();
      await widget.onStartPeriodicContextUpdate();
    } catch (e) {
      _showSnackBar('Failed to start mobile hub: $e');
    }
  }

  Future<void> _stopMobileHub() async {
    try {
      await _plugin.stopMobileHub();
      _showSnackBar('Mobile hub stopped.');

      setState(() {
        _isMobileHubRunning = false;
      });
      
      // Stop BLE scanning when Mobile Hub stops
      widget.onStopBleScanning();
      await widget.onStopPeriodicContextUpdate();
    } catch (e) {
      _showSnackBar('Failed to stop mobile hub: $e');
    }
  }

  void _showSnackBar(String message) {
    if (mounted) {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(SnackBar(content: Text(message)));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Mobile Hub')),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              TextField(
                controller: _ipAddressController,
                decoration: const InputDecoration(
                  border: OutlineInputBorder(),
                  labelText: 'IP Address',
                ),
              ),
              const SizedBox(height: 10),
              TextField(
                controller: _portController,
                decoration: const InputDecoration(
                  border: OutlineInputBorder(),
                  labelText: 'Port',
                ),
                keyboardType: TextInputType.number,
              ),
              const SizedBox(height: 20),
              ElevatedButton(
                onPressed: _startMobileHub,
                child: const Text('Start Mobile Hub'),
              ),
              const SizedBox(height: 10),
              ElevatedButton(
                onPressed: _stopMobileHub,
                child: const Text('Stop Mobile Hub'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class BleHomePage extends StatefulWidget {
  const BleHomePage({super.key});

  @override
  State<BleHomePage> createState() => _BleHomePageState();
}

class _BleHomePageState extends State<BleHomePage> with WidgetsBindingObserver {
  final _plugin = Plugin();
  bool _isScanning = false;
  bool _isResumed = true;
  final List<String> _receivedMessages = [];
  final Set<String> _uuidWhitelist = {};
  final TextEditingController _uuidInputController = TextEditingController();
  StreamSubscription? _scanningStateSubscription;
  StreamSubscription? _messageSubscription;
  Timer? _contextUpdateTimer;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    _listenToBleStreams();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    _scanningStateSubscription?.cancel();
    _messageSubscription?.cancel();
    _contextUpdateTimer?.cancel();
    _uuidInputController.dispose();
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    super.didChangeAppLifecycleState(state);
    setState(() {
      _isResumed = state == AppLifecycleState.resumed;
    });
    if (state == AppLifecycleState.paused) {
      if (_isScanning) {
        _stopListening();
      }
    }
  }

  void _listenToBleStreams() {
    _scanningStateSubscription = _plugin.onScanningStateChanged.listen((
      isScanning,
    ) {
      setState(() {
        _isScanning = isScanning;
      });
      print('Scanning state changed: $isScanning');
    });

    _messageSubscription = _plugin.onBleDataReceived.listen((deviceData) {
      // Cast the received data to a Map
      final deviceMap = deviceData as Map<dynamic, dynamic>;

      // Extract device information
      final name = deviceMap['name'] ?? 'N/A';
      final uuid = deviceMap['uuid'] as String?;
      final rssi = deviceMap['rssi'];

      if (uuid == null) return; // Don't process devices without a UUID

      final text = 'Name: $name\nUUID: $uuid, RSSI: $rssi';

      setState(() {
        // Check if the device is already in the list by its UUID
        final existingIndex = _receivedMessages.indexWhere(
          (msg) => msg.contains(uuid),
        );

        if (existingIndex != -1) {
          // If the device is already in the list, update its information
          _receivedMessages[existingIndex] = text;
        } else {
          // Otherwise, add it as a new device
          _receivedMessages.add(text);
        }
      });
      print('Device found: $text');
    });
  }

  Future<void> _startPeriodicContextUpdate() {
    _contextUpdateTimer = Timer.periodic(const Duration(seconds: 2), (_) {
      _sendContextUpdate();
    });

    return Future.value();
  }

  Future<void> _stopPeriodicContextUpdate() {
    _contextUpdateTimer?.cancel();
    _contextUpdateTimer = null;

    return Future.value();
  }

  Future<void> _sendContextUpdate() async {
    try {
      // Convert the list of device messages to a list of maps
      final devicesList = _receivedMessages.map((message) {
        // Parse the message to extract device info
        // Format: "Name: xxx\nUUID: yyy, RSSI: zzz"
        final lines = message.split('\n');
        final name = lines[0].replaceFirst('Name: ', '');
        final uuidAndRssi = lines[1].split(', RSSI: ');
        final uuid = uuidAndRssi[0].replaceFirst('UUID: ', '');
        final rssi = int.tryParse(uuidAndRssi[1]) ?? 0;

        return {
          'name': name,
          'uuid': uuid,
          'rssi': rssi,
        };
      }).toList();

      await _plugin.updateContext(devices: devicesList);
      print('Context updated with ${devicesList.length} devices');
    } catch (e) {
      print('Failed to update context: $e');
    }
  }

  void _addUuidToWhitelist() {
    final uuid = _uuidInputController.text.trim();
    if (uuid.isEmpty) {
      _showSnackBar('Please enter a UUID.');
      return;
    }

    setState(() {
      if (_uuidWhitelist.add(uuid)) {
        _uuidInputController.clear();
        _showSnackBar('UUID added to whitelist.');
      } else {
        _showSnackBar('UUID already in whitelist.');
      }
    });
  }

  void _removeUuidFromWhitelist(String uuid) {
    setState(() {
      _uuidWhitelist.remove(uuid);
    });
    _showSnackBar('UUID removed from whitelist.');
  }

  Future<void> _startListening() async {
    try {
      // Clear previous results on new scan
      setState(() {
        _receivedMessages.clear();
      });

      // Convert whitelist to a list for the plugin, pass null if whitelist is empty
      final uuidList = _uuidWhitelist.isNotEmpty ? _uuidWhitelist.toList() : null;
      await _plugin.startListening(uuids: uuidList);
      
      if (uuidList != null) {
        _showSnackBar('BLE scan started with ${uuidList.length} UUID(s)');
      } else {
        _showSnackBar('BLE scan started (scanning all devices)');
      }
    } catch (e) {
      _showSnackBar('Failed to start BLE scan: $e');
    }
  }

  Future<void> _stopListening() async {
    try {
      await _plugin.stopListening();
      _showSnackBar('BLE scan stopped.');

    } catch (e) {
      _showSnackBar('Failed to stop BLE scan: $e');
    }
  }

  void _showSnackBar(String message) {
    if (mounted) {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(SnackBar(content: Text(message)));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('BLE Message Receiver'),
        actions: [
          IconButton(
            icon: const Icon(Icons.hub),
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => MobileHubScreen(
                    onStartBleScanning: _startListening,
                    onStopBleScanning: _stopListening,
                    onStartPeriodicContextUpdate: _startPeriodicContextUpdate,
                    onStopPeriodicContextUpdate: _stopPeriodicContextUpdate,
                  ),
                ),
              );
            },
          ),
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: <Widget>[
            Text(
              'Scan Status: ${_isScanning ? "Running" : "Stopped"}',
              style: Theme.of(context).textTheme.headlineSmall,
            ),
            const SizedBox(height: 10),
            Row(
              children: [
                ElevatedButton.icon(
                  icon: const Icon(Icons.play_arrow),
                  onPressed:
                      _isScanning || !_isResumed ? null : _startListening,
                  label: const Text('Start Scan'),
                ),
                const SizedBox(width: 10),
                ElevatedButton.icon(
                  icon: const Icon(Icons.stop),
                  onPressed: _isScanning ? _stopListening : null,
                  label: const Text('Stop Scan'),
                ),
              ],
            ),
            const SizedBox(height: 20),
            TextField(
              controller: _uuidInputController,
              decoration: InputDecoration(
                border: const OutlineInputBorder(),
                labelText: 'Add UUID to Whitelist',
                hintText: 'e.g., 0000180d-0000-1000-8000-00805f9b34fb',
                suffixIcon: IconButton(
                  icon: const Icon(Icons.add),
                  onPressed: _addUuidToWhitelist,
                ),
              ),
              onSubmitted: (_) => _addUuidToWhitelist(),
            ),
            const SizedBox(height: 15),
            if (_uuidWhitelist.isNotEmpty) ...[
              Text(
                'UUID Whitelist (${_uuidWhitelist.length}):',
                style: Theme.of(context).textTheme.titleMedium,
              ),
              const SizedBox(height: 8),
              Expanded(
                flex: 1,
                child: ListView.builder(
                  itemCount: _uuidWhitelist.length,
                  itemBuilder: (context, index) {
                    final uuid = _uuidWhitelist.elementAt(index);
                    return Card(
                      margin: const EdgeInsets.symmetric(vertical: 4.0),
                      child: ListTile(
                        title: Text(
                          uuid,
                          overflow: TextOverflow.ellipsis,
                          style: const TextStyle(fontSize: 12),
                        ),
                        trailing: IconButton(
                          icon: const Icon(Icons.delete, color: Colors.red),
                          onPressed: () => _removeUuidFromWhitelist(uuid),
                        ),
                      ),
                    );
                  },
                ),
              ),
            ],
            const SizedBox(height: 20),
            const Divider(height: 30),
            Text(
              'Received Messages:',
              style: Theme.of(context).textTheme.headlineSmall,
            ),
            const SizedBox(height: 10),
            Expanded(
              flex: 2,
              child: ListView.builder(
                itemCount: _receivedMessages.length,
                itemBuilder: (context, index) {
                  return Card(
                    margin: const EdgeInsets.symmetric(vertical: 4.0),
                    child: Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Text(_receivedMessages[index]),
                    ),
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }
}