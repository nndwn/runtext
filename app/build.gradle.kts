import java.util.Properties
import kotlin.apply

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

android {
    namespace = "com.nndwn.runtext"
    compileSdk = 37

    flavorDimensions.add("distribution")
    productFlavors {
        create("playstore") {
            dimension = "distribution"
        }
        create("foss") {
            dimension = "distribution"
            applicationIdSuffix = ".foss"
            versionNameSuffix = "-foss"
        }
    }

    defaultConfig {
        applicationId = "com.nndwn.runtext"
        minSdk = 28
        targetSdk = 37
        versionCode = 3
        versionName = "0.0.3-alpha"

        val adsApiKey = "ADS_API_KEY"
        val adsApiAlt = "ADS_API_ALT"
        val email = "EMAIL"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", email, "\"${localProperties.getProperty(email) ?: ""}\"")
        buildConfigField("String", adsApiAlt, "\"${localProperties.getProperty(adsApiAlt) ?: ""}\"")
        manifestPlaceholders[adsApiKey] = localProperties.getProperty(adsApiKey) ?: ""
    }

    signingConfigs {
        create("release") {
            val keyFile = localProperties.getProperty("KEY_FILE")
            if (keyFile != null) {
                storeFile = rootProject.file(keyFile)
                storePassword = localProperties.getProperty("KEYSTORE_PASSWORD")
                keyAlias = localProperties.getProperty("KEY_ALIAS")
                keyPassword = localProperties.getProperty("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            optimization {
                enable = true
            }
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

}

dependencies {


    "playstoreImplementation"(libs.play.services.ads)
    //Compose Bom
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    //implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(libs.androidx.compose.material3.window.size.class1)

    //AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    //Navigation
    implementation(libs.androidx.navigation.compose)

    //Datastore
    implementation(libs.androidx.datastore.preferences)

    //DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    //Testing
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    debugImplementation(libs.androidx.compose.ui.tooling)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}