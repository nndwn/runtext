import java.util.Properties
import javax.xml.parsers.DocumentBuilderFactory
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

fun getAppNameFromStrings(): String {
    val stringsFile = File(projectDir, "src/main/res/values/strings.xml")
    if (!stringsFile.exists()) return "App"

    val doc = DocumentBuilderFactory
        .newInstance()
        .newDocumentBuilder()
        .parse(stringsFile)
    val nodeList = doc.getElementsByTagName("string")

    for (i in 0 until nodeList.length) {
        val node = nodeList.item(i)
        if (node.attributes?.getNamedItem("name")?.nodeValue == "app_name") {
            return node.textContent ?: "App"
        }
    }
    return "App"
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
            versionNameSuffix = "-foss"
        }
    }

    defaultConfig {
        applicationId = "com.nndwn.runtext"
        minSdk = 28
        targetSdk = 37
        versionCode = 9
        versionName = "1.0.8-beta"

        val adsApiKey = "ADS_API_KEY"
        val adsApiAlt = "ADS_API_ALT"
        val tipMe = "PURCHASE_ID_1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", adsApiAlt, "\"${localProperties.getProperty(adsApiAlt) ?: ""}\"")
        buildConfigField("String", tipMe, "\"${localProperties.getProperty(tipMe) ?: ""}\"")
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
            val releaseConfig = signingConfigs.getByName("release")
            signingConfig = if (releaseConfig.storeFile?.exists() == true) {
                releaseConfig
            } else {
                signingConfigs.getByName("debug")
            }
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

androidComponents {
    onVariants(selector().withFlavor("distribution" to "foss")) { variant ->
        variant.outputs.forEach { output ->
            val appName = getAppNameFromStrings().replace(" ", "-")
            val vName = output.versionName.get()
            output.outputFileName.set("$appName-v$vName-foss-${variant.buildType}.apk")
        }
    }
}

dependencies {

    "playstoreImplementation"(libs.play.services.ads)
    "playstoreImplementation"(libs.app.update.ktx)
    "playstoreImplementation"(libs.billing.ktx)


    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material.icons.extended)
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
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.junit)
    debugImplementation(libs.androidx.compose.ui.tooling)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}