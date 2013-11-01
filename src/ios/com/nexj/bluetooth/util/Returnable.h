//
//  ReturnableCommand.h
//  DevicePlugin
//
//  Created by Caitlin Potter on 1/16/13.
//
//

#import <Foundation/Foundation.h>
#import "CDVPlugin.h"

// The 'execute' method must be implemented in all subclasses
// The default implenmentation will simply throw an exception, log an error, and
// more than likely leak resources.
@interface Returnable : NSObject

@property (readonly) CDVPlugin *plugin;
@property (readonly) NSString *callBackId;

- (id)initWithPlugin:(CDVPlugin *)plugin named:(NSString *)callbackId;
- (void)returnResult:(const CDVPluginResult *const)result named:(NSString *)callbackId;

@end
