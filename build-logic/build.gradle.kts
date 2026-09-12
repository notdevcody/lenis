plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("com.gradleup.shadow:shadow-gradle-plugin:9.3.0")
    implementation("org.ow2.asm:asm:9.10.1")
    implementation("org.ow2.asm:asm-commons:9.10.1")
}

gradlePlugin {
    plugins {
        create("pylonBuild") {
            id = "pylon.build"
            implementationClass = "pl.tomgirl.pylon.build.PylonBuildPlugin"
        }
    }
}
