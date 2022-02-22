plugins {
    id("lib-kotlin-android-no-config")
    id("lib-kotlin-robolectric")
    id("publish-android")
}

dependencies {
    api(libs.rxjava)
    implementation(libs.rxjavaAndroid)
}