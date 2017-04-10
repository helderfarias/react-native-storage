#if __has_include("RCTBridgeModule.h")
#import "RCTBridgeModule.h"
#else
#import <React/RCTBridgeModule.h>
#endif

#import <FMDB/FMDB.h>

@interface RNStorage : NSObject <RCTBridgeModule> {
  FMDatabase *db;
}

@property (nonatomic, copy) FMDatabase *db;

@end
