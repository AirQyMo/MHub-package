import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:plugin/plugin.dart'; // Import your plugin

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

    @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  bool _isMobileHubStarted = false;
  final _plugin = Plugin(); // Instantiate your plugin

  
  @override
  void initState() {
    super.initState();
    _initMobileHub(); // Initialize MobileHub when the app starts
    _getPlatformVersion();
  }

  Future<void> _getPlatformVersion() async {
    String? platformVersion;
    try {
      platformVersion = await _plugin.getPlatformVersion();
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion ?? 'Unknown';
    });
  }

  Future<void> _initMobileHub() async {
    try {
      await _plugin.initMobileHub();
      print('MobileHub initialized successfully.');
    } catch (e) {
      print('Error initializing MobileHub: $e');
    }
  }

  Future<void> _startMobileHub() async {
    try {
      await _plugin.startMobileHub();
      final started = await _plugin.isMobileHubStarted();
      setState(() {
        _isMobileHubStarted = started ?? false;
      });
      print('MobileHub started: $_isMobileHubStarted');
    } catch (e) {
      print('Error starting MobileHub: $e');
    }
  }

  Future<void> _stopMobileHub() async {
    try {
      await _plugin.stopMobileHub();
      final started = await _plugin.isMobileHubStarted();
      setState(() {
        _isMobileHubStarted = started ?? false;
      });
      print('MobileHub stopped: $_isMobileHubStarted');
    } catch (e) {
      print('Error stopping MobileHub: $e');
    }
  }

  
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin Example App'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text('Running on: $_platformVersion\n'),
              ElevatedButton(
                onPressed: _startMobileHub,
                child: const Text('Start MobileHub'),
              ),
              ElevatedButton(
                onPressed: _stopMobileHub,
                child: const Text('Stop MobileHub'),
              ),
              Text('MobileHub is started: $_isMobileHubStarted'),
            ],
          ),
        ),
      ),
    );
  }
}