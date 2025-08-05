rootProject.name = "ktor-books"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven(url = "https://maven.d1s.dev/releases")
    }
}
