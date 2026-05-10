rootProject.name = "Multiplatform-App"

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

// Оставляем только те модули, которые не зависят от Android-библиотек Go
include(":sharedUI")
include(":desktopApp")
