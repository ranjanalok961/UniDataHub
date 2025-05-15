import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    kotlin("plugin.serialization") version "2.0.0"
    alias(libs.plugins.google.gms.google.services)

    id("kotlin-parcelize")
}

android {
    namespace = "com.assignmentwaala.unidatahub"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.assignmentwaala.unidatahub"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    val localProperties = Properties()
    val localPropertiesFile = File(rootDir, "local.properties")
    if (localPropertiesFile.exists() && localPropertiesFile.isFile) {
        localPropertiesFile.inputStream().use {
            localProperties.load(it)
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField("String", "CLOUDINARY_NAME",  localProperties.getProperty("CLOUDINARY_NAME"))
            buildConfigField("String", "CLOUDINARY_API_KEY",  localProperties.getProperty("CLOUDINARY_API_KEY"))
            buildConfigField("String", "CLOUDINARY_API_SECRET",  localProperties.getProperty("CLOUDINARY_API_SECRET"))
        }

        debug {
            buildConfigField("String", "CLOUDINARY_NAME",  localProperties.getProperty("CLOUDINARY_NAME"))
            buildConfigField("String", "CLOUDINARY_API_KEY",  localProperties.getProperty("CLOUDINARY_API_KEY"))
            buildConfigField("String", "CLOUDINARY_API_SECRET",  localProperties.getProperty("CLOUDINARY_API_SECRET"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.espresso.core)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // this ia for hilt dependency injection
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    kapt("androidx.hilt:hilt-compiler:1.2.0")



    // this is for coil
    implementation("io.coil-kt:coil-compose:2.6.0")


    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.0-beta06")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Cloudinary
    implementation("com.cloudinary:cloudinary-android:3.0.2")

    // Datastore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
//    //this is for pager
//    implementation("com.google.accompanist:accompanist-pager:0.28.0")
//    implementation("com.google.accompanist:accompanist-pager-indicators:0.28.0")
}