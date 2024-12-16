pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        gradlePluginPortal()
    }
    //include(":ModernUI")
    //project(":ModernUI").projectDir = file("ModernUI")
}