//
//  BluetoothCommand.h
//  DevicePlugin
//
//  Created by Richard Eyre on 13-01-22.
//
//

#import "Returnable.h"
#import "Command.h"
#import "MedicalDevicePlugin.h"

@interface BluetoothCommand : Returnable <Command>

- (id)initWithPlugin:(MedicalDevicePlugin *)plugin named:(NSString *)callbackId;
- (MedicalDevicePlugin *)medicalDevicePlugin;
- (BluetoothServer *)server;
- (void)execute;

@end
