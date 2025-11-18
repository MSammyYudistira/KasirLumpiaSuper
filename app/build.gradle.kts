import org.gradle.kotlin.dsl.implementation

    plugins {
        alias(libs.plugins.android.application)
        alias(libs.plugins.kotlin.android)
        alias(libs.plugins.kotlin.compose)
        alias(libs.plugins.google.gms.google.services)
    }

    android {
        namespace = "com.example.kasirlumpiasuper"
        compileSdk = 35

        defaultConfig {
            applicationId = "com.example.kasirlumpiasuper"
            minSdk = 26
            targetSdk = 35
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
            viewBinding = true
        }

//        composeOptions {
//            kotlinCompilerExtensionVersion = "1.5.15"
//        }

        packagingOptions {
            resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    dependencies {

        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.activity.compose)
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.androidx.ui)
        implementation(libs.androidx.ui.graphics)
        implementation(libs.androidx.ui.tooling.preview)
        implementation(libs.androidx.material3)
        implementation(libs.firebase.firestore)
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
        androidTestImplementation(platform(libs.androidx.compose.bom))
        androidTestImplementation(libs.androidx.ui.test.junit4)
        debugImplementation(libs.androidx.ui.tooling)
        debugImplementation(libs.androidx.ui.test.manifest)

        implementation("androidx.navigation:navigation-compose:2.9.6")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.2")


        // Firebase
        implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
        implementation("com.google.firebase:firebase-auth-ktx")
        implementation("com.google.firebase:firebase-firestore-ktx")
        implementation("com.google.firebase:firebase-functions-ktx")
        implementation("com.google.firebase:firebase-storage-ktx:")

        implementation(libs.escpos.thermalprinter.android)

        implementation("androidx.datastore:datastore-preferences:1.1.1")
        implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

        implementation ("com.midtrans:uikit:2.3.0-SANDBOX")
        implementation ("com.midtrans:uikit:2.3.0")

        // Retrofit
        implementation("com.squareup.retrofit2:retrofit:2.11.0")
        implementation("com.squareup.retrofit2:converter-gson:2.11.0")

        // OkHttp logging (optional)
        implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

        //Insert Images
        implementation("io.coil-kt:coil-compose:2.6.0")

        //Chart
        implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")





    }