//
//  ANDHeader.h
//  DevicePlugin
//
//  Created by Caitlin Potter on 1/25/13.
//
//

#import <Foundation/Foundation.h>

// A&D Medical device types:
typedef
enum ANDDeviceType_t
{
    // UC-321PBT: Precision Health Scale
    AND_UC321PBT = 0x0141,
    
    // UA-767PBT: Upper Arm Blood Pressure Monitor
    AND_UA767PBT = 0x02FF,
} ANDDeviceType;

// Packet header
@interface ANDHeader : NSObject
@property (readonly) int packetType;
@property (readonly) int packetLength;
@property (readonly) ANDDeviceType deviceType;
@property (readonly) uint8_t flag;
@property (readonly) NSDate *measurementDate;
@property (readonly) NSDate *transmissionDate;
@property (readonly) NSString *deviceId;
@property (readonly) NSString *accessPoint;
@property (readonly) NSString *serialNo;
@property (readonly) uint8_t batteryStatus;
@property (readonly) uint8_t revision;

+(id)header:(NSInputStream *)inputStream;
-(id)init:(NSInputStream *)inputStream;
@end