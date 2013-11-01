//
//  ANDHeader.m
//  DevicePlugin
//
//  Created by Caitlin Potter on 1/25/13.
//
//

#import "ANDHeader.h"
#import "../../util/NJBSwap.h"

struct ANDPacketDate
{
    uint16_t year;
    uint8_t month;
    uint8_t day;
    uint8_t hour;
    uint8_t minute;
    uint8_t second;
} __attribute__((aligned(1), packed));

void
ANDDateToHost( struct ANDPacketDate *date )
{
    date->year = NJInt16FromBigEndian( date->year );
    // Are other date components packed in some way?
}

struct ANDPacketHeaderData
{
    uint16_t packetType;
    uint32_t packetLength;
    uint16_t deviceType;
    uint8_t flag;
    struct ANDPacketDate measurement;
    struct ANDPacketDate transmission;
    uint8_t deviceId[6];
    uint8_t accessPoint[6];
    uint8_t serialNo[12];
    uint8_t reserved[10];
    uint8_t batteryStatus;
    uint8_t reserved2;
    uint8_t revision;
} __attribute__((aligned(1), packed));

// Helper function to return NSDateComponents from an ANDPacketDate
static NSDateComponents *
componentsFromPacketDate( struct ANDPacketDate *pd )
{
    NSDateComponents *date = [ [ [ NSDateComponents alloc ] init ] autorelease ];
    date.year = pd->year;
    date.month = pd->month;
    date.day = pd->day;
    date.hour = pd->hour;
    date.minute = pd->minute;
    date.second = pd->second;
    return date;
}

// Helper function to return an NSString from a fixed-length string in AND's packet header
static NSString *
stringify( const uint8_t *bytes, size_t length )
{
    // If there's a better way to do this, I don't know what it is
    NSMutableString *ms = [ NSMutableString stringWithCapacity:length ];
    for( ; length--; bytes++ )
    {
        [ ms appendFormat:@"%c", *bytes ];
    }
    return [ NSString stringWithString:ms ];
}

@implementation ANDHeader
@synthesize packetType = m_packetType;
@synthesize packetLength = m_packetLength;
@synthesize deviceType = m_deviceType;
@synthesize flag = m_flag;
@synthesize measurementDate = m_measurementDate;
@synthesize transmissionDate = m_transmissionDate;
@synthesize deviceId = m_deviceId;
@synthesize accessPoint = m_accessPoint;
@synthesize serialNo = m_serialNo;
@synthesize batteryStatus = m_batteryStatus;
@synthesize revision = m_revision;

+(id)header:(NSInputStream *)inputStream
{
    if( inputStream == nil )
    {
        return nil;
    }
    return [ [ [ ANDHeader alloc ] init:inputStream ] autorelease ];
}

-(id)init:(NSInputStream *)inputStream
{
    if( self = [ super init ] )
    {
        struct ANDPacketHeaderData data;
        const size_t size = sizeof(data); // For debugging
        int read = [ inputStream read:(uint8_t *)&data maxLength:size ];
        if( read <= 0 )
        {
            return nil;
        }
        m_packetType = NJInt16FromBigEndian( data.packetType );
        m_packetLength = NJInt32FromBigEndian( data.packetLength );
        m_deviceType = NJInt16FromBigEndian( data.deviceType );
        m_flag = data.flag;
        
        // Correct endianness of date
        ANDDateToHost( &data.measurement );
        ANDDateToHost( &data.transmission );
        
        m_measurementDate = [ componentsFromPacketDate( &data.measurement ) date ];
        m_transmissionDate = [ componentsFromPacketDate( &data.transmission ) date ];
        
        m_deviceId = stringify( data.deviceId, sizeof(data.deviceId) );
        m_accessPoint = stringify( data.accessPoint, sizeof( data.accessPoint ) );
        m_serialNo = stringify( data.serialNo, sizeof(data.serialNo) );
        m_batteryStatus = data.batteryStatus;
        m_revision = data.revision;
    }
    return self;
}


@end
