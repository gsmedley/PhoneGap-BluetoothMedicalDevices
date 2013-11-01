//
//  PacketParser.h
//  DevicePlugin
//
//  Created by Caitlin Potter on 1/10/13.
//
//

#import <Foundation/Foundation.h>
#import <Foundation/NSStream.h>

@protocol PacketParserProtocol <NSObject>
@required
- (void)respond:(id)header :(NSInputStream *)inputStream :(NSOutputStream *)outputStream;
- (id)parsePacket:(id)packet;
- (id)createPacket:(id)header :(NSInputStream *)is;
- (id)createHeader:(NSInputStream *)is;

- (void)preHeader:(NSInputStream *)is :(NSOutputStream *)os;
- (void)postHeader:(NSInputStream *)is :(NSOutputStream *)os :(id)header;
- (void)preBody:(NSInputStream *)is :(NSOutputStream *)os :(id)header;
- (void)postBody:(NSInputStream *)is :(NSOutputStream *)os :(id)packet;
- (void)preParsing:(NSInputStream *)is :(NSOutputStream *)os :(id)packet;
- (void)postParsing:(NSInputStream *)is :(NSOutputStream *)os :(id)packet;
@end

@interface PacketParser : NSObject <PacketParserProtocol>
- (id)init;
- (id)dataToJSON :(NSInputStream *)inputStream :(NSOutputStream *)outputStream;

// Stub implementations of these routines
- (void)respond:(id)header :(NSInputStream *)inputStream :(NSOutputStream *)outputStream;
- (id)parsePacket:(id)packet;
- (id)createPacket:(id)header :(NSInputStream *)is;
- (id)createHeader:(NSInputStream *)is;
- (void)copy:(NSOutputStream *)outputStream :(NSInputStream *)inputStream :(int)nLength;
- (void)preHeader:(NSInputStream *)is :(NSOutputStream *)os;
- (void)postHeader:(NSInputStream *)is :(NSOutputStream *)os :(id)header;
- (void)preBody:(NSInputStream *)is :(NSOutputStream *)os :(id)header;
- (void)postBody:(NSInputStream *)is :(NSOutputStream *)os :(id)packet;
- (void)preParsing:(NSInputStream *)is :(NSOutputStream *)os :(id)packet;
- (void)postParsing:(NSInputStream *)is :(NSOutputStream *)os :(id)packet;
@end
