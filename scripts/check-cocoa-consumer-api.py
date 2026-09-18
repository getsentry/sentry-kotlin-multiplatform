#!/usr/bin/env python3
"""Compile preserved Cocoa imports from published KMP artifacts on macOS.

Run with JDK 17 and Xcode configured:
  python3 scripts/check-cocoa-consumer-api.py

Publishes to an isolated temporary Maven repository, then compiles an independent
Kotlin consumer for iosSimulatorArm64. This checks source/klib compatibility,
not final app linkage or compatibility with older Kotlin compiler versions.
The temporary project and artifacts are retained for inspection.
"""
import os
from pathlib import Path
import re
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[1]
MODULE = 'sentry-kotlin-multiplatform'


def main():
    config = (ROOT / 'buildSrc/src/main/java/Config.kt').read_text()
    kotlin_version = re.search(r'val kotlinVersion = "([^"]+)"', config).group(1)
    properties = (ROOT / 'gradle.properties').read_text()
    sdk_version = re.search(r'^versionName\s*=\s*(.+)$', properties, re.MULTILINE).group(1).strip()
    workspace = Path(tempfile.mkdtemp(prefix='kmp-cocoa-consumer-'))
    repository = workspace / 'maven'
    consumer = workspace / 'consumer'
    source = consumer / 'src/iosSimulatorArm64Main/kotlin'
    source.mkdir(parents=True)
    print(f'Consumer artifacts: {workspace}', flush=True)
    env = dict(os.environ, CI='false', SENTRY_SKIP_UPLOAD='1')
    gradle = str(ROOT / 'gradlew')
    subprocess.run([
        gradle, f'-Dmaven.repo.local={repository}',
        f':{MODULE}:publishKotlinMultiplatformPublicationToMavenLocal',
        f':{MODULE}:publishIosSimulatorArm64PublicationToMavenLocal',
    ], cwd=ROOT, env=env, check=True)
    (consumer / 'settings.gradle.kts').write_text('''
pluginManagement { repositories { gradlePluginPortal(); mavenCentral(); google() } }
rootProject.name = "cocoa-api-consumer"
''')
    (consumer / 'build.gradle.kts').write_text(f'''
plugins {{ kotlin("multiplatform") version "{kotlin_version}" }}
repositories {{ maven {{ url = uri("{repository.as_uri()}") }}; mavenCentral() }}
kotlin {{
    iosSimulatorArm64()
    sourceSets.iosSimulatorArm64Main.dependencies {{
        implementation("io.sentry:{MODULE}:{sdk_version}")
    }}
}}
''')
    (consumer / 'gradle.properties').write_text('kotlin.native.ignoreDisabledTargets=true\n')
    (source / 'CocoaApiConsumer.kt').write_text('''
@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

import cocoapods.Sentry.SentryEvent as CocoaEvent
import cocoapods.Sentry.SentryOptions as CocoaOptions
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryEvent

fun nativeOptions(): CocoaOptions = CocoaOptions()
fun wrapNativeEvent(event: CocoaEvent): SentryEvent = SentryEvent(event)
fun initializeWithNativeOptions() {
    Sentry.initWithPlatformOptions { _: CocoaOptions -> }
}
''')
    subprocess.run([gradle, '-p', str(consumer), 'compileKotlinIosSimulatorArm64'], cwd=ROOT, env=env, check=True)
    print('PASS: published KMP artifacts preserve cocoapods.Sentry imports and native API types.', flush=True)


if __name__ == '__main__':
    main()
