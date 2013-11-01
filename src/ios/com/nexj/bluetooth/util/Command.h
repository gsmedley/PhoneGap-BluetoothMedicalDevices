//
//  CommandProtocol.h
//  DevicePlugin
//
//  Created by Richard Eyre on 13-01-23.
//
//

@protocol Command

@required

- (void)execute;

@end
