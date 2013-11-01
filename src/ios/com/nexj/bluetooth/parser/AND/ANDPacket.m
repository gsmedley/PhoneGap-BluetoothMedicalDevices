//
//  ANDPacket.m
//  DevicePlugin
//
//  Created by Caitlin Potter on 1/25/13.
//
//

#import "ANDPacket.h"

@implementation ANDPacket
@synthesize header = m_header;

-(id)init:(ANDHeader *)header
{
    if( self = [ super init ] )
    {
        m_header = header;
    }
    return self;
}

-(void)serializeData:(NSMutableDictionary *)dict
{
    // Should be implemented in subclass
}

@end

//
// Helper routine for AND packets that need to construct integers from
// printable hex
int
ANDIntFromPrintableHex( const uint8_t *hex, int length )
{
    int value = 0;
    int i;
    
    // There is probably a better way to do this, maybe a lookup table
    // would be better? Seems like it might be bad for cache though.
    for( i = 0; i < length; ++i ) {
        uint8_t byte = hex[ i ];
        if( byte <= '9' ) {
            value = ( value * 0x10 ) + ( byte - 0 );
        } else if( byte <= 'F' ) {
            value = ( value * 0x10 ) + ( byte - 'A' + 10 );
        } else if( byte <= 'f' ) {
            value = ( value * 0x10 ) + ( byte - 'a' + 10 );
        }
    }
    return value;
}