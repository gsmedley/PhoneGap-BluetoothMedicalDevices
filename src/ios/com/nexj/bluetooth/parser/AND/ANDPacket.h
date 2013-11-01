//
//  ANDPacket.h
//  DevicePlugin
//
//  Created by Caitlin Potter on 1/25/13.
//
//

#import <Foundation/Foundation.h>
#import "ANDHeader.h"

@interface ANDPacket : NSObject
{
}
@property (readonly) ANDHeader *header;
-(id)init:(ANDHeader *)header;
-(void)serializeData:(NSMutableDictionary *)dict;
@end

int ANDIntFromPrintableHex( const uint8_t *hex, int length );
inline int ANDIntFromPrintableHex2( const uint8_t *hex )
{
    return ANDIntFromPrintableHex( hex, 2 );
}

#import "ANDBloodPressurePacket.h"
#import "ANDWeightPacket.h"
