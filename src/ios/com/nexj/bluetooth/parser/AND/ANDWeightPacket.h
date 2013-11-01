//
//  ANDWeightPacket.h
//  DevicePlugin
//
//  Created by Caitlin Potter on 1/25/13.
//
//
#import "ANDPacket.h"

typedef
enum ANDWeightUnit_t
{
    ANDWeightLBS,
    ANDWeightKGS,
} ANDWeightUnit;

@interface ANDWeightPacket : ANDPacket
@property (readonly)NSString *leadBytes;
@property (readonly)float weight;
@property (readonly)ANDWeightUnit units;
+(id)packet:(ANDHeader *)header withData:(NSData *)data length:(int)length;
-(id)init:(ANDHeader *)header withData:(NSData *)data length:(int)length;
-(void)serializeData:(NSMutableDictionary *)dict;
@end
