// The following are snippets from the Sentry Cocoa SDK used to generate Kotlin stubs.
//
// https://github.com/getsentry/sentry-cocoa/blob/9.28.0/Sources/Sentry/include/SentryCrashStackCursor.h
//
// Copyright (c) 2016 Karl Stenerud. All rights reserved.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall remain in place
// in this source code.

#include <stdbool.h>
#include <stdint.h>

#define SentryCrashSC_CONTEXT_SIZE 100

typedef struct {
    uintptr_t address;
    const char *imageName;
    uintptr_t imageAddress;
    const char *symbolName;
    uintptr_t symbolAddress;
} SentryCrashStackEntry;

typedef struct SentryCrashStackCursor {
    SentryCrashStackEntry stackEntry;
    struct {
        int currentDepth;
        bool hasGivenUp;
    } state;
    void (*resetCursor)(struct SentryCrashStackCursor *);
    bool (*advanceCursor)(struct SentryCrashStackCursor *);
    void *context[SentryCrashSC_CONTEXT_SIZE];
} SentryCrashStackCursor;
