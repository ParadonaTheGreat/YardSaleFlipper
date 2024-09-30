plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.yardsaleflipper"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.yardsaleflipper"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    dependencies {
        val camerax_version = "1.4.0-beta02"
        implementation("androidx.camera:camera-core:${camerax_version}")
        implementation("androidx.camera:camera-camera2:${camerax_version}")
        implementation("androidx.camera:camera-lifecycle:${camerax_version}")
        implementation("androidx.camera:camera-video:${camerax_version}")
        implementation("androidx.camera:camera-view:${camerax_version}")
        implementation("androidx.camera:camera-mlkit-vision:${camerax_version}")
        implementation("androidx.camera:camera-extensions:${camerax_version}")

        implementation("com.squareup.picasso:picasso:2.8")


    }
}