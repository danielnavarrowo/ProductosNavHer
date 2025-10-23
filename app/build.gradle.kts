@file:Suppress("UnstableApiUsage")

import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("plugin.serialization")
    alias(libs.plugins.compose.compiler)
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_20)
    }
}

android {
    namespace = "com.navher.myapplication"
    compileSdk = 36

    defaultConfig {

        val localProperties = Properties().apply {
            load(rootProject.file("local.properties").inputStream())
        }

        buildConfigField("String", "SUPABASE_URL", "\"${localProperties.getProperty("SUPABASE_URL")}\"")
        buildConfigField("String", "SUPABASE_KEY", "\"${localProperties.getProperty("SUPABASE_KEY")}\"")

        applicationId = "com.navher.productos"
        minSdk = 28
        targetSdk = 36
        versionCode = 4
        versionName = "2.0.0"

        vectorDrawables {
            useSupportLibrary = true
        }

        androidResources {
            noCompress.add("tflite")
            localeFilters.addAll(listOf("en", "es"))
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_20
        targetCompatibility = JavaVersion.VERSION_20
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = false
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    implementation("androidx.compose.material3:material3-android:1.5.0-alpha07")
    implementation(libs.androidx.datastore.core.android)

    // MLKit Barcode Scanning (bundled)
    implementation("com.google.mlkit:barcode-scanning:17.3.0")

    // CameraX
    implementation("androidx.camera:camera-core:1.5.1")
    implementation("androidx.camera:camera-camera2:1.5.1")
    implementation("androidx.camera:camera-lifecycle:1.5.1")
    implementation("androidx.camera:camera-view:1.5.1")

    // Accompanist Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.37.3")

    implementation(libs.androidx.foundation.android)
    implementation(libs.play.services.base)
    implementation(libs.play.services.tflite.java)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.compose.foundation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.android)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)


    implementation(platform(libs.supabase.bom))
    implementation(libs.postgrest.kt)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.cio)
    implementation (libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
    implementation("io.github.jan-tennert.supabase:auth-kt:3.2.5")

    // Añadir dependencia de desugaring
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}
