import Foundation
import Sentry

/// Objective-C entry points for Cocoa's Swift-only metrics API.
@objcMembers public final class SentryKMPMetrics: NSObject {
    public static func count(_ name: String, value: UInt64, attributes: [String: SentryAttribute]) {
        guard let nativeValue = UInt(exactly: value) else { return }
        SentrySDK.metrics.count(key: name, value: nativeValue, attributes: metricAttributes(attributes))
    }

    public static func gauge(_ name: String, value: Double, unit: String?, attributes: [String: SentryAttribute]) {
        SentrySDK.metrics.gauge(key: name, value: value, unit: unit.flatMap(SentryUnit.init(rawValue:)), attributes: metricAttributes(attributes))
    }

    public static func distribution(_ name: String, value: Double, unit: String?, attributes: [String: SentryAttribute]) {
        SentrySDK.metrics.distribution(key: name, value: value, unit: unit.flatMap(SentryUnit.init(rawValue:)), attributes: metricAttributes(attributes))
    }

    public static func configure(_ options: Options, beforeSend: ((SentryKMPMetric) -> SentryKMPMetric?)?) {
        guard let beforeSend = beforeSend else {
            options.beforeSendMetric = nil
            return
        }
        options.beforeSendMetric = { metric in
            beforeSend(SentryKMPMetric(metric))?.metric
        }
    }

    private static func metricAttributes(_ attributes: [String: SentryAttribute]) -> [String: any SentryAttributeValue] {
        attributes.compactMapValues { attribute in
            switch attribute.type {
            case "string": return attribute.value as? String
            case "boolean": return (attribute.value as? NSNumber)?.boolValue
            case "integer": return (attribute.value as? NSNumber)?.intValue
            case "double": return (attribute.value as? NSNumber)?.doubleValue
            default: return nil
            }
        }
    }
}

/// Mutable view of a native metric, retaining metadata and unsupported native attributes.
@objcMembers public final class SentryKMPMetric: NSObject {
    @nonobjc fileprivate var metric: SentryMetric

    @nonobjc fileprivate init(_ metric: SentryMetric) {
        self.metric = metric
        super.init()
    }

    public var timestamp: Double { metric.timestamp.timeIntervalSince1970 }
    public var traceId: String { metric.traceId.sentryIdString }
    public var spanId: String? { metric.spanId?.sentrySpanIdString }
    public var name: String {
        get { metric.name }
        set { metric.name = newValue }
    }
    public var unit: String? {
        get { metric.unit?.rawValue }
        set { metric.unit = newValue.flatMap(SentryUnit.init(rawValue:)) }
    }
    public var type: String {
        switch metric.value {
        case .counter: return "counter"
        case .gauge: return "gauge"
        case .distribution: return "distribution"
        @unknown default: return "unknown"
        }
    }
    public var counterValue: UInt64 {
        if case .counter(let value) = metric.value { return UInt64(value) }
        return 0
    }
    public var doubleValue: Double {
        switch metric.value {
        case .counter(let value): return Double(value)
        case .gauge(let value), .distribution(let value): return value
        @unknown default: return .nan
        }
    }
    public func setCounter(_ value: UInt64) -> Bool {
        guard let nativeValue = UInt(exactly: value) else { return false }
        metric.value = .counter(nativeValue)
        return true
    }
    public func setGauge(_ value: Double) { metric.value = .gauge(value) }
    public func setDistribution(_ value: Double) { metric.value = .distribution(value) }

    public var attributes: [String: SentryAttribute] {
        get {
            metric.attributes.compactMapValues { content in
                switch content {
                case .string(let value): return SentryAttribute(string: value)
                case .boolean(let value): return SentryAttribute(boolean: value)
                case .integer(let value): return SentryAttribute(integer: value)
                case .double(let value): return SentryAttribute(double: value)
                default: return nil
                }
            }
        }
        set {
            // Only scalar attributes are visible to Kotlin. Preserve native arrays and future types.
            for key in attributes.keys { metric.attributes.removeValue(forKey: key) }
            for (key, attribute) in newValue {
                switch attribute.type {
                case "string":
                    if let value = attribute.value as? String { metric.attributes[key] = .string(value) }
                case "boolean":
                    if let value = attribute.value as? NSNumber { metric.attributes[key] = .boolean(value.boolValue) }
                case "integer":
                    if let value = attribute.value as? NSNumber { metric.attributes[key] = .integer(value.intValue) }
                case "double":
                    if let value = attribute.value as? NSNumber { metric.attributes[key] = .double(value.doubleValue) }
                default: break
                }
            }
        }
    }
}
