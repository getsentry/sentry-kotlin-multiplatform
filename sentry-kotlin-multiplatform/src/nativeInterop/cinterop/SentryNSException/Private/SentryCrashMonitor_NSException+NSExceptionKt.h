// https://github.com/rickclephas/NSExceptionKt/blob/master/nsexception-kt-sentry/src/nativeInterop/cinterop/Sentry/Private/SentryCrashMonitor_NSException%2BNSExceptionKt.h
//
// Copyright (c) 2022 Rick Clephas
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.

#import <Foundation/Foundation.h>
#import <Private/SentryCrashMonitor_NSException.h>
#import <Private/SentryCrashStackCursor.h>

// Similar to how Sentry converts stacktraces from NSExceptions
// https://github.com/getsentry/sentry-cocoa/blob/167de8bea5a0effef3aaa5c99c540088de30b361/Sources/SentryCrash/Recording/Monitors/SentryCrashMonitor_NSException.m#L60
SentryCrashStackCursor NSExceptionKt_SentryCrashStackCursorFromNSException(NSException *exception) {
    NSArray *addresses = [exception callStackReturnAddresses];
    NSUInteger numFrames = addresses.count;
    uintptr_t *callstack = malloc(numFrames * sizeof(*callstack));
    assert(callstack != NULL);
    for (NSUInteger i = 0; i < numFrames; i++) {
        callstack[i] = (uintptr_t)[addresses[i] unsignedLongLongValue];
    }
    SentryCrashStackCursor cursor;
    sentrycrashsc_initWithBacktrace(&cursor, callstack, (int)numFrames, 0);
    return cursor;
}
