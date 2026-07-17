plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.doan1"

    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.doan1"
        minSdk = 24
        targetSdk = 36
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
}

dependencies {

    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:34.15.0"))

    // Firebase Realtime Database
    implementation("com.google.firebase:firebase-database")
    implementation(libs.cardview)

    // Firebase Authentication
    implementation(libs.firebase.auth)
    implementation(libs.play.services.ads)

    // Google Sign-In
    implementation(libs.play.services.auth)

    // Nếu cần Analytics thì bỏ comment dòng dưới
    // implementation("com.google.firebase:firebase-analytics")

    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    // Giao diện Material Design
    implementation(libs.material)

    // Glide để load ảnh từ mạng
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // MapTiler (osmdroid)
    implementation("org.osmdroid:osmdroid-android:6.1.18")

    // Retrofit & GSON để lấy dữ liệu từ API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // Thư viện để đọc XML (RSS)
    implementation("com.squareup.retrofit2:converter-simplexml:2.9.0")
    implementation(libs.recyclerview)

    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}