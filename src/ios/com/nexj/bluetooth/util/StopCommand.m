//
//  StopCommand.m
//  DevicePlugin
//
//  Created by Richard Eyre on 13-01-29.
//
//

#import "StopCommand.h"
#import "Invoker.h"

@implementation StopCommand

- (void)execute
{
    [Invoker stopAll];
}

@end
