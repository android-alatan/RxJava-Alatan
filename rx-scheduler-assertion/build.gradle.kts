plugins {
    id("lib-kotlin-android-no-config")
    id("publish-android")
}

dependencies {
    api(libs.rxjava)
    api(project(":rx-scheduler-api"))
}