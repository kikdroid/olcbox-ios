import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
    // Плагин android-library удален, так как мы собираем только iOS
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.metro)
}

kotlin {
    // Мы оставляем только Apple-таргеты и JVM для разработки
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    macosX64()
    macosArm64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(libs.compose.runtime)
            api(libs.compose.ui)
            api(libs.compose.foundation)
            api(libs.compose.resources)
            api(libs.compose.material3)

            implementation(libs.kermit)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.multiplatformSettings)
            implementation(libs.kstore)
            implementation(libs.materialKolor)
            implementation(libs.androidx.datastore.preferences)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kstore.file)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.kstore.file)
        }

        macosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.kstore.file)
        }
    }

    targets
        .withType<KotlinNativeTarget>()
        .matching { it.konanTarget.family.isAppleFamily }
        .configureEach {
            binaries {
                framework {
                    baseName = "SharedUI"
                    isStatic = true
                }
            }
        }
}
