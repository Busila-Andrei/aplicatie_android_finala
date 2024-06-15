plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.aplicatie_android_finala"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.aplicatie_android_finala"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.firebase.inappmessaging)
    implementation(libs.androidx.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.retrofit)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Room dependencies
    implementation (libs.room.runtime)
    annotationProcessor (libs.androidx.room.compiler)
    implementation (libs.androidx.room.ktx)
    implementation(libs.androidx.appcompat.v120)
    implementation(libs.androidx.core.ktx)
    implementation("com.google.code.gson:gson:2.10.1")
}