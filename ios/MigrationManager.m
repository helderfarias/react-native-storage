//
//  MigrationManager.m
//  Storage
//
//  Created by Helder Guilherme Farias on 10/04/17.
//  Copyright © 2017 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <React/RCTLog.h>
#import "MigrationManager.h"

@implementation MigrationManager

- (id) initWithDatabase:(FMDatabase *)newDB
{
  self = [super init];
  if (self) {
    db = newDB;
  }
  return self;
}


- (BOOL) applyFrom:(NSInteger)fromVersion to:(NSInteger)toVersion
{
  NSString * resourcePath = [[NSBundle mainBundle] resourcePath];

  NSString * documentsPath = [resourcePath stringByAppendingPathComponent:@"assets/migrations"];

  NSArray *files = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:documentsPath error:NULL];

  NSArray *migrations = [self filter:files from:fromVersion to:toVersion];

  RCTLogInfo(@"migrations %lu", [migrations count] - 1);
    
  if ([migrations count] <= 0) {
    RCTLogInfo(@"without migrations files");
    return FALSE;
  }

  @try {
    [db beginTransaction];

    BOOL status = FALSE;

    for (NSDictionary *migration in migrations) {
      NSString *content = [migration valueForKey:@"content"];

      [db executeUpdate:content];
        
      if (!status && ![@"not an error" isEqualToString:db.lastErrorMessage]) {
        break;
      }
    }

    if (!status && ![@"not an error" isEqualToString:db.lastErrorMessage]) {
      [db rollback];
      return FALSE;
    }

    RCTLogInfo(@"applied migrations %lu", [migrations count] - 1);
      
    [db commit];
    return TRUE;
  } @catch (NSException *exception) {
    [db rollback];
    return FALSE;
  }
}

- (NSArray *) filter:(NSArray *)assets from:(NSInteger)fromVersion to:(NSInteger)toVersion
{
  NSMutableArray *filters = [[NSMutableArray alloc] initWithCapacity:assets.count];

  for (NSString *fileName in assets) {
    NSString *version = [self getVersion:fileName];
    NSString *content = [self readContent:fileName];

    if ([version integerValue] > fromVersion &&
        [version integerValue] <= toVersion &&
        [content length] > 0) {
      for (NSString *data in [content componentsSeparatedByString:@";"]) {
        if ([data length] > 0) {
          [filters addObject:@{ @"file": fileName, @"content": data}];
        }
      }
    }
  }

  return filters;
}

- (NSString *) readContent:(NSString *)fileName
{
  NSString * resourcePath = [[NSBundle mainBundle] resourcePath];
  NSString * path = [resourcePath stringByAppendingPathComponent:[NSString stringWithFormat:@"assets/migrations/%@", fileName]];

  NSString* content = [NSString stringWithContentsOfFile:path encoding:NSUTF8StringEncoding error:NULL];
  if (content == nil) {
    return @"";
  }

  return content;
}

- (NSString *) getVersion:(NSString *)name
{
  NSInteger s = [self indexOf:name text:@"."];
  NSString *base = [name substringWithRange:NSMakeRange(0, s > 0 ? s : name.length)];
  return base;
}

- (NSInteger) indexOf:(NSString *)src text:(NSString *)text
{
  NSRange range = [src rangeOfString:text];

  if ( range.length > 0 ) {
    return range.location;
  } else {
    return -1;
  }
}

@end
