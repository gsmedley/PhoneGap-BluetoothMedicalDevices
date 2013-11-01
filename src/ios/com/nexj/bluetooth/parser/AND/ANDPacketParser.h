//
//  ANDPacketParser.h
//  DevicePlugin
//
//  Created by Caitlin Potter on 1/23/13.
//
//

#import "PacketParser.h"

@interface ANDPacketParser : PacketParser
+ (id) packetParser;

// PacketParserProtocol
- (void)respond:(id)header :(NSInputStream *)inputStream :(NSOutputStream *)outputStream;
- (id) parsePacket:(id)packet;
- (id)createPacket:(id)header :(NSInputStream *)inputStream;
- (id)createHeader:(NSInputStream *)inputStream;
@end

