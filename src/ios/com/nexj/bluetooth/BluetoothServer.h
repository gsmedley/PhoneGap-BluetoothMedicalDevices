//
//  BluetoothServer.h
//  DevicePlugin
//
//  Created by Caitlin Potter on 1/9/13.
//
//

#import <Foundation/Foundation.h>
#import <ExternalAccessory/ExternalAccessory.h>
#import <CoreBluetooth/CoreBluetooth.h>
#import "PacketParser.h"
#import <CoreBluetooth/CoreBluetooth.h>
@class EASession;
@interface BluetoothServer : NSObject <CBCentralManagerDelegate>
{
    PacketParser *m_packetParser;
    NSMutableArray *m_bluetoothSockets;
}

@property (readonly) NSArray *bondedDevices;

- (id) initWithPacketParser:(id)parser;
- (BOOL) isSupported;
- (BOOL) isEnabled;
- (id) listen:(NSDictionary *)params;
- (id) listenToSession:(EASession *)session;
- (BOOL) isSupported;
- (NSArray *)filterSockets:(NSArray *)sockets byType:(NSString *)type;
- (NSArray *)filterSockets:(NSArray *)sockets bySerial:(NSString *)serial;
@end
