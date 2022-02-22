plugins {
    id("lib-kotlin-android-no-config")
    id("publish-android")
}

dependencies {
    api(libs.rxjava)
    api(libs.recyclerview)
    api(libs.databinding.runtime)
    compileOnly(libs.androidAnnotation)

    testImplementation(libs.mockito)
    testImplementation(libs.mockitoKotlin)
    testRuntimeOnly(libs.jupiterVintage)
    testRuntimeOnly(libs.jupiterEngine)
}