plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    kotlin("kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.projectwork_1"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.projectwork_1"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        //Для экспорта схемы в room db
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf("room.schemaLocation" to "$projectDir/schemas")
            }
        }
    }

    flavorDimensions += "version"

    productFlavors {
        create("free") {
            dimension = "version"
            applicationIdSuffix = ".free"
            versionNameSuffix = "-free"
        }

        create("paid") {
            dimension = "version"
            applicationIdSuffix = ".paid"
            versionNameSuffix = "-paid"
        }
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.firebase.config)
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.airbnb.android:lottie:6.6.6")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Добавляем зависимости retrofit и логгера через модуль
    implementation(project(":domain_retrofit"))

    //Внедрение Dagger2
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)

    //Внедрение Room для RxJava через модуль
    implementation(project(":domain_room"))

    //для activityViewModels и viewModels()
    implementation("androidx.fragment:fragment-ktx:1.6.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")


    //Swipe refresh layout dependency
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    
    //Внедрения для RxJava
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")

    //Внедрение для Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.8.0"))
    implementation("com.google.firebase:firebase-analytics")
}