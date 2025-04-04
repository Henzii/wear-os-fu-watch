plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.apollographql.apollo3").version("3.8.5")
}

android {
    namespace = "com.henzisoft.puttmaster9000"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.henzisoft.puttmaster9000"
        minSdk = 30
        targetSdk = 34
        versionCode = 1004
        versionName = "Halinalle"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    }
}

dependencies {

    implementation(libs.play.services.wearable)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.compose.material)
    implementation(libs.compose.foundation)
    implementation(libs.wear.tooling.preview)
    implementation(libs.activity.compose)
    implementation(libs.core.splashscreen)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    implementation(libs.apollo.runtime)
    implementation(libs.apollo.normalized.cache)
    // implementation(libs.apollo.coroutines.support)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.viewmodel)
    implementation(libs.compose.navigation)
}

apollo {
    service("service") {
        packageName.set("com.henzisoft.puttmaster9000")
    }
}