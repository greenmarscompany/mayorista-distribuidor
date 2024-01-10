plugins {
    id("com.android.application")
}

android {
    namespace = "com.greenmars.distribuidor"
    compileSdk = 33

    // buildToolsVersion "33.0.2"

    defaultConfig {
        applicationId = "com.greenmars.distribuidor"
        minSdk = 16
        targetSdk = 33
        versionCode = 7
        versionName = "v1.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isCrunchPngs = false
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
}

apply (plugin = "com.android.application")
apply(plugin = "com.google.gms.google-services")

dependencies {
    implementation ("com.google.maps.android:android-maps-utils:0.6.2")

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    //--Firebase
    implementation(platform("com.google.firebase:firebase-bom:26.1.0"))
    implementation ("com.google.firebase:firebase-messaging")

    implementation ("androidx.appcompat:appcompat:1.2.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")
    implementation ("com.android.support.constraint:constraint-layout:2.0.4")
    implementation ("androidx.coordinatorlayout:coordinatorlayout:1.1.0")
    testImplementation ("junit:junit:4.13.1")
    androidTestImplementation ("androidx.test.ext:junit:1.1.2")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.3.0")
    implementation ("com.google.android.gms:play-services-maps:17.0.0")
    implementation ("com.google.android.gms:play-services-location:17.1.0")
    implementation ("com.google.android.material:material:1.2.1")
    implementation ("com.github.nkzawa:socket.io-client:0.6.0")
    implementation ("com.mcxiaoke.volley:library:1.0.19")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation ("com.auth0.android:jwtdecode:2.0.0")
    implementation ("com.google.android.material:material:1.2.1")
    implementation ("com.google.code.gson:gson:2.8.6")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation ("com.facebook.shimmer:shimmer:0.5.0")
    implementation ("com.github.smarteist:autoimageslider:1.4.0")
    implementation ("com.github.bumptech.glide:glide:4.11.0")
    implementation ("com.airbnb.android:lottie:3.5.0")
}