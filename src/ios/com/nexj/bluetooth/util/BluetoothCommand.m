//
//  BluetoothCommand.m
//  DevicePlugin
//
//  Created by Richard Eyre on 13-01-22.
//
//

#import "BluetoothCommand.h"

@implementation BluetoothCommand

- (id)initWithPlugin:(MedicalDevicePlugin *)plugin named:(NSString *)callbackId;
{
    self = [super initWithPlugin:plugin named:callbackId];
    return self;
}

- (MedicalDevicePlugin *)medicalDevicePlugin
{
    return (MedicalDevicePlugin *)self.plugin;
}

- (BluetoothServer *)server
{
    return self.medicalDevicePlugin.server;
}

// Default implementation of execute@CommandProtocol, which will log an error and throw
// an exception if the method is not implemented in a subclass
- (void)execute
{
    // #1: Die if we've instantiated ReturnableCommand directly:
    NSAssert( [ self class ] != [ BluetoothCommand class ], @"Instantiation of abstract class `BluetoothCommand'" );
    // #2: If we're still alive, die because we failed to implement abstract method:
    NSAssert( false, @"Class `$@' does not implement abstract method `(void) execute'", NSStringFromClass( [ self class ] ) );
}

@end
