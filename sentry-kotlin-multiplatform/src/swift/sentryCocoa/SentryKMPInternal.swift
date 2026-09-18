import Foundation
@_spi(Private) import Sentry

/// Objective-C entry points for the Swift-only hybrid SDK API.
@objcMembers public final class SentryKMPInternal: NSObject {
    public static func setSdkName(_ name: String, version: String) {
        SentrySDK.internal.sdk.setName(name, version: version)
    }

    public static var sdkName: String { SentrySDK.internal.sdk.name }
    public static var sdkVersion: String { SentrySDK.internal.sdk.versionString }

    @_spi(Private) public static func storeEnvelope(_ envelope: SentryEnvelope) {
        SentrySDK.internal.envelope.store(envelope)
    }

    public static func debugImages(forFrames frames: [Frame]) -> [DebugMeta] {
        var seen = Set<UInt64>()
        let addresses = frames.compactMap { frame -> UInt64? in
            guard let address = frame.imageAddress else { return nil }
            let hex = address.hasPrefix("0x") ? String(address.dropFirst(2)) : address
            guard let value = UInt64(hex, radix: 16), seen.insert(value).inserted else { return nil }
            return value
        }
        return SentrySDK.internal.debug.images(forAddresses: addresses.sorted(by: >))
    }
}
