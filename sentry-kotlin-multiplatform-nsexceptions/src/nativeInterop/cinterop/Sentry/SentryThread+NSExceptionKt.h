// https://github.com/rickclephas/NSExceptionKt/blob/master/nsexception-kt-sentry/src/nativeInterop/cinterop/Sentry/SentryThread%2BNSExceptionKt.h
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
#import <SentryThread.h>

// When we create the NSNumber in Kotlin it isn't converted to a boolean,
// so we are using this wrapper function instead.
void NSExceptionKt_SentryThreadSetCrashed(SentryThread *thread) {
    thread.crashed = @(YES);
}
