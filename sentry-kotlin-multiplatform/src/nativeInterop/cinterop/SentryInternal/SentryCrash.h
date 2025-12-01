// SentryCrash interface - expose the uncaughtExceptionHandler
// Based on: https://github.com/getsentry/sentry-cocoa/blob/main/Sources/Sentry/include/SentryCrash.h

@interface SentryCrash : NSObject

@property (nonatomic, assign, nullable) NSUncaughtExceptionHandler *uncaughtExceptionHandler;

@end
