PhoneGap-BluetoothMedicalDevices
================================

Fork of https://bitbucket.org/nexj/mobile-bluetooth-adapters

# To Do

- Test Android devices
- Bring ios up to 3.0 plugin interface

# Mobile Bluetooth Adapters

This is a javascript interface built to give HTML5 mobile applciations the ability to interact with medical deivce peripherals.

This javascript interface is shared between the supported mobile platforms: Android and iOS.

Each of the supported mobile platforms has an identical implementation of the bluetooth adapter barring platform specific colloquialisms. These implementations handle the various use cases a user may expect when interacting with a medical device peripheral.

# Installing the plugin

Install the core plugin files via the [Command-line Interface](http://docs.phonegap.com/en/3.0.0/guide_cli_index.md.html#The%20Command-line%20Interface):

    $ phonegap plugin add https://github.com/gsmedley/PhoneGap-BluetoothMedicalDevices

# Using the plugin

### Available Methods

	:::javascript
		medicalDevicePlugin.
			on('event', callback(e payload));		// Attaches the provided callback to the specified event
			remove('event', callback(e, payload));	// Removes the provided callback from the specified event
			initialize(callback(e, payload));	// Initialize the device plugin
			isEnabled(callback(e, payload));	// Passes whether or not the Bluetooth interface is enabled into the optional callback	
			isSupported(callback(e, payload));	// Passes whether or not the Bluetooth interface is supported into the optional callback
			getbonded(callback(e, payload));	// Passes the current paired Bluetooth devices into the optional callback
			getEvents(callback(payload));		// Passes the supported list of events into the optional callback, this cannot produce an error
			enable(callback(e, payload));		// Enables the Bluetooth interface, optional callback
			disable(callback(e, payload));		// Disables the Bluetooth interface, optional callback
			discoverable({						// Starts the Bluetooth discovery process
				time: 120 						// Time window to keep a device discoverable 0-300 seconds, defaults to 120
			}, callback(e, payload));			// Optional callback
			listen({							// Begins the process of listening for Bluetooth client connections
				time: 120,						// Time window to keep listening runnnig, defaults to 120, 0 is unlimited
				brand: 'AND',					// Brand or manufacturer of device
				type: 'weight/blood pressure',	// Device type to focus listening on, ignoring other devices, defaults to any
				serial: '5090651014',			// Device serial to focus listening on, ignoring other devices, defaults to any
				num: 0							// The total number of measurements to get from medical devices, defaults to 0, 0 is unlimited until time
			}, callback(e, payload));			// Optional callback
			stopListen(callback(e, payload));	// Stops the Bluetooth listening and discovery processes, optional callback

### Initalization

	:::javascript
		medicalDevicePlugin.initialize();
		// Any calls to methods which utilizes part of the mobile devices native code
		// implementation will be deferred until after medicalDevicePlugin.initialize() is called

### Attaching event handlers and callback structure

	:::javascript
		medicalDevicePlugin.on('initialize', function(e, payload){
			// I am the event handler method and I am the same structure as all method callbacks
			// e is always the first parameter and will be defined if there is an error
			// the parameter following e will be a payload of information if it is relevant
		});

### Possible events

	:::javascript
		.on(/*
			'initialized'
			'enabled'
			'disabled'
			'connection'
			'discovery'
			'listening'
			'cordovaReady'
		*/, callback(e, payload));

### Callback payload structure

	:::javascript
		// initialize() and on 'initialized' event
		payload = {
			status: true/false	// True if plugin initialization was successful
		}
		// isEnabled(), enable() and on 'enabled' event
		payload = {
			status: true/false	// True if Bluetooth is now on
		}
		// isSupported()
		payload = {
			status: true/false	// True if Bluetooth is supported
		}
		// getBonded()
		payload = {
			devices: [{
				name: 'name',		// Visible name of the paired Bluetooth device
				serial: 'serial',	// Serial of the paired Bluetooth device
				address: 'address'	// MAC address of the paired Bluetooth device
			}]
		}
		// getEvents()
		payload = {
			events: [			// Array of emitted events by the medicalDevicePlugin
				'initialized'
				'enabled'
				'disabled'
				'connection'
				'discovery'
				'listening'
				'cordovaReady'
			]
		}
		// disable() and on 'disabled' event
		payload = {
			status: true/false	// True if Bluetooth is now off
		}
		// discoverable() and on 'discovery' event
		payload = {
			status: true/false,	// True if discovery has started, false when dicovery has finished
			time: 300			// Discovery uptime in seconds
		}
		// listen(), stopListen() and on 'listening' event
		payload = {
			status: true/false,	// True when listening has started, false when listening has finished
		}
		// listen(), and on 'connection' event
		payload = {
			// weight
			type: 'weight'
			time: 1360624026, 	// Unix time of measurement in seconds
			system: 'lb/kg',	// Measurement system
			weight: 100			// Weight as an integer
			serial: 5090250438  // Device serial number

			// blood pressure
			type: 'blood pressure'
			time: 1360624026 	// Unix time of measurement in seconds
			systolic: 100,		// Systolic pressure as an integer
			diastolic: 100,		// Diastolic pressure as an integer
			pulse: 100,			// Pulse as an integer
			mean: 100			// Arterial mean as an integer
			serial: 5090651014  // Device serial number
		}

# Uninstalling the plugin

To uninstall the plugin and its components, use:

    $ phonegap plugin remove medicalDevicePlugin


# Design Documentation

Docs Wiki - https://bitbucket.org/nexj/mobile-bluetooth-adapters/wiki/Home



