NS_ASSUME_NONNULL_BEGIN

NS_SWIFT_NAME(User)
@interface SentryUser : NSObject

/**
 * Optional: Id of the user
 */
@property (atomic, copy) NSString *userId;

/**
 * Optional: Email of the user
 */
@property (atomic, copy) NSString *_Nullable email;

/**
 * Optional: Username
 */
@property (atomic, copy) NSString *_Nullable username;

/**
 * Optional: IP Address
 */
@property (atomic, copy) NSString *_Nullable ipAddress;

/**
 * Optional: Additional data
 */
@property (atomic, strong) NSDictionary<NSString *, id> *_Nullable data;

@end

NS_ASSUME_NONNULL_END
