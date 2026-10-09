// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "KotlinMultiplatformLinkedPackage",
  platforms: [
    .iOS("15.0"),
    .macOS("12.0"),
    .tvOS("15.0"),
    .watchOS("15.0")
  ],
  products: [
    .library(
      name: "KotlinMultiplatformLinkedPackage",
      type: .none,
      targets: ["KotlinMultiplatformLinkedPackage"]
    )
  ],
  dependencies: [
    .package(
      url: "https://github.com/getsentry/sentry-cocoa.git",
      exact: "9.30.1"
    ),
    .package(path: "subpackages/KotlinMultiplatformLinkedPackageDylib")
  ],
  targets: [
    .target(
      name: "KotlinMultiplatformLinkedPackage",
      dependencies: [
        .product(name: "KotlinMultiplatformLinkedPackageDylib", package: "KotlinMultiplatformLinkedPackageDylib")
      ]
    )
  ]
)
