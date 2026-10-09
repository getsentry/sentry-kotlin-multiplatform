// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "KotlinMultiplatformLinkedPackageDylib",
  platforms: [
    .iOS("15.0"),
    .macOS("12.0"),
    .tvOS("15.0"),
    .watchOS("15.0")
  ],
  products: [
    .library(
      name: "KotlinMultiplatformLinkedPackageDylib",
      type: .dynamic,
      targets: ["KotlinMultiplatformLinkedPackageDylib"]
    )
  ],
  dependencies: [
    .package(
      url: "https://github.com/getsentry/sentry-cocoa.git",
      exact: "9.30.1"
    )
  ],
  targets: [
    .target(
      name: "KotlinMultiplatformLinkedPackageDylib",
      dependencies: [
        .product(
          name: "Sentry",
          package: "sentry-cocoa",
          condition: .when(platforms: [.iOS, .macOS, .tvOS, .watchOS])
        )
      ]
    )
  ]
)
