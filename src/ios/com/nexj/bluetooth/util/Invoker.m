//
//  Invoker.m
//  DevicePlugin
//
//  Created by Rick Eyre on 1/17/13.
//
//

#import "Invoker.h"

@implementation Invoker

static NSOperationQueue *operationQueue;

+ (NSOperationQueue *)getOperationQue
{
    if (!operationQueue)
    {
        [[operationQueue init] autorelease];
    }
    return operationQueue;
}

+ (void)execute:(id<Command>)task
{
    NSInvocationOperation *invocationOperation =
    [[[NSInvocationOperation alloc]initWithTarget:task selector:@selector(execute) object:nil] autorelease];
    
    [[Invoker getOperationQue] addOperation:invocationOperation];
}

+ (void)startAll
{
    [[Invoker getOperationQue] setSuspended:false];
}

+ (void)stopAll
{
    [[Invoker getOperationQue] setSuspended:true];
}

@end
