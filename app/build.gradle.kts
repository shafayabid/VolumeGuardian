plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.shafay.volumeguardian"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.shafay.volumeguardian"
        minSdk = 24
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

    flavorDimensions += "dimension"

    productFlavors {
        create("withFirebase") {
            dimension = "dimension"
            applicationIdSuffix = ".withFirebase"
            versionNameSuffix = "-withFirebase"
            if (gradle.startParameter.taskRequests.toString().contains("WithFirebase"))
            {
                apply(plugin = "com.google.gms.google-services")
                apply(plugin = "com.google.firebase.crashlytics")
            }
        }
        create("withoutFirebase") {
            dimension = "dimension"
            applicationIdSuffix = ".withoutFirebase"
            versionNameSuffix = "-withoutFirebase"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    //mediapipe
    implementation ("com.google.mediapipe:tasks-audio:0.20230731")

    //sdp
    implementation ("com.intuit.sdp:sdp-android:1.1.1")

    //Firebase Crashlytics
    "withFirebaseImplementation"(platform("com.google.firebase:firebase-bom:32.8.1"))
    "withFirebaseImplementation"("com.google.firebase:firebase-crashlytics")

    //Firebase Analytics
//    "withFirebaseImplementation"("com.google.firebase:firebase-analytics-ktx")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}