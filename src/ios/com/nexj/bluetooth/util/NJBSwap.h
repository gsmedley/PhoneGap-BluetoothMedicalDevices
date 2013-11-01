//
//  NJBSwap.h
//  DevicePlugin
//
//  Created by Caitlin Potter on 2/21/13.
//
//

#import <CoreFoundation/CoreFoundation.h>

//
// Routines to convert from remote endianness to host
//
// NJInt16FromBigEndian
// NJInt32FromBigEndian
// NJInt16FromLittleEndian
// NJInt32FromLittleEndian
//
static inline uint16_t
NJInt16FromBigEndian( uint16_t u16 )
{
    return CFSwapInt16BigToHost( u16 );
}

static inline uint32_t
NJInt32FromBigEndian( uint32_t u32 )
{
    return CFSwapInt32BigToHost( u32 );
}

static inline uint16_t
NJInt16FromLittleEndian( uint16_t u16 )
{
    return CFSwapInt16LittleToHost( u16 );
}

static inline uint32_t
NJInt32FromLittleEndian( uint32_t u32 )
{
    return CFSwapInt32LittleToHost( u32 );
}

//
// Routines to convert from host endianness to other
//
// NJInt16ToBigEndian
// NJInt32ToBigEndian
// NJInt16ToLittleEndian
// NJInt32ToLittleEndian
//
static inline uint16_t
NJInt16ToBigEndian( uint16_t u16 )
{
    return CFSwapInt16HostToBig( u16 );
}

static inline uint32_t
NJInt32ToBigEndian( uint32_t u16 )
{
    return CFSwapInt32HostToBig( u16 );
}

static inline uint16_t
NJInt16ToLittleEndian( uint16_t u16 )
{
    return CFSwapInt16HostToLittle( u16 );
}

static inline uint32_t
NJInt32ToLittleEndian( uint32_t u32 )
{
    return CFSwapInt32HostToLittle( u32 );
}