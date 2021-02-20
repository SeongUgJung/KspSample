import com.example.kotlingradledsl.properties.Dependency

plugins {
    id("custom-lib")
    id("lib-android-ksp")
    kotlin("kapt")
}

android {
}

dependencies {
    api(Dependency.Moshi.lib)
    kapt(Dependency.Moshi.compiler)
    ksp(project(com.example.kotlingradledsl.properties.KspProject.moshiAdapterFactory))
}