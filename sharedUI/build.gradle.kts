import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.metro)
}

// Проверка среды
val isCi = System.getenv("CI") != null

// Поиск локальной Go-библиотеки (для Android)
val olcrtcRepoPath = providers.environmentVariable("OLCRTC_REPO").orElse(
    listOf(
        layout.projectDirectory.dir("../olcrtc-original").asFile.absolutePath,
        layout.projectDirectory.dir("olcrtc-original").asFile.absolutePath,
        rootProject.layout.projectDirectory.dir("../olcrtc-original").asFile.absolutePath
    ).find { file(it).exists() } ?: "NOT_FOUND"
)

val olcrtcRepoDir = file(olcrtcRepoPath.get())
val hasOlcrtc = olcrtcRepoDir.exists() && olcrtcRepoDir.isDirectory

val olcrtcAndroidAar = layout.buildDirectory.file("generated/olcrtc/olcrtc.aar")
val olcrtcAndroidAarFile = olcrtcAndroidAar.get().asFile

// Задача сборки AAR (только если есть исходники)
val buildOlcrtcAndroidAar by tasks.registering(Exec::class) {
    group = "build"
    onlyIf { hasOlcrtc }
    if (hasOlcrtc) {
        inputs.dir(olcrtcRepoDir.resolve("mobile"))
        outputs.file(olcrtcAndroidAar)
        workingDir = olcrtcRepoDir
        commandLine("gomobile", "bind", "-target=android", "-androidapi", "21", "-o", olcrtcAndroidAarFile.absolutePath, "./mobile")
    }
}

val olcrtcAndroidAarDependency = if (hasOlcrtc) files(olcrtcAndroidAarFile).builtBy(buildOlcrtcAndroidAar) else files()

kotlin {
    android {
        namespace = "org.olcbox.app.sharedui"
        compileSdk = 36
        minSdk = 23
        compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
    }

    jvm { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }

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
            implementation(libs.kotlinx.serialization.json)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activityCompose)
            implementation(libs.ktor.client.okhttp)
            if (hasOlcrtc) { implementation(olcrtcAndroidAarDependency) }
        }

        iosMain.dependencies { implementation(libs.ktor.client.darwin) }
        macosMain.dependencies { implementation(libs.ktor.client.darwin) }
    }

    targets.withType<KotlinNativeTarget>().matching { it.konanTarget.family.isAppleFamily }.configureEach {
        binaries.framework {
            baseName = "SharedUI"
            isStatic = true
        }
    }
}
