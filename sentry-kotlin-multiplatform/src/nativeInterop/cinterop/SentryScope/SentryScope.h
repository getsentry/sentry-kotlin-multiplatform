#import <Foundation/Foundation.h>
#import "SentryUser.h"
#import "SentryDefines.h"

@interface SentryScope : NSObject

@property (atomic, strong) SentryUser *_Nullable userObject;

@property (atomic, strong) NSMutableDictionary<NSString *, NSString *> *tagDictionary;

@property (atomic, strong) NSMutableDictionary<NSString *, id> *extraDictionary;

@property (atomic, strong)
    NSMutableDictionary<NSString *, NSDictionary<NSString *, id> *> *contextDictionary;

@property (atomic) enum SentryLevel levelEnum;

@end
