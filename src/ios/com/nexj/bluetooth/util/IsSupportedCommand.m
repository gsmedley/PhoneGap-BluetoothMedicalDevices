//
//  IsSupportedCommand.m
//  DevicePlugin
//
//  Created by Richard Eyre on 13-01-30.
//
//

#import "IsSupportedCommand.h"

@implementation IsSupportedCommand

- (void)execute
{
    [self returnResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:[self.server isSupported]]
                        named:self.callBackId];
}

@end
