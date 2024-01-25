plugins {
    kotlin("kapt")
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.greenmars.distribuidor"
    compileSdk = 34

    // buildToolsVersion "33.0.2"

    defaultConfig {
        applicationId = "com.greenmars.distribuidor"
        minSdk = 16
        targetSdk = 34
        versionCode = 8
        versionName = "v1.8"
        multiDexEnabled = true
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

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

android {
    lint {
        baseline = file("lint-baseline.xml")
    }
}


dependencies {
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    val navVersion = "2.7.6"
    val activity_version = "1.6.1"
    val room_version = "2.5.0"

    implementation("com.google.maps.android:android-maps-utils:0.6.2")

    // implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    //--Firebase
    implementation(platform("com.google.firebase:firebase-bom:26.1.0"))
    implementation("com.google.firebase:firebase-messaging")

    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("com.android.support.constraint:constraint-layout:2.0.4")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.1.0")
    testImplementation("junit:junit:4.13.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    implementation("com.google.android.gms:play-services-maps:17.0.0")
    implementation("com.google.android.gms:play-services-location:17.1.0")
    implementation("com.github.nkzawa:socket.io-client:0.6.0")
    implementation("com.mcxiaoke.volley:library:1.0.19")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("com.auth0.android:jwtdecode:2.0.0")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("com.github.smarteist:autoimageslider:1.4.0")
    implementation("com.github.bumptech.glide:glide:4.11.0")
    implementation("com.airbnb.android:lottie:3.5.0")

    //NavComponent
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.3.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.3.1")


    implementation("androidx.multidex:multidex:2.0.1")
    implementation("com.google.android.material:material:1.9.0")

    //Dagger hilt
    implementation("com.google.dagger:hilt-android:2.46")
    kapt("com.google.dagger:hilt-android-compiler:2.46")


    implementation("androidx.activity:activity-ktx:$activity_version")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")



    implementation ("androidx.room:room-runtime:2.6.1")
    kapt ("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")
}