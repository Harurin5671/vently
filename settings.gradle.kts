pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Vently"
include(":app")
include(":core:ui")
include(":data")
include(":shared:domain")
include(":shared:sunat")
include(":shared:data")
include(":core:domain")
include(":core:common")
include(":core:designsystem")
include(":feature:onboarding")
include(":core:network")
include(":feature:auth")
include(":feature:home")
include(":feature:settings")
include(":feature:stock")
include(":feature:history")
