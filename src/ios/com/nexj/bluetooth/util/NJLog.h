//
//  NJLog.h
//  DevicePlugin
//
//  Created by Caitlin Potter on 1/22/13.
//
//

#import <Foundation/Foundation.h>

#ifndef NJLog_h
#define NJLog_h

// Log a message
void NJLog( id object, NSString *format, ... );

// Log a warning
void NJWarning( id object, NSString *format, ... );

// Log an error
void NJError( id object, NSString *format, ... );

// Log a "fatal error" (Does not terminate the application)
void NJFatal( id object, NSString *format, ... );

#endif
