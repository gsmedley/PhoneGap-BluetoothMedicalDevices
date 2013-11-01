//
//  Invoker.h
//  DevicePlugin
//
//  Created by Rick Eyre on 1/17/13.
//
//

#import <Foundation/Foundation.h>
#import "Command.h"

@interface Invoker : NSObject

+ (NSOperationQueue *)getOperationQue;
+ (void)execute:(id<Command>)task;
+ (void)startAll;
+ (void)stopAll;

@end
