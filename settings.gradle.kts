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
        // Убираем фильтры и лишние пути, так как мы больше не ищем локальные AAR
        google()
        mavenCentral()
    }
}

// ВАЖНО: Мы полностью исключаем ":androidApp", чтобы Gradle 
// даже не пытался проверять его зависимости и наличие Go-кода.
include(":sharedUI")
include(":desktopApp")
