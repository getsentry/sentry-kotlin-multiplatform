// SentryCrash interface - expose the uncaughtExceptionHandler
// Based on: https://github.com/getsentry/sentry-cocoa/blob/main/Sources/Sentry/include/SentryCrash.h

typedef enum {
    SentryCrashMonitorTypeMachException = 0x01,
    SentryCrashMonitorTypeSignal = 0x02,
    SentryCrashMonitorTypeCPPException = 0x04,
    SentryCrashMonitorTypeNSException = 0x08,
    SentryCrashMonitorTypeSystem = 0x40,
    SentryCrashMonitorTypeApplicationState = 0x80,
} SentryCrashMonitorType;

@interface SentryCrash : NSObject

@property (nonatomic, assign, nullable) NSUncaughtExceptionHandler *uncaughtExceptionHandler;
@property (nonatomic, readwrite, assign) SentryCrashMonitorType monitoring;

@end
