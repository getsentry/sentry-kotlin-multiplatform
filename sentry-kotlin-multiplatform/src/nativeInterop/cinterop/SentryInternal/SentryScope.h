#import <Foundation/Foundation.h>

@class SentryUser;

typedef NS_ENUM(NSUInteger, SentryLevel) {
kSentryLevelNone = 0,
        kSentryLevelDebug = 1,
        kSentryLevelInfo = 2,
        kSentryLevelWarning = 3,
        kSentryLevelError = 4,
        kSentryLevelFatal = 5
};

@interface SentryScope : NSObject

@property (atomic, strong) SentryUser *_Nullable userObject;

@property (atomic, strong) NSMutableDictionary<NSString *, NSString *> *tagDictionary;

@property (atomic, strong) NSMutableDictionary<NSString *, id> *extraDictionary;

@property (atomic, strong)
    NSMutableDictionary<NSString *, NSDictionary<NSString *, id> *> *contextDictionary;

@property (atomic) enum SentryLevel levelEnum;

@end
