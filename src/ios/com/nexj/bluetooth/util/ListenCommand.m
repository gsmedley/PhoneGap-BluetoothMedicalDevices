//
//  ListenCommand.m
//  DevicePlugin
//
//  Created by Richard Eyre on 13-01-30.
//
//

#import "ListenCommand.h"

@implementation ListenCommand

- (void)execute
{
    CDVPluginResult *result = nil;
    NSString *listenResult = nil;
    CDVCommandStatus cdv;
    
    @try {
        listenResult = [self.server listen];
        cdv = CDVCommandStatus_OK;
    }
    @catch (NSException *exception) {
        listenResult = [exception reason];
        cdv = CDVCommandStatus_ERROR;
    }
    
    result = [CDVPluginResult resultWithStatus:cdv messageAsString:listenResult];
    [self returnResult:result named:self.callBackId];
}

@end
