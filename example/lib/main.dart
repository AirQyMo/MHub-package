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

    _messageSubscription = _plugin.onMessageReceived.listen((message) {
      setState(() {
        final text = "From ${message['sender']}: ${message['message']}";
        // Avoid adding duplicate messages
        if (!_receivedMessages.contains(text)) {
          _receivedMessages.add(text);
        }
      });
      print('Message received: $message');
    });
  }

  Future<void> _startListening() async {
    try {
      // Clear previous results on new scan
      setState(() {
        _receivedMessages.clear();
      });
      await _plugin.startListening();
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
      appBar: AppBar(title: const Text('BLE Message Receiver')),
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


// import 'package:flutter/material.dart';
// import 'package:plugin/plugin.dart'; // Ensure this path is correct

// void main() {
//   runApp(const MyApp());
// }

// class MyApp extends StatelessWidget {
//   const MyApp({super.key});

//   @override
//   Widget build(BuildContext context) {
//     return MaterialApp(
//       title: 'Mobile Hub Plugin Example',
//       theme: ThemeData(
//         primarySwatch: Colors.blue,
//       ),
//       home: const MobileHubHomePage(),
//     );
//   }
// }

// class MobileHubHomePage extends StatefulWidget {
//   const MobileHubHomePage({super.key});

//   @override
//   State<MobileHubHomePage> createState() => _MobileHubHomePageState();
// }

// class _MobileHubHomePageState extends State<MobileHubHomePage> {
//   final _mobileHubPlugin = Plugin();
//   final TextEditingController _idController = TextEditingController();

//   String _contextNetStatus = 'Disconnected';
//   final List<String> _discoveredBleDevices = [];
//   final List<String> _sensorDataMessages = [];

//   @override
//   void initState() {
//     super.initState();
//     _listenToPluginStreams();
//   }

//   void _listenToPluginStreams() {
//     _mobileHubPlugin.onConnectionStatusChanged.listen((status) {
//       setState(() {
//         _contextNetStatus = status;
//       });
//       print('ContextNet Connection Status: $status');
//     });

//     _mobileHubPlugin.onBleDeviceDiscovered.listen((device) {
//       setState(() {
//         _discoveredBleDevices.add('Name: ${device['name']}, ID: ${device['id']}');
//       });
//       print('Discovered BLE Device: ${device['name']} (${device['id']})');
//     });

//     _mobileHubPlugin.onMobileHubEvent.listen((event) {
//       print('Mobile Hub Event: $event');
//     });

//     _mobileHubPlugin.onMessageReceived.listen((message) {
//       print('ContextNet Message Received: $message');
//     });
//   }

//   Future<void> _connectToContextNet() async {
//     bool initialized = await _mobileHubPlugin.initHub();
//     if (initialized) {
//       bool started = await _mobileHubPlugin.startHub();
//       if (started) {
//         _showSnackBar('Mobile Hub started and attempting to connect to ContextNet.');
//       } else {
//         _showSnackBar('Failed to start Mobile Hub.');
//       }
//     } else {
//       _showSnackBar('Failed to initialize Mobile Hub.');
//     }
//   }

//   Future<void> _disconnectFromContextNet() async {
//     bool stopped = await _mobileHubPlugin.stopHub();
//     if (stopped) {
//       _showSnackBar('Mobile Hub stopped and disconnected from ContextNet.');
//     } else {
//       _showSnackBar('Failed to stop Mobile Hub.');
//     }
//   }

//   Future<void> _startBleScan() async {
//     setState(() {
//       _discoveredBleDevices.clear(); // Clear previous scan results
//     });
//     bool scanStarted = await _mobileHubPlugin.startBleScan();
//     if (scanStarted) {
//       _showSnackBar('BLE scan started.');
//     } else {
//       _showSnackBar('Failed to start BLE scan.');
//     }
//   }

//   Future<void> _stopBleScan() async {
//     bool scanStopped = await _mobileHubPlugin.stopBleScan();
//     if (scanStopped) {
//       _showSnackBar('BLE scan stopped.');
//     } else {
//       _showSnackBar('Failed to stop BLE scan.');
//     }
//   }

//   void _showSnackBar(String message) {
//     ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(message)));
//   }

//   @override
//   void dispose() {
//     _idController.dispose();
//     super.dispose();
//   }

//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       appBar: AppBar(
//         title: const Text('Mobile Hub Plugin Example'),
//       ),
//       body: Padding(
//         padding: const EdgeInsets.all(16.0),
//         child: SingleChildScrollView(
//           child: Column(
//             crossAxisAlignment: CrossAxisAlignment.start,
//             children: <Widget>[
//               // ContextNet Controls
//               Text(
//                 'ContextNet Status: $_contextNetStatus',
//                 style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
//               ),
//               Row(
//                 children: [
//                   ElevatedButton(
//                     onPressed: _connectToContextNet,
//                     child: const Text('Connect to ContextNet'),
//                   ),
//                   const SizedBox(width: 10),
//                   ElevatedButton(
//                     onPressed: _disconnectFromContextNet,
//                     child: const Text('Disconnect from ContextNet'),
//                   ),
//                 ],
//               ),
//               const Divider(height: 30),

//               // BLE Controls
//               const Text(
//                 'BLE Devices & Data:',
//                 style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
//               ),
//               Row(
//                 children: [
//                   ElevatedButton(
//                     onPressed: _startBleScan,
//                     child: const Text('Start BLE Scan'),
//                   ),
//                   const SizedBox(width: 10),
//                   ElevatedButton(
//                     onPressed: _stopBleScan,
//                     child: const Text('Stop BLE Scan'),
//                   ),
//                 ],
//               ),
//               const SizedBox(height: 10),
//               const Text('Discovered BLE Devices:', style: TextStyle(fontWeight: FontWeight.bold)),
//               SizedBox(
//                 height: 100,
//                 child: ListView.builder(
//                   itemCount: _discoveredBleDevices.length,
//                   itemBuilder: (context, index) {
//                     return Text(_discoveredBleDevices[index]);
//                   },
//                 ),
//               ),
//               const SizedBox(height: 10),
//               const Text('Received Sensor Data:', style: TextStyle(fontWeight: FontWeight.bold)),
//               SizedBox(
//                 height: 150,
//                 child: ListView.builder(
//                   itemCount: _sensorDataMessages.length,
//                   itemBuilder: (context, index) {
//                     return Text(_sensorDataMessages[index]);
//                   },
//                 ),
//               ),
//               const Divider(height: 30),

//               // ID Input
//               const Text(
//                 'Your ID (for illustrative purposes):',
//                 style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
//               ),
//               TextField(
//                 controller: _idController,
//                 decoration: const InputDecoration(
//                   hintText: 'Enter your ID',
//                   border: OutlineInputBorder(),
//                 ),
//                 onChanged: (text) {
//                   // In a real app, you might use this to update a setting via the plugin
//                   print('ID changed to: $text');
//                 },
//               ),
//               const SizedBox(height: 10),
//               ElevatedButton(
//                 onPressed: () {
//                   _showSnackBar('Entered ID: ${_idController.text}');
//                   // As noted, no direct plugin method for generic ID setting,
//                   // this is just for UI demonstration.
//                 },
//                 child: const Text('Save ID'),
//               ),
//             ],
//           ),
//         ),
//       ),
//     );
//   }
// }