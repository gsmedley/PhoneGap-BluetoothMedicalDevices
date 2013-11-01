//
//  NJLog.m
//  DevicePlugin
//
//  Created by Caitlin Potter on 1/22/13.
//
//

#import "NJLog.h"

// Basic logging function, which is used by others
void NJLogv( NSString *type, id object, NSString *format, va_list av )
{
    NSString *f;
    f = type == nil ? [ NSString string ] : [ NSString stringWithFormat:@"%@: ", type ];
    if( object != nil )
    {
        f = [ f stringByAppendingFormat:@"%@ ", NSStringFromClass( [object class] ) ];
    }
    if( format != nil )
    {
        f = [ f stringByAppendingString:format ];
    }

    NSLogv( f, av );

}

void NJLog( id object, NSString *format, ... )
{
    va_list args;
    va_start(args,format);
    NJLogv( nil, object, format, args );
    va_end(args);
}

// Log a warning
void NJWarning( id object, NSString *format, ... )
{
    va_list args;
    va_start(args,format);
    NJLogv( @"warning", object, format, args );
    va_end(args);
}

// Log an error
void NJError( id object, NSString *format, ... )
{
    va_list args;
    va_start(args,format);
    NJLogv( @"error", object, format, args );
    va_end(args);
}

// Log a "fatal" error
void NJFatal( id object, NSString *format, ... )
{
    va_list args;
    va_start(args,format);
    NJLogv( @"fatal error", object, format, args );
    va_end(args);
}
