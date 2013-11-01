//
//  MedicalDevicePlugins.m
//  DevicePlugin
//
//  Created by Rick Eyre on 1/10/13.

#import "MedicalDevicePlugin.h"
#import "Invoker.h"
#import "BondedDevicesCommand.h"
#import "IsEnabledCommand.h"
#import "StopCommand.h"
#import "ListenCommand.h"
#import "IsSupportedCommand.h"

@implementation MedicalDevicePlugin

static const NSString *s_sTag;
static int m_nRequestCode = 0;

static const NSString *ACTION_INIT = @"initialize";
static const NSString *ACTION_REQUEST_DISCOVERABLE = @"request";
static const NSString *ACTION_LISTEN = @"listen";
static const NSString *ACTION_GETBONDEDDEVICES = @"bonded";
static const NSString *ACTION_IS_ENABLED = @"isEnabled";
static const NSString *ACTION_IS_DISABLED = @"isDisabled";
static const NSString *ACTION_IS_SUPPORTED = @"isSupported";

NSMutableDictionary *m_activityResultMap;

@synthesize server = m_server;

- (id)init
{
    PacketParser *parser = nil;
    
    if (self = [super init])
    {
        s_sTag = NSStringFromClass([self class]);
        m_activityResultMap = [[[NSMutableDictionary alloc] init] autorelease];
        
        parser = [[parser init] autorelease];
        m_server = [[m_server initWithPacketParser:parser] autorelease];
    }
    return self;
}

- (int)generateRequestCode
{
    return m_nRequestCode = m_nRequestCode + 1;
}

- (void)onAppTerminate
{
    [[[[StopCommand alloc] init] autorelease] execute];
    [super onAppTerminate];
}

- (void)registerCommandForResult:(int)requestCode commandForResult:(id<Command>)command
{
    @synchronized(m_activityResultMap)
    {
        [m_activityResultMap setObject:command forKey:[NSString stringWithFormat:@"%i", requestCode]];
    }
}

- (void)bluetoothCommand:(CDVInvokedUrlCommand*)command
{
    NSString *action = [command.arguments objectAtIndex:0];
    id<Command> btCommand = nil;
    
    if ([ACTION_REQUEST_DISCOVERABLE isEqualToString:action])
    {
            
    }
    else if([ACTION_IS_SUPPORTED isEqualToString:action])
    {
        btCommand = [[[IsSupportedCommand alloc] initWithPlugin:self named:command.callbackId] autorelease];
    }
    else if ([ACTION_LISTEN isEqualToString:action])
    {
        btCommand = [[[ListenCommand alloc] initWithPlugin:self named:command.callbackId] autorelease];
    }
    else if ([ACTION_GETBONDEDDEVICES isEqualToString:action])
    {
        btCommand = [[[BondedDevicesCommand alloc] initWithPlugin:self named:command.callbackId] autorelease];
    }
    else if ([ACTION_IS_DISABLED isEqualToString:action])
    {
    
    }
    else if ([ACTION_IS_ENABLED isEqualToString:action])
    {
        btCommand = [[[IsEnabledCommand alloc] initWithPlugin:self named:command.callbackId] autorelease];
    }
    else
    {
        [self.commandDelegate sendPluginResult:
         [CDVPluginResult resultWithStatus:CDVCommandStatus_INVALID_ACTION]
                                    callbackId:command.callbackId];
        return;
    }
    
    [Invoker execute:btCommand];
    
    [self.commandDelegate sendPluginResult:
     [CDVPluginResult resultWithStatus:CDVCommandStatus_NO_RESULT]
                                callbackId:command.callbackId];
}

@end
