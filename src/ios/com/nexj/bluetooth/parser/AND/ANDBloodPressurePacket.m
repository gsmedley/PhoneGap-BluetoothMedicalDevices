//
//  ANDBloodPressurePacket.m
//  DevicePlugin
//
//  Created by Caitlin Potter on 1/25/13.
//
//

#import "ANDBloodPressurePacket.h"

@implementation ANDBloodPressurePacket
-(int)systolicPressure
{
    return m_pulsePressure + m_diastolicPressure;
}
@synthesize isValid = m_isValid;
@synthesize pulsePressure = m_pulsePressure;
@synthesize diastolicPressure = m_diastolicPressure;
@synthesize pulseRate = m_pulseRate;
@synthesize meanArterialPressure = m_meanArterialPressure;

+(id)packet:(ANDHeader *)header withData:(NSData *)data length:(int)len
{
    return [ [ [ ANDBloodPressurePacket alloc ] init:header withData:data length:len ] autorelease ];
}

-(id)init:(ANDHeader *)header withData:(NSData *)data length:(int)len
{
    const uint8_t *b = (const uint8_t *)data.bytes;
    if( self = [ super init:header ] )
    {
        switch( len )
        {
            case 10: // 14 byte weight packet
            {
                m_isValid = b[0] == '8' && b[1] == '0';
                m_pulsePressure = ANDIntFromPrintableHex2( b + 2 );
                m_diastolicPressure = ANDIntFromPrintableHex2( b + 4 );
                m_pulseRate = ANDIntFromPrintableHex2( b + 6 );
                m_meanArterialPressure = ANDIntFromPrintableHex2( b + 8 );
            }
            break;
    
            default:
            {
                // Unsupported packet
            }
            break;
        }
    }
    return self;
}

-(void)serializeData:(NSMutableDictionary *)dict
{
    [ dict setValue:[ NSNumber numberWithInt:self.systolicPressure ] forKey:@"systolic" ];
    [ dict setValue:[ NSNumber numberWithInt:self.diastolicPressure ] forKey:@"diastolic" ];
    [ dict setValue:[ NSNumber numberWithInt:self.pulseRate ] forKey:@"pulse" ];
    [ dict setValue:[ NSNumber numberWithInt:self.meanArterialPressure ] forKey:@"mean" ];
}

@end
