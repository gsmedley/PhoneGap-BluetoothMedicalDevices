//
//  ANDWeightPacket.m
//  DevicePlugin
//
//  Created by Caitlin Potter on 1/25/13.
//
//

#import "ANDWeightPacket.h"

static NSString *LEAD_BYTES_ST = @"ST.";
static const uint8_t unit_lbs[] = "lb";
static const uint8_t unit_kg[] = "kg";
static NSString *UNIT_LBS = @"lb";
static NSString *UNIT_KG = @"kg";
static NSString *UNIT_UNKNOWN = @"??";

@implementation ANDWeightPacket

@synthesize leadBytes = m_leadBytes;
@synthesize weight = m_weight;
@synthesize units = m_units;

+(id)packet:(ANDHeader *)header withData:(NSData *)data length:(int)len
{
    return [ [ [ ANDWeightPacket alloc ] init:header withData:data length:len ] autorelease ];
}

-(id)init:(ANDHeader *)header withData:(NSData *)data length:(int)len
{
    const uint8_t *b = (const uint8_t *)data.bytes;
    if( self = [ super init:header ] )
    {
        switch( len )
        {
            case 14: // 14 byte weight packet
            {
                char bweight[8];
                memcpy(bweight,b + 3, 7 );
                
                // This may not work: The '+' sign might not be parsed as expected. It's also likely the most expensive
                // way to do this, since it requires a memcpy as well as a string allocation. We can do better!
                bweight[7] = 0;
                m_weight = [ [ NSString stringWithCString:bweight encoding:NSASCIIStringEncoding ] floatValue ];
                m_leadBytes = LEAD_BYTES_ST;
                if( memcmp( (b + 10 ), unit_lbs, 2 ) == 0 )
                {
                    m_units = ANDWeightLBS;
                }
                else if( memcmp( (b + 10 ), unit_kg, 2 ) == 0 )
                {
                    m_units = ANDWeightKGS;
                }
                else
                {
                    // Unknown unit!
                }
                
                // Do we really care about the CRLF at the end?
                if( b[12] != 0x0D || b[13] != 0x0A )
                {
                    // Doesn't end with CRLF! Do something!
                }
            }
            break;
            
            default:
            {
                // Unsupported packet
                self = nil;
            }
        }
    }
    return self;
}

-(void)serializeData:(NSMutableDictionary *)dict
{
    NSString *unitString = nil;

    if( self.units == ANDWeightKGS )
    {
        unitString = UNIT_KG;
    }
    else if( self.units == ANDWeightLBS )
    {
        unitString = UNIT_LBS;
    }
    else
    {
        unitString = UNIT_UNKNOWN;
    }
    
    [ dict setValue:[ NSNumber numberWithFloat:self.weight ] forKey:@"weight" ];
    [ dict setValue:unitString forKey:@"system" ];
}

@end
