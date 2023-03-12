pluginManagement {
  repositories {
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public/")
  }
}
include(":movecraft-shields")
project(":movecraft-shields").projectDir = File("../dependencies/MovecraftShields")
rootProject.name = "movecraft-missiles"
