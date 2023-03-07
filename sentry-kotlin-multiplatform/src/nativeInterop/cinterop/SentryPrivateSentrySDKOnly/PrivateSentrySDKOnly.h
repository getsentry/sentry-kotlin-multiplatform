#import <Foundation/Foundation.h>

@interface PrivateSentrySDKOnly : NSObject

/**
 * Override SDK information.
 */
+ (void)setSdkName:(NSString *)sdkName andVersionString:(NSString *)versionString;

/**
 * Override SDK information.
 */
+ (void)setSdkName:(NSString *)sdkName;

/**
 * Retrieves the SDK name
 */
+ (NSString *)getSdkName;

/**
 * Retrieves the SDK version string
 */
+ (NSString *)getSdkVersionString;

@end
