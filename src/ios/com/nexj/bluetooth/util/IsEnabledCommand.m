//
//  IsEnabledCommand.m
//  DevicePlugin
//
//  Created by Caitlin Potter on 1/21/13.
//
//

#import "IsEnabledCommand.h"

@implementation IsEnabledCommand

- (void)execute
{
    CDVPluginResult *result = [ CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                messageAsInt:(int)[ self.server isEnabled ] ];
    [ super returnResult:result named:self.callBackId ];
}

@end
