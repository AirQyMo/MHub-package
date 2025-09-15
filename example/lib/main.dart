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
  const MobileHubScreen({super.key});

  @override
  State<MobileHubScreen> createState() => _MobileHubScreenState();
}

class _MobileHubScreenState extends State<MobileHubScreen> {
  final _plugin = Plugin();

  Future<void> _startMobileHub() async {
    try {
      await _plugin.startMobileHub();
      _showSnackBar('Mobile hub started.');
    } catch (e) {
      _showSnackBar('Failed to start mobile hub: $e');
    }
  }

  Future<void> _stopMobileHub() async {
    try {
      await _plugin.stopMobileHub();
      _showSnackBar('Mobile hub stopped.');
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
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            ElevatedButton(
              onPressed: _startMobileHub,
              child: const Text('Start Mobile Hub'),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: _stopMobileHub,
              child: const Text('Stop Mobile Hub'),
            ),
          ],
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
  final TextEditingController _uuidController = TextEditingController();
  StreamSubscription? _scanningStateSubscription;
  StreamSubscription? _messageSubscription;

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
    _uuidController.dispose();
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

    _messageSubscription = _plugin.onMessageReceived.listen((deviceData) {
      // Cast the received data to a Map
      final deviceMap = deviceData as Map<dynamic, dynamic>;

      // Extract device information
      final name = deviceMap['name'] ?? 'N/A';
      final macAddress = deviceMap['macAddress'];
      final rssi = deviceMap['rssi'];

      final text = 'Name: $name\nAddress: $macAddress, RSSI: $rssi';

      setState(() {
        // Check if the device is already in the list by its MAC address
        final existingIndex = _receivedMessages.indexWhere(
          (msg) => msg.contains(macAddress),
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

  Future<void> _startListening() async {
    try {
      // Clear previous results on new scan
      setState(() {
        _receivedMessages.clear();
      });
      final uuid = _uuidController.text.trim();
      await _plugin.startListening(uuid: uuid.isNotEmpty ? uuid : null);
      _showSnackBar('BLE scan started.');
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
                MaterialPageRoute(builder: (context) => const MobileHubScreen()),
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
                  onPressed: _isScanning || !_isResumed ? null : _startListening,
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
              controller: _uuidController,
              decoration: const InputDecoration(
                border: OutlineInputBorder(),
                labelText: 'Filter by UUID (optional)',
                hintText: 'e.g., 0000180d-0000-1000-8000-00805f9b34fb',
              ),
              onChanged: (text) {
                // You can add validation or immediate action here if needed
                print('UUID entered: $text');
              },
            ),
            const SizedBox(height: 20),
            const Divider(height: 30),
            Text(
              'Received Messages:',
              style: Theme.of(context).textTheme.headlineSmall,
            ),
            const SizedBox(height: 10),
            Expanded(
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
