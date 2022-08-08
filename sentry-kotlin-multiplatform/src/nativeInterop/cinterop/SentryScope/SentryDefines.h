#import <Foundation/Foundation.h>

typedef NS_ENUM(NSUInteger, SentryLevel) {
    kSentryLevelNone = 0,
    kSentryLevelDebug = 1,
    kSentryLevelInfo = 2,
    kSentryLevelWarning = 3,
    kSentryLevelError = 4,
    kSentryLevelFatal = 5
};
