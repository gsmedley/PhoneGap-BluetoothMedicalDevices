//
//  PacketParser.m
//  DevicePlugin
//
//  Created by Cait Potter on 1/14/13.
//  Copyright (c) 2013 __MyCompanyName__. All rights reserved.
//

#import "PacketParser.h"
#import "NJLog.h"

@implementation PacketParser

- (id)init
{
    if( self = [ super init ] )
    {
        return self;
    }
    return nil;
}

- (id)dataToJSON:(NSInputStream *)inputStream :(NSOutputStream *)outputStream
{
    // There is probably a better way to do this.
    if([self conformsToProtocol:@protocol(PacketParserProtocol)])
    {
        [ NSException raise:NSInvalidArgumentException
                      format:@"PacketParser does not conform to "
                             @"PacketParserProtocol" ];
    }
    else
    {
        id header;
        id packet;
        id json;

        // Header
        [ self preHeader:inputStream :outputStream ];
        @try
        {
            header = [ self createHeader:inputStream ];
        }
        @catch (NSException *e)
        {
            NJLog( self, @"Error occured during header creation: %@",
                   e.reason );
            @throw e;
        }
        [ self postHeader:inputStream :outputStream :header ];

        // Body
        [ self preBody:inputStream :outputStream :header ];
        @try
        {
            packet = [ self createPacket:header :inputStream ];
        }
        @catch (NSException *e)
        {
            NJLog( self, @"Error occured during packet creation: %@",
                   e.reason );
            @throw e;
        }
        [ self postBody:inputStream :outputStream :packet ];

        // Parsing
        [ self preParsing:inputStream :outputStream :packet ];
        @try
        {
            json = [ self parsePacket:packet ];
        }
        @catch (NSException *e)
        {
            NJLog( self, @"Error occurred during JSON object creation: %@",
                   e.reason );
            [ NSException raise:@"IOException"
                          format:e.reason ];
        }
        [ self postParsing:inputStream :outputStream :packet ];

        return json;
    }
}

- (void)copy:(NSOutputStream *)outputStream :(NSInputStream *)inputStream :(int)nLength
{
    int nCount;
    int b;
    uint8_t buffer[0x200];
    for( nCount = 0; nCount < nLength; ++nCount )
    {
        b = [ inputStream read:buffer maxLength:sizeof(buffer) ];
        if( b < 0 ) {
            // Read error occured. Throw?
            break;
        }
        if( [ outputStream write:buffer maxLength:b ] <= 0 ) {
            // Write error occured. Throw?
            break;
        }
    }
}

// Stub implementations (TODO: use NJError() to log these
- (void)respond:(id)header :(NSInputStream *)inputStream :(NSOutputStream *)outputStream
{
    NJError( self, @"method `-respond' not implemented"  );
}

- (id) parsePacket:(id)packet
{
    NJError( self, @"method `-parsePacket' not implemented" );
    return nil;
}

- (id)createPacket:(id)header :(NSInputStream *)is
{
    NJError( self, @"method `-createPacket' not implemented" );
    return nil;
}

- (id)createHeader:(NSInputStream *)is
{
    NJError( self, @"method `-createHeader' not implemented" );
    return nil;
}

- (void)preHeader:(NSInputStream *)is :(NSOutputStream *)os
{
}

- (void)postHeader:(NSInputStream *)is :(NSOutputStream *)os :(id)header
{
}

- (void)preBody:(NSInputStream *)is :(NSOutputStream *)os :(id)header
{
}

- (void)postBody:(NSInputStream *)is :(NSOutputStream *)os :(id)packet
{
}

- (void)preParsing:(NSInputStream *)is :(NSOutputStream *)os :(id)packet
{
}

- (void)postParsing:(NSInputStream *)is :(NSOutputStream *)os :(id)packet
{
}
@end
