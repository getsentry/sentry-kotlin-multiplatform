// Private Cocoa 9.28.0 crash-monitor API. Keep declarations aligned with
// Sources/Sentry/include/SentryCrashC.h and SentryCrashMonitor.h.
#include <stdint.h>

typedef uint32_t SentryCrashMonitorType;
static const SentryCrashMonitorType SentryCrashMonitorTypeCPPException = 0x04;

SentryCrashMonitorType sentrycrash_setMonitoring(SentryCrashMonitorType monitors);
SentryCrashMonitorType sentrycrashcm_getActiveMonitors(void);
