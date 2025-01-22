import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    kotlin("plugin.serialization") version "2.0.21"
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            linkerOpts.add("-lsqlite3")
        }
    }

    sourceSets {
        commonMain {
            dependencies {

                /**
                 * Compose Multiplatform
                 */
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                /**
                 * Voyager Navigation
                 */
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.screenmodel)
                implementation(libs.voyager.koin)

                /**
                 * DI
                 */
                implementation(libs.koin.core)
                // Удаляем koin.compose из commonMain
                // implementation(libs.koin.compose)

                /**
                 * Coroutines
                 */
                implementation(libs.kotlinx.coroutines.core)

                /**
                 * Ktor
                 */
                implementation(libs.ktor.core)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.kotlinx.serialization.json)

                /**
                 * SQLDelight
                 */
                implementation(libs.sqldelight.coroutines)
                implementation(libs.sqldelight.runtime)

                /**
                 * Coil
                 */
                implementation(libs.coil.compose)
                implementation(libs.coil.network.ktor)

                /**
                 * Logging
                 */
                implementation(libs.napier)

                /**
                 * Compose Navigation
                 */
                implementation(libs.androidx.navigation.compose)

                /**
                 * KMP ViewModel
                 */
                implementation(libs.androidx.lifecycle.viewmodel.compose)

                /**
                 * DI
                 */
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)

            }
        }

        androidMain {
            dependencies {

                /**
                 * Compose Preview
                 */
                implementation(compose.preview)

                /**
                 * SQLDelight Android Driver
                 */
                implementation(libs.sqldelight.android)

                /**
                 * Ktor
                 */
                implementation(libs.ktor.client.okhttp)

                /**
                 * Coroutines Android
                 */
                implementation(libs.kotlinx.coroutines.android)

                /**
                 * AndroidX Activity Compose
                 */
                implementation(libs.androidx.activity.compose)
            }
        }

        iosMain {

            dependencies {
                /**
                 * SQLDelight Native Driver
                 */
                implementation(libs.sqldelight.native)

                /**
                 * Ktor
                 */
                implementation(libs.ktor.client.darwin)
            }
        }
    }
}

android {
    namespace = "com.gleb.lemana.task"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.gleb.lemana.task"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

sqldelight {
    databases {
        create("LemenaTestAppDb") {
            packageName.set("com.gleb.lemana.task")
        }
    }
}

dependencies {

    implementation(libs.androidx.navigation.compose)
    /**
     * Compose UI Tooling
     */
    debugImplementation(compose.uiTooling)
}
