#import "RNStorage.h"
#import <React/RCTLog.h>
#import <sqlite3.h>
#import "MigrationManager.h"

@implementation RNStorage

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

- (void) applyMigrations:(NSInteger)version database:(FMDatabase *)newDB
{
  NSInteger oldVersion = 0;
  NSUserDefaults *preferences = [NSUserDefaults standardUserDefaults];
  if ([preferences objectForKey:@"DATABASE_MIGRATION_CURRENT_VERSION"] != nil) {
    oldVersion = [[preferences objectForKey:@"DATABASE_MIGRATION_CURRENT_VERSION"] integerValue];
  }

  RCTLogInfo(@"current migration version: %ld", oldVersion);

  if (oldVersion >= version) {
    RCTLogInfo(@"no migration");
    return;
  }

    @try {
    MigrationManager *manager = [[MigrationManager alloc] initWithDatabase:newDB];

    BOOL applied = [manager applyFrom:oldVersion to:version];

    if (applied) {
      [preferences setInteger:version forKey:@"DATABASE_MIGRATION_CURRENT_VERSION"];
    }
  } @catch(NSException *exception) {
    RCTLogError(@"error: %@", exception);
  }
}

RCT_EXPORT_MODULE()

@synthesize db;

RCT_EXPORT_METHOD(open:(NSString *)nameArgs
                  version:(NSInteger)versionArgs
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
  @synchronized (self) {
    if (db != nil) {
      return;
    }

    @try {
      NSURL *documentsDir = [[[NSFileManager defaultManager] URLsForDirectory:NSDocumentDirectory
                                                                    inDomains:NSUserDomainMask] lastObject];

      NSString *path = [documentsDir.path stringByAppendingPathComponent:nameArgs];

      db = [FMDatabase databaseWithPath:path];

      BOOL ok = [db openWithFlags:SQLITE_OPEN_READWRITE | SQLITE_OPEN_CREATE];
      if (ok) {
        [self applyMigrations:versionArgs database:db];
        resolve(@"true");
        return;
      }

      reject(@"error", db.lastErrorMessage, db.lastError);
    } @catch (NSError *exception) {
      reject(@"error", @"cannot update", exception);
    }
  }
}

RCT_EXPORT_METHOD(query:(NSString *)sql
                  args:(NSDictionary *) args
                  tx:(BOOL) tx
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
  @synchronized (self) {
    if (tx && ![db inTransaction]) {
      reject(@"error", @"without transaction started", nil);
      return;
    }

    if (!tx && [db inTransaction]) {
      reject(@"error", @"transacion opened, execute commit or rollback before using operation.", nil);
      return;
    }

    @try {
      NSMutableArray *rows = [NSMutableArray arrayWithCapacity:0];

      FMResultSet *rs = [db executeQuery:sql withParameterDictionary:args];
      while ([rs next]) {
        [rows addObject:[rs resultDictionary]];
      }

      resolve(rows);
    } @catch(NSError *exception) {
      reject(@"error", @"cannot update", exception);
    }
  }
}

RCT_EXPORT_METHOD(update:(NSString *)table
                  values:(NSDictionary *)values
                  whereClausule:(NSString *)whereClausule
                  whereArgs:(NSArray *)whereArgs
                  tx:(BOOL) tx
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
  @synchronized (self) {
    if (tx && ![db inTransaction]) {
      reject(@"error", @"without transaction started", nil);
      return;
    }

    if (!tx && [db inTransaction]) {
      reject(@"error", @"transacion opened, execute commit or rollback before using operation.", nil);
      return;
    }

    @try {
      NSMutableString *argsFields = [[NSMutableString alloc] init];
      NSMutableArray *argsValues = [[NSMutableArray alloc] init];

      int count = 0;
      for (NSString *key in values) {
        if (++count < values.allValues.count) {
          [argsFields appendString:[NSString stringWithFormat:@"%@ = ?, ", key]];
        } else {
          [argsFields appendString:[NSString stringWithFormat:@"%@ = ? ", key]];
        }

        [argsValues addObject:values[key]];
      }

      [argsValues addObjectsFromArray:whereArgs];

      NSString *sql = [NSString stringWithFormat:@"UPDATE %@ SET %@ WHERE %@", table, argsFields, whereClausule];

      BOOL ok = [db executeUpdate:sql withArgumentsInArray:argsValues];
      if (ok) {
        resolve(@"true");
        return;
      }

      reject(@"error", db.lastErrorMessage, db.lastError);
    } @catch (NSError *exception) {
      reject(@"error", @"cannot update", exception);
    }
  }
}

RCT_EXPORT_METHOD(insert:(NSString *)table
                  args:(NSDictionary *) values
                  tx:(BOOL) tx
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
  @synchronized (self) {
    if (tx && ![db inTransaction]) {
      reject(@"error", @"without transaction started", nil);
      return;
    }

    if (!tx && [db inTransaction]) {
      reject(@"error", @"transacion opened, execute commit or rollback before using operation.", nil);
      return;
    }

    @try {
      NSMutableString *columnsNames = [[NSMutableString alloc] init];
      NSMutableString *columnsValues = [[NSMutableString alloc] init];
      NSMutableArray *argsValues = [[NSMutableArray alloc] init];

      int count = 0;
      for (NSString *key in values) {
        if (++count < values.allValues.count) {
          [columnsNames appendString:[NSString stringWithFormat:@"%@,", key]];
          [columnsValues appendString:@"?,"];
        } else {
          [columnsNames appendString:[NSString stringWithFormat:@"%@", key]];
          [columnsValues appendString:@"?"];
        }

        [argsValues addObject:values[key]];
      }

      NSString *sql = [NSString stringWithFormat:@"INSERT INTO %@ (%@) VALUES (%@)", table, columnsNames, columnsValues];

      BOOL ok = [db executeUpdate:sql withArgumentsInArray:argsValues];
      if (ok) {
        resolve(@"true");
        return;
      }

      reject(@"error", db.lastErrorMessage, db.lastError);
    } @catch (NSError *exception) {
      reject(@"error", @"cannot insert", exception);
    }
  }
}

RCT_EXPORT_METHOD(delete:(NSString *)table
                  whereClausule:(NSString *)whereClausule
                  whereArgs:(NSArray *)values
                  tx:(BOOL) tx
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
  @synchronized (self) {
    if (tx && ![db inTransaction]) {
      reject(@"error", @"without transaction started", nil);
      return;
    }

    if (!tx && [db inTransaction]) {
      reject(@"error", @"transacion opened, execute commit or rollback before using operation.", nil);
      return;
    }

    @try {
      NSString *sql = [NSString stringWithFormat:@"DELETE FROM %@", table];
      if (whereClausule != nil && values != nil) {
        sql = [NSString stringWithFormat:@"DELETE FROM %@ WHERE %@", table, whereClausule];
      }

      BOOL ok = FALSE;
      if (whereClausule != nil && values != nil) {
        ok = [db executeUpdate:sql withArgumentsInArray:values];
      } else {
        ok = [db executeUpdate:sql];
      }

      if (ok) {
        resolve(@"true");
        return;
      }

      reject(@"error", db.lastErrorMessage, db.lastError);
    } @catch (NSError *exception) {
      reject(@"error", @"cannot update", exception);
    }
  }
}

RCT_EXPORT_METHOD(execSQL:(NSString *)sql
                  args:(NSDictionary *) args
                  tx:(BOOL) tx
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
  @synchronized (self) {
    if (tx && ![db inTransaction]) {
      reject(@"error", @"without transaction started", nil);
      return;
    }

    if (!tx && [db inTransaction]) {
      reject(@"error", @"transacion opened, execute commit or rollback before using operation.", nil);
      return;
    }

    @try {
      BOOL ok = [db executeUpdate:sql withParameterDictionary:args];
      if (ok) {
        resolve(@"true");
        return;
      }

      reject(@"error", db.lastErrorMessage, db.lastError);
    } @catch (NSError *exception) {
      reject(@"error", @"cannot update", exception);
    }
  }
}

RCT_EXPORT_METHOD(startTransaction:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
  @synchronized (self) {
    if ([db inTransaction]) {
      reject(@"error", @"transacion active.", nil);
      return;
    }

    @try {
      BOOL ok = [db beginTransaction];

      if (ok) {
        resolve(@"true");
      } else {
        resolve(@"false");
      }
    } @catch (NSError *exception) {
      reject(@"error", @"cannot start transaction", exception);
    }
  }
}

RCT_EXPORT_METHOD(commit:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
  @synchronized (self) {
    if (![db inTransaction]) {
      reject(@"error", @"without transaction started", nil);
      return;
    }

    @try {
      BOOL ok = [db commit];

      if (ok) {
        resolve(@"true");
      } else {
        resolve(@"false");
      }
    } @catch (NSError *exception) {
      reject(@"error", @"cannot commit transaction", exception);
    }
  }
}

RCT_EXPORT_METHOD(rollback:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
  @synchronized (self) {
    if (![db inTransaction]) {
      reject(@"error", @"without transaction started", nil);
      return;
    }

    @try {
      BOOL ok = [db rollback];

      if (ok) {
        resolve(@"true");
      } else {
        resolve(@"false");
      }
    } @catch (NSError *exception) {
      reject(@"error", @"cannot rollback transaction", exception);
    }
  }
}

@end
