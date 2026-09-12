pluginManagement {
    includeBuild("build-logic")
    repositories {
        maven("https://maven.fabricmc.net/")
        mavenLocal()
        gradlePluginPortal()
    }
}

rootProject.name = "pylon"

include(":runs:fabric", ":runs:vanilla")
