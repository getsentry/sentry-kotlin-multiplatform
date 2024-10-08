// The following are snippets from the Sentry Cocoa SDK used to generate Kotlin stubs.
//
// https://github.com/getsentry/sentry-cocoa/blob/825b2e1f8aa0569f29f45b7ca2e2a72b41637660/Sources/Sentry/Public/SentryDebugImageProvider.h
//
// The MIT License (MIT)
//
// Copyright (c) 2015 Sentry
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

@class SentryDebugMeta, SentryThread;

@interface SentryDebugImageProvider : NSObject

- (NSArray<SentryDebugMeta *> *_Nonnull)getDebugImagesForThreads:(NSArray<SentryThread *> *_Nonnull)threads;

@end
