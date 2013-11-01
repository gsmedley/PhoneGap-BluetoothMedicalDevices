//
//  ANDPacketParser.m
//  DevicePlugin
//
//  Created by Caitlin Potter on 1/23/13.
//
//

#import "NJLog.h"
#import "ANDPacketParser.h"
#import "ANDHeader.h"
#import "ANDPacket.h"

@implementation ANDPacketParser
+ (id) packetParser
{
    return [ [ [ ANDPacketParser alloc ] init ] autorelease ];
}

static const uint8_t DATA_REFUSED[] = { 'P', 'W', 'A', '1' };
static const uint8_t ENTER_CONFIGURATION[] = { 'P', 'W', 'A', '2' };
static const uint8_t CON_REQ_PACKET[] = { 'P', 'W', 'R', 'Q', 'C', 'F' };
static const uint8_t CHANGE_SUCCESSFUL[] = { 'P', 'W', 'C', '1' };

- (void)respond:(id)header
               :(NSInputStream *)inputStream
               :(NSOutputStream *)outputStream
{
    if( [ header class ] != [ ANDHeader class ] )
    {
        return;
    }
    @try
    {
        uint8_t buffer[0x100] = { 0 };
        int read;
        int written;
        ANDHeader *head = (ANDHeader *)header;
        NSCalendar *calendar = [ [ [ NSCalendar alloc ] init ] autorelease ];
        NSDate *date = [ NSDate date ];
        NSDateComponents *components = [ calendar components:0 fromDate:date ];
        NSDateComponents *trans = [ calendar components:0
                                    fromDate:head.transmissionDate ];
        int year = [ components year ];
        int month = [ components month ];
        int day = [ components day ];
        int hour = [ components hour ];
        int min = [ components minute ];


        // Ensure that the transmission time was in the last 15 minutes
        if( trans.year != year || trans.month != month || trans.day != day ||
           trans.hour != hour || !( trans.minute - min <= 15 ))
        {
            // TODO: check for read failure?
            [ outputStream write:ENTER_CONFIGURATION
                           maxLength:sizeof(ENTER_CONFIGURATION) ];

            read = [ inputStream read:buffer maxLength:sizeof(buffer) ];
            if( read == sizeof(CON_REQ_PACKET) && memcmp( buffer,
                CON_REQ_PACKET, sizeof(CON_REQ_PACKET) ) )
            {
                NSDate *now;
                NSDateComponents *components;
                // Recieved CON_REQ_PACKET
                uint8_t ack[82];
                int top = year / 256;
                int bottom = year / top * 256;
                memset( ack, 0, sizeof(ack) );

                now = [ NSDate date ];
                components = [ calendar components:0 fromDate:now ];

                ack[62] = 1; // set clock
                ack[63] = bottom;
                ack[64] = top;
                ack[65] = month;
                ack[66] = day;
                ack[67] = hour;
                ack[68] = min;
                ack[69] = [ components second ];

                // TODO: check for write failure
                written = [ outputStream write:ack maxLength:sizeof(ack) ];

                read = [ inputStream read:buffer maxLength:sizeof(buffer) ];

                // Check that we've got our confirmation.
                if( read == 4 )
                {
                    if( memcmp( buffer, CHANGE_SUCCESSFUL,
                                sizeof(CHANGE_SUCCESSFUL) ) )
                    {
                        //NJLog( self, @"Date changed" );
                    }
                    else
                    {
                        //NJLog( self, @"Date not changed" );
                    }
                }
            }
        }
        else
        {
            [ outputStream write:DATA_REFUSED maxLength:sizeof(DATA_REFUSED) ];
        }
    }
    @catch( NSException *e )
    {
        //NJError( self, @"Failed to respond to device: %@", [ e reason ] );
        [ NSException raise:@"IOException"
                      format:@"Failed to respond to device: %@",
                      [ e reason ] ];
    }
}

//
// parsePacket
//
// Format packet as string
- (id) parsePacket:(id)_packet
{
    ANDPacket *packet = (ANDPacket *)_packet;
    ANDHeader *header;
    NSMutableDictionary *dict = [ [ [ NSMutableDictionary alloc ] init ]
                                  autorelease ];

#define UNIXTIME (1)
#if UNIXTIME
    NSNumber *measurementDate;
#else
    NSDateFormatter *dfmt;
    NSString *measurementDate;
#endif

    if( packet == nil )
    {
        return nil;
    }

    header = packet.header;
#if UNIXTIME
    measurementDate = [ NSNumber numberWithLongLong:((time_t)floor(
        [ header.measurementDate timeIntervalSince1970 ])) ];
#else
    dfmt = [ [ [NSDateFormatter alloc ] init ] autorelease ];
    dfmt.dateFormat = @"YYYY-MM-DDTHH:mm:ss.sssZ";
    measurementDate = [ dfmt stringFromDate:header.measurementDate ];
#endif

    [ dict setValue:measurementDate forKey:@"time" ];
    [ packet serializeData:dict ];

    return dict;
}

- (id)createPacket:(id)_header :(NSInputStream *)inputStream
{
    ANDHeader *header = (ANDHeader *)_header;
    NSOutputStream *os = nil;
    NSData *dat = nil;

    int length;

    if( header == nil )
    {
        return nil;
    }

    length = header.packetLength;

    os = [ NSOutputStream outputStreamToMemory ];
    [ self copy:os :inputStream :length ];
    dat = [ os propertyForKey:NSStreamDataWrittenToMemoryStreamKey ];
    length = dat.length;

    switch( header.deviceType )
    {
        // Is this not a better way to figure out which kind of packet we're
        // reading?

        // Weight scale packet:
        case AND_UC321PBT:
        {
            return [ ANDWeightPacket packet:header withData:dat length:length ];
        }
        break;

        case AND_UA767PBT:
        {
            return [ ANDBloodPressurePacket packet:header withData:dat length:length ];
        }
        break;

        default:
        {
            // Unsupported device
            return nil;
        }
        break;
    }
}

- (id)createHeader:(NSInputStream *)inputStream
{
    return [ ANDHeader header:inputStream ];
}

- (void)postBody:(NSInputStream *)is :(NSOutputStream *)os :(id)packet
{
    [ self respond:((ANDPacket *)packet).header:is:os ];
}

@end
