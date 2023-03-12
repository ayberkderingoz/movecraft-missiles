package dev.peopo.movecraftmissiles.util

import dev.peopo.movecraftmissiles.util.config.YamlConfig
import dev.peopo.movecraftmissiles.util.config.messages.MessageConfig
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger


val plugin: Plugin by lazy { Bukkit.getPluginManager().getPlugin("MovecraftMissiles")!! }

val javaPlugin: JavaPlugin by lazy { plugin as JavaPlugin }

val pluginManager: PluginManager by lazy { Bukkit.getPluginManager() }

val config: YamlConfig by lazy { YamlConfig(plugin, "config.yml") }

val messages: MessageConfig by lazy { MessageConfig(plugin, "messages.yml") }

val logger: Logger by lazy { plugin.logger }

val version: String by lazy { plugin.version }

val Plugin.version: String by lazy {
    val input = plugin.getResource("plugin.yml")?.reader()
    val config = YamlConfiguration()
    config.load(input!!)
    return@lazy config.getString("version")!!
}

val Plugin.dependencies: List<String> by lazy {
    val input = plugin.getResource("plugin.yml")?.reader()
    val config = YamlConfiguration()
    config.load(input!!)
    return@lazy config.getStringList("depend")
}

val Plugin.softDependencies: List<String> by lazy {
    val input = plugin.getResource("plugin.yml")?.reader()
    val config = YamlConfiguration()
    config.load(input!!)
    return@lazy config.getStringList("softdepend")
}

val Plugin.apiVersion: String by lazy {
    val input = plugin.getResource("plugin.yml")?.reader()
    val config = YamlConfiguration()
    config.load(input!!)
    return@lazy config.getString("api-version")!!
}

fun Plugin.hasDependencies(): Boolean {
    for (dependency in this.dependencies) if (pluginManager.getPlugin(dependency) == null) return false
    return true
}

fun Plugin.disable() = pluginManager.disablePlugin(this)