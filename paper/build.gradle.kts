plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.yash.paper"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        targetSdk = 36
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
}

dependencies {
    compileOnly(project(":sdk"))

    // Host-provided libs → compileOnly
    val roomVersion = "2.6.0"

    compileOnly(libs.androidx.core.ktx)
    compileOnly(libs.androidx.lifecycle.runtime.ktx)
    compileOnly(libs.androidx.activity.compose)
    compileOnly(platform(libs.androidx.compose.bom))
    compileOnly(libs.androidx.ui)
    compileOnly(libs.androidx.ui.graphics)
    compileOnly(libs.androidx.ui.tooling.preview)
    compileOnly(libs.androidx.material3)

    // Navigation
    compileOnly("androidx.navigation:navigation-compose:2.7.5")

    // Icons
    compileOnly("androidx.compose.material:material-icons-extended:1.6.1")

    // Pager
    compileOnly("com.google.accompanist:accompanist-pager:0.34.0")
    compileOnly("com.google.accompanist:accompanist-pager-indicators:0.34.0")

    // Gson
    compileOnly("com.google.code.gson:gson:2.11.0")

    // Charts
    compileOnly("com.himanshoe:charty:2.1.0-beta03.4")

    // Network
    compileOnly("com.squareup.okhttp3:okhttp:4.12.0")
    compileOnly("com.squareup.retrofit2:retrofit:2.9.0")
    compileOnly("com.squareup.retrofit2:converter-gson:2.9.0")

    // DB (Room)
    compileOnly("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    compileOnly("androidx.room:room-ktx:$roomVersion")

    // Lifecycle
    compileOnly("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    compileOnly("androidx.activity:activity-compose:1.9.0")
}

