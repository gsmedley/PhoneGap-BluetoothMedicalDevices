//
//  ANDBloodPressurePacket.h
//  DevicePlugin
//
//  Created by Caitlin Potter on 1/25/13.
//
//

#import "ANDPacket.h"

@interface ANDBloodPressurePacket : ANDPacket
@property (readonly) BOOL isValid;
@property (readonly) int pulsePressure; // Systolic pressure minus diastolic pressure
@property (readonly) int systolicPressure;
@property (readonly) int diastolicPressure;
@property (readonly) int pulseRate;
@property (readonly) int meanArterialPressure;

+(id)packet:(ANDHeader *)header withData:(NSData *)data length:(int)len;
-(id)init:(ANDHeader *)header withData:(NSData *)data length:(int)len;
-(void)serializeData:(NSMutableDictionary *)dict;
@end
