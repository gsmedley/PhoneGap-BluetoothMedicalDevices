//
//  ReturnableCommand.m
//  DevicePlugin
//
//  Created by Caitlin Potter on 1/16/13.
//
//

#import <dispatch/dispatch.h>
#import "CDVPluginResult.h"
#import "Returnable.h"

@implementation Returnable

@synthesize plugin = m_plugin;
@synthesize callBackId = m_callBackId;

- (id)init
{
    if( self = [ super init ] )
    {
        m_plugin = nil;
        m_callBackId = nil;
    }
    return self;
}

- (id)initWithPlugin:(CDVPlugin *)plugin named:(NSString *)callbackId
{	
	if( self = [ self init ] )
	{
        m_plugin = plugin;
        m_callBackId = callbackId;
	}
	return self;
}

- (void)returnResult:(CDVPluginResult *)result named:(NSString *)callbackId
{
    // Dispatch callback to run on main thread (should be somewhat equivalent to UI thread on Android)
    // ...is Cordova running our background stuff in a different thread?
	dispatch_async( dispatch_get_main_queue(), ^{
		result.keepCallback = [ NSNumber numberWithBool:true ];
		[ m_plugin.commandDelegate sendPluginResult:result callbackId:callbackId ];
	});
}

@end

