//
//  MigrationManager.h
//  Storage
//
//  Created by Helder Guilherme Farias on 10/04/17.
//  Copyright © 2017 Facebook. All rights reserved.
//

#ifndef MigrationManager_h
#define MigrationManager_h
#endif /* MigrationManager_h */

#import <FMDB/FMDB.h>

@interface MigrationManager : NSObject {
  FMDatabase *db;
}

- (id) initWithDatabase:(FMDatabase *)newDB;

- (BOOL) applyFrom:(NSInteger)fromVersion to:(NSInteger)toVersion;

@end


