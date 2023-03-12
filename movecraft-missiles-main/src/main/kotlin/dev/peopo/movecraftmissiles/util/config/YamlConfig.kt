package dev.peopo.movecraftmissiles.util.config

import dev.peopo.movecraftmissiles.util.logger
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File

open class YamlConfig(private val plugin: Plugin, fileName: String) : YamlConfiguration() {
    private val file: File
    private val fileName: String

    init {
        if (fileName.endsWith(".yml")) this.fileName = fileName
        else this.fileName = "$fileName.yml"
        file = File(plugin.dataFolder, this.fileName)

        if (!file.exists()) {
            file.parentFile.mkdirs()
            this.saveDefault()
        }
        this.load()
    }

    private fun saveDefault() {
        plugin.saveResource(fileName, false)
    }

    fun load() = try {
        super.load(file)
    } catch (e: Exception) {
        logger.severe("An error has occurred while loading the config file!")
    }

    fun save() = try {
        super.save(file)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

