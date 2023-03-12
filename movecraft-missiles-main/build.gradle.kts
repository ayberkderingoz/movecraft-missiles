import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
  id("java")
  kotlin("jvm") version "1.7.10"
  kotlin("kapt") version "1.7.10"
  kotlin("plugin.serialization") version "1.7.10"
  id("net.minecrell.plugin-yml.bukkit") version "0.5.2" // Generates plugin.yml
  id("com.github.johnrengelman.shadow") version "7.1.2"
  id("io.papermc.paperweight.userdev") version "1.3.8"
  id("xyz.jpenilla.run-paper") version "1.0.6"
}

group = "dev.peopo"
version = "1.0.0"
description = "Movecraft Missiles"
val pluginName: String = "MovecraftMissiles"
val configNames: List<String> = File("$projectDir//src//main/resources//" ).list()!!.toList()
val configPath: String = "$projectDir//run//plugins//$pluginName//"

repositories{
  mavenCentral()
  maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
  maven("https://maven.enginehub.org/repo/")
  maven("https://repo.papermc.io/repository/maven-public/")
  maven("https://jitpack.io" )
}
dependencies {
  paperDevBundle("1.17.1-R0.1-SNAPSHOT")
  implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
  implementation("com.github.mertunctuncer:bukkit-scope:1.0.0")
  compileOnly(project(":movecraft-shields"))
  compileOnly(files("../dependencies/MassiveCore.jar"))
  compileOnly(files("../dependencies/Factions.jar"))
  compileOnly(files("../dependencies/Movecraft.jar"))
  compileOnly(files("../dependencies/MovecraftWorldGuard-1.0.2.jar"))
  compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.7")
  compileOnly("me.clip:placeholderapi:2.11.2")
}

tasks {
  runServer{
    minecraftVersion("1.17.1")
  }

  shadowJar {
    relocate("com.zaxxer", "dev.peopo.depend.com.zaxxer")
    minimize()

  }
  register("reobfJarWithShadow"){
    group = "shadow"
    dependsOn(shadowJar)
    dependsOn(reobfJar)
  }
  compileJava {
    options.encoding = Charsets.UTF_8.name()
  }
  javadoc {
    options.encoding = Charsets.UTF_8.name()
  }
  processResources {
    filteringCharset = Charsets.UTF_8.name()
  }
  val cleanConfigs = register<Delete>("cleanConfigs") {
    group = "other"
    for(config in configNames) {
      File("$configPath/$config").delete()
    }
  }

  register("cleanBuildThenRun") {
    group = "run paper"
    dependsOn(cleanConfigs)
    dependsOn(clean)
    dependsOn(runServer)
  }

  register("cleanConfigThenRun") {
    group = "run paper"
    dependsOn(cleanConfigs)
    dependsOn(runServer)
  }
}
bukkit {
  name = "MovecraftMissiles"
  load = BukkitPluginDescription.PluginLoadOrder.STARTUP
  main = "dev.peopo.movecraftmissiles.MovecraftMissiles"
  apiVersion = "1.17"
  authors = listOf("Ayberk#3332","Kunefe#0031","Aki..#0001")
  depend = listOf("WorldGuard", "Movecraft", "MovecraftShields", "Factions")
  softDepend = listOf("PlaceHolderAPI")
}
