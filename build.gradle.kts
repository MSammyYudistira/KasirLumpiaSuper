import org.jetbrains.kotlin.fir.declarations.builder.buildScript

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}

//buildscript {
//    repositories {
//        google()
//        mavenCentral()
//        maven { url = uri("https://jitpack.io") }
//    }
//    dependencies {
//        classpath ("com.android.tools.build:gradle:8.0.2")
//        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
//    }
//}
//
//allprojects {
//    repositories {
//        google()
//        mavenCentral()
//        maven { url = uri("https://jitpack.io") }
//    }
//}
//
//tasks.register("clean", Delete::class) {
//    delete(rootProject.buildDir)
//}
