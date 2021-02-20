package com.example.kotlingradledsl.properties

object Dependency {
    const val kotlinVersion = "1.4.30"
    const val androidGradleVersion = "4.1.1"
    const val minSdk = 16
    const val compileSdk = 30
    const val targetSdk = 30
    const val buildToolVersion = "30.0.3"
    const val kspVersion = "1.4.30-1.0.0-alpha02"

    private const val moshiVersion = "1.11.0"

    object ClassPath {
        const val androidGradle = "com.android.tools.build:gradle:$androidGradleVersion"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
        const val ksp = "com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:$kspVersion"
    }

    object Kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
    }

    object AndroidX {
        const val coreKtx = "androidx.core:core-ktx:1.3.2"
        const val appComat = "androidx.appcompat:appcompat:1.2.0"

        const val material = "com.google.android.material:material:1.3.0"
        const val contstraintlayout = "androidx.constraintlayout:constraintlayout:2.0.4"
    }

    object Moshi {
        const val lib = "com.squareup.moshi:moshi:$moshiVersion"
        const val compiler = "com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion"
    }

    object Ksp {
        const val symbolProcessingApi = "com.google.devtools.ksp:symbol-processing-api:$kspVersion"
    }

    object Test {
        const val junit4 = "junit:junit:4.13.1"
    }
}