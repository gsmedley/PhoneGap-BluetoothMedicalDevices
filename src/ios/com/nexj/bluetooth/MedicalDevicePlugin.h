//
//  MedicalDevicePlugins.h
//  DevicePlugin
//
//  Created by Rick Eyre on 1/10/13.

#import <Foundation/Foundation.h>
#import "CDVPlugin.h"
#import "CDVPluginResult.h"
#import "BluetoothServer.h"
#import "PacketParser.h"
#import "Command.h"

@interface MedicalDevicePlugin : CDVPlugin

@property (readonly) BluetoothServer *server;

- (int)generateRequestCode;
- (void)registerCommandForResult:(int)requestCode commandForResult:(id<Command>)command;
- (void)bluetoothCommand:(CDVInvokedUrlCommand*)command;
- (void)onAppTerminate;

@end
