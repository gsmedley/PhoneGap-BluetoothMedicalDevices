//
//  BondedDevicesCommand.m
//  DevicePlugin
//
//  Created by Caitlin Potter on 1/21/13.
//
//

#import "BondedDevicesCommand.h"
#import <ExternalAccessory/ExternalAccessory.h>
#import <CoreBluetooth/CoreBluetooth.h>

@implementation BondedDevicesCommand

- (void)execute
{
    CDVPluginResult *result = nil;
    NSMutableArray *bondedDevices = [ NSMutableArray array ];
    NSArray *bondSet = [ [ self server ] bondedDevices ];

    @synchronized( bondSet )
    {
        @try
        {
            for( id it in bondSet )
            {
                // If we are talking to this device via the
                // ExternalAccessory framework...
                if( [ it isKindOfClass:[ EASession class ]])
                {
                    EASession *device = (EASession *)it;
                    NSString *name = device.accessory.name;
                    NSNumber *isBonded = [ NSNumber
                        numberWithBool:device.accessory.isConnected ];
                    // I'm not sure of a way to get the 'address' of
                    // the device with the ExternalAccessory API.
                    [ bondedDevices addObject:@{@"name":name,
                        @"address":@"unknown", @"isBonded":isBonded} ];
                }

                // Otherwise, if we're talking to this device via the
                // CoreBluetooth framework...
                else if( [ it isKindOfClass:[ CBPeripheral class ] ] )
                {
                    CBPeripheral *device = (CBPeripheral *)it;
                    NSString *name = device.name;
                    NSNumber *isBonded = [ NSNumber
                        numberWithBool:device.isConnected ];
                    // I'm not sure of a way to get the 'address' of
                    // the device with the ExternalAccessory API.
                    [ bondedDevices addObject:@{@"name":name,
                        @"address":@"unknown", @"isBonded":isBonded} ];
                }
            }
        }
        @catch (NSException *exception)
        {
            result = [ CDVPluginResult
                resultWithStatus:CDVCommandStatus_ERROR
                messageAsString: [ exception reason ] ];
            result.keepCallback = [ NSNumber numberWithBool:true ];
            [ super returnResult:result named:self.callBackId ];
            return;
        }

        // Return array of bonded devices in PluginResult.
        result = [ CDVPluginResult
            resultWithStatus:CDVCommandStatus_OK messageAsArray:bondedDevices ];
            result.keepCallback = [ NSNumber numberWithBool:true ];
        [ super returnResult:result named:self.callBackId ];
    }
}
@end
