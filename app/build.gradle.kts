plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.gms.google-services") // ✅ Required for Firebase

}

android {
    namespace = "com.example.myloginapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.myloginapp"
        minSdk = 26
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        mlModelBinding = true
        viewBinding = true
    }

    //added by jlemus for TFLite
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.compose.foundation.layout)



    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)






    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")

    // Also add the dependencies for the Credential Manager libraries and specify their versions
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")


    //added
    implementation("androidx.credentials:credentials:1.2.2")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")
    implementation("com.google.android.gms:play-services-auth:21.2.0")






    implementation ("com.google.firebase:firebase-firestore-ktx")


    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))

// Firebase libraries (no versions)
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")



    //TensorF added by Jamieson
    // TensorFlow Lite core runtime
    implementation ("org.tensorflow:tensorflow-lite:2.14.0")

    // TensorFlow Lite support library for TensorBuffer, ImageProcessor, etc.
    implementation ("org.tensorflow:tensorflow-lite-support:0.4.4")

    // TensorFlow Lite metadata (helps with model labels)
    implementation ("org.tensorflow:tensorflow-lite-metadata:0.4.4")

    // Optional (GPU or NNAPI delegates)
    // implementation 'org.tensorflow:tensorflow-lite-gpu:2.14.0'

    //Using the libraries for using apis

    // Retrofit for HTTP requests
    implementation("com.squareup.retrofit2:retrofit:2.11.0")

    // Converter for JSON <-> Kotlin data classes
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // Coroutines support (for async calls)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("io.coil-kt:coil-compose:2.6.0")


    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")

    // ML Kit Translation
    implementation("com.google.mlkit:translate:17.0.1")
    implementation("com.google.android.gms:play-services-mlkit-language-id:17.0.0")

    //GoogleMaps
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.maps.android:maps-compose:4.3.0")






}