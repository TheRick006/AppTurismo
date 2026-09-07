plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
    id("androidx.navigation.safeargs")
}

android {

    namespace = "com.itanes.appturismo"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.itanes.appturismo"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }


}
configurations {
    all {
        resolutionStrategy {
            force("org.jetbrains:annotations:23.0.0")
        }
    }
}
dependencies {
    dependencies {









        // Material Design
        implementation("com.google.android.material:material:1.11.0")

        // AndroidX Core
        implementation("androidx.core:core-ktx:1.12.0")
        implementation("androidx.appcompat:appcompat:1.6.1")

        // Fragment
        implementation("androidx.fragment:fragment-ktx:1.6.2")

        // Navigation
        implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
        implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

        // Lifecycle
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
        implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

        // Coroutines
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

        // Room
        implementation("androidx.room:room-runtime:2.8.4")
        implementation("androidx.room:room-ktx:2.8.4")
        ksp("androidx.room:room-compiler:2.8.4")

        // Retrofit
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")

        // Glide
        implementation("com.github.bumptech.glide:glide:4.16.0")
        ksp("com.github.bumptech.glide:compiler:4.16.0")

        // WorkManager
        implementation("androidx.work:work-runtime-ktx:2.9.0")
    }

}
