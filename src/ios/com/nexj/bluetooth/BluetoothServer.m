//
//  BluetoothServer.m
//  DevicePlugin
//
//  Created by Caitlin Potter on 1/9/13.
//
//

#import <ExternalAccessory/ExternalAccessory.h>
#import "BluetoothServer.h"

@interface BluetoothServer ()
{
    CBCentralManager *m_centralManager;
}
@end

@implementation BluetoothServer 

@synthesize bondedDevices = m_bondedDevices;

/**
 * 'class members' -- not visible beyond scope of BluetoothServer.m.
 */
static const NSUUID *s_sServiceId;
static const NSString *s_sTag;
static const NSString *const s_sServiceName = @"PWAccessP";

-(id) init
{
    if( self = [ super init ] )
    {
        // OLD: Logging based on CoreBluetooth's manager state
        // maybe we should bring CoreBluetooth back in for this purpose, as ExternalAccessory framework
        // provides no way to access this information.
        /*
        state = self->m_centralManager.state;
        if( state == CBCentralManagerStatePoweredOn )
        {
            NSLog( s_sTag, "Bluetooth is currently activated" );
        }
        else if( state == CBCentralManagerStatePoweredOff
            || state == CBCentralManagerStateResetting
            || state == CBCentralManagerStateUnknown )
        {
            NSLog( s_sTag, "Bluetooth is not currently activated" );
        }
        else if( state == CBCentralManagerStateUnsupported )
        {
            NSLog( s_sTag, "Bluetooth 4.0/Low Energy is not supported on this platform." );
        }
        else
        {
            NSLog( s_sTag, "Bluetooth is not supported on this device." );
        }
        */
        // Register for notifications
        [[NSNotificationCenter defaultCenter] addObserver:self
            selector:@selector(accessoryDidConnect:)
            name:EAAccessoryDidConnectNotification object:nil];
        
        [[NSNotificationCenter defaultCenter] addObserver:self
            selector:@selector(accessoryDidDisconnect:)
            name:EAAccessoryDidDisconnectNotification object:nil];
        
        [[EAAccessoryManager sharedAccessoryManager]
            registerForLocalNotifications];
        
        // Initialize CoreBluetooth as well:
        m_centralManager = [ [ [ CBCentralManager alloc ]
            initWithDelegate:self queue:dispatch_get_main_queue() ] autorelease ];
        // Create array of connections
        self->m_bluetoothSockets = [ [ NSMutableArray alloc ]
            initWithCapacity:16 ];
    }
    
    s_sServiceId = [[[NSUUID alloc]
        initWithUUIDString:@"00001101-0000-1000-8000-00805F9B34FB"]
        autorelease]; s_sTag = NSStringFromClass([BluetoothServer class]);
    
    return self;
}

-(id) initWithPacketParser:(id)parser
{
    // Ensure that we have a valid PacketParser implementation
    if( [parser conformsToProtocol:@protocol(PacketParserProtocol)] == false )
    {
        return nil;
    }
    
    // initialize the superclass
    if( self = [ self init ] )
    {
        self->m_packetParser = parser;
    }
    
    s_sServiceId = [[[NSUUID alloc]
        initWithUUIDString:@"00001101-0000-1000-8000-00805F9B34FB"]
        autorelease];
    s_sTag = NSStringFromClass([BluetoothServer class]);
    
    return self;
}

/**
 * Needs to be implemented.
 */
- (void)centralManagerDidUpdateState:(CBCentralManager *)central
{
    
}


// Return true if we were able to create the CBCentralManager at all
-(BOOL) isSupported
{
    // Should this always be true? I don't think [ EAAccessoryManager
    // sharedAccessoryManager ] ever returns nil Again, this is NOT
    // accurate for older devices
    if( m_centralManager != nil && [ m_centralManager state ] !=
        CBCentralManagerStatePoweredOn ) {
        return false;
    }
    return true;
}

// Return true if the bluetooth adapter (wrapped by CBCentralManager)
// is powered on and accessible by the device.
-(BOOL) isEnabled
{
    // Should we use CoreBluetooth to get access to this information?
    // the ExternalAccessory framework doesn't expose a way to get
    // this information.
    // If CBCentralManagerStateUnsupported, We don't actually know if
    // it's enabled or not, on hardware that doesn't support BT 4.0/LE
    // (eg iPad 2)
    if( m_centralManager != nil ) {
        return m_centralManager.state == CBCentralManagerStatePoweredOn
            || m_centralManager.state == CBCentralManagerStateUnsupported;
    }
    return true;
}

// Currently, we open a Session with a remote device (Accessory) as
// soon as we are notified of a connection.  This means that we don't
// need to wait for and accept a client from the 'listen' method. We
// can simply parse the data we've received from it, and then close it.
//
// I'm not sure this is exactly the kind of behaviour we'll want in
// this method, depending on what closing the session on iOS entails.
// I think if the session closes, but the device remains connected,
// then that's fine. However, in that case, we will only get one
// packet per connection with the current code, so it's not really
// ideal.
//
// Basically I am not sure how this code should look until we can test it.
//
-(id) listen:(NSDictionary *)params
{
    EASession *session = nil;
    NSString *type = nil;
    NSString *serial = nil;
    int time = 0;
    int num = 0;
    
    NSArray *array = [ NSArray array ];
    if( params != nil )
    {
        type = [ params objectForKey:@"type" ];
        serial = [ params objectForKey:@"serial" ];
        time = [ [ params objectForKey:@"time" ] integerValue ];
        num = [ [ params objectForKey:@"num" ] integerValue ];
    }
    if( !time && !num )
    {
        num = 1;
    }
    
    @try
    {
        // Start connecting to bluetooth devices, if we haven't yet.
        [ self start ];
        
        @synchronized( m_bluetoothSockets )
        {
            NSArray *sockets = m_bluetoothSockets;
            if( sockets.count > 0 )
            {
                sockets = [ self filterSockets:sockets byType:type ];
                sockets = [ self filterSockets:sockets bySerial:serial ];
                
                if( session == nil && sockets.count > 0 ) {
                    NSDate *now = [ NSDate date ];
                    NSDate *end = nil;
                    if( time > 0 ) {
                        end = [ NSDate dateWithTimeInterval:time sinceDate:now ];
                    }
                    session = [ sockets lastObject ];
                    
                    while( 1 ) // Forever
                    {
                        if( end )
                        {
                            now = [ NSDate date ];
                            NSComparisonResult result = [ now compare:end ];
                            if( result == NSOrderedDescending || result == NSOrderedSame )
                            {
                                break;
                            }
                        }
                        
                        if( num && array.count == num )
                        {
                            break;
                        }
                        
                        array = [ array arrayByAddingObject:[ self listenToSession:session ] ];
                    }
                    
                }
            }
        }
    }
    @finally
    {
        if( session != nil )
        {
            @try
            {
                @synchronized( m_bluetoothSockets )
                {
                    [ m_bluetoothSockets removeObject:session ];
                }
                session = nil;
            }
            @catch(NSException *exception)
            {
                // Implement here
            }
        }
    }
    
    return array;
}

- (id) listenToSession:(EASession *)session
{
    if( session != nil )
    {
        NSInputStream *inputStream = session.inputStream;
        NSOutputStream *outputStream = session.outputStream;
        return [ m_packetParser dataToJSON:inputStream :outputStream ];

    }
    
    return nil;
}

// As above, it doesn't look like we need to do anything in 'start' or
// 'stop' at all, and they don't need to be called as far as I can
// tell.
- (void) start
{
}

- (void) stop
{

}

- (NSArray *)filterSockets:(NSArray *)sockets byType:(NSString *)type
{
    NSArray *array;
    if( type == nil || [ type isEqualToString:@"" ] ) {
        return sockets;
    }
    array = [ NSArray array ];
    for( EASession *session in sockets ) {
        if( [ session.accessory.name isEqualToString:type ] ) {
            array = [ array arrayByAddingObject:session ];
        }
    }
    return array;
}

- (NSArray *)filterSockets:(NSArray *)sockets bySerial:(NSString *)serial
{
    NSArray *array;
    if( serial == nil || [ serial isEqualToString:@"" ] ) {
        return sockets;
    }
    array = [ NSArray array ];
    for( EASession *session in sockets ) {
        if( [ session.accessory.serialNumber isEqualToString:serial ] ) {
            array = [ array arrayByAddingObject:session ];
        }
    }
    return array;
}

#pragma mark Internal
// On connection of accessory, we should open a session for the first
// matching supported protocol that is found, so that we may begin
// reading and writing from/to it.
- (void)accessoryDidConnect:(NSNotification *)notification
{
    NSBundle *bundle = [ NSBundle mainBundle ];
    NSArray *protocols = [ bundle objectForInfoDictionaryKey:
        @"UISupportedExternalAccessoryProtocols" ];
    EAAccessory *connectedAccessory = [ [ notification userInfo ]
        objectForKey:EAAccessoryKey ];
    NSString *protocolString = [ connectedAccessory.protocolStrings
        firstObjectCommonWithArray:protocols ];
    EASession *session = [ [ [ EASession alloc ]
        initWithAccessory:connectedAccessory
        forProtocol:protocolString ]
        autorelease ];
    
    if( session )
    {
        [ connectedAccessory setValue:session forKey:@"EASession" ];
        @synchronized( m_bluetoothSockets )
        {
            [ m_bluetoothSockets addObject:session ];
        }
    }
}

// On disconnection of accessory, remove the appropriate session from
// the list of sessions, if it is present.
- (void)accessoryDidDisconnect:(NSNotification *)notification
{
    EAAccessory *accessory = [[notification userInfo]
        objectForKey:EAAccessoryKey];
    EASession *session = [ accessory valueForKey:@"EASession" ];
    if( session != nil )
    {
        @synchronized( m_bluetoothSockets )
        {
            [m_bluetoothSockets removeObject:session];
        }
        session = nil;
    }
}
@end
