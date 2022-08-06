group "com.example"
version "1.0-SNAPSHOT"

allprojects {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins {
    id("org.jetbrains.compose").version("1.2.0-alpha01-dev753")
}

