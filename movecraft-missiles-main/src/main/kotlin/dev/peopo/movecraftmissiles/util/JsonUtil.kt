import com.google.gson.Gson
import dev.peopo.movecraftmissiles.computer.Computer
import dev.peopo.movecraftmissiles.computer.Computer.Companion.computerList
import dev.peopo.movecraftmissiles.computer.ComputerJSON
import dev.peopo.movecraftmissiles.launcher.Launcher
import dev.peopo.movecraftmissiles.launcher.Launcher.Companion.launcherList
import dev.peopo.movecraftmissiles.launcher.LauncherJSON
import dev.peopo.movecraftmissiles.util.config
import dev.peopo.movecraftmissiles.util.config.getItemStack
import dev.peopo.movecraftmissiles.util.logger
import dev.peopo.movecraftmissiles.util.math.toVector3
import dev.peopo.movecraftmissiles.util.plugin
import org.bukkit.Bukkit
import org.bukkit.Location
import java.io.File


fun saveComputers() {
    try {
        var jsonString = "["
        var i = 0
        val gson = Gson()
        val file = File(plugin.dataFolder.absolutePath + "/computers.json")
        file.createNewFile()
        file.writeText("[")
        computerList.forEach {
            i++
            val computerJSON = ComputerJSON(it.id, it.location.world.name, it.location.toVector3())
            if (computerList.size != i) file.appendText(gson.toJson(computerJSON) + ",\n")
            else file.appendText(gson.toJson(computerJSON))
        }
        file.appendText("]")
        logger.info("Computers saved successfully")
    } catch (e: Exception) {
        logger.info("Failed to save computers\n Exception:$e")
    }
}

fun loadComputers() {
    try {
        val gson = Gson()
        val file = File(plugin.dataFolder.absolutePath + "/computers.json")
        file.createNewFile()
        val computers = gson.fromJson(file.readText(), Array<ComputerJSON>::class.java)
        computers.forEach {
            createComputerObject(it)
        }
        logger.info("Computers loaded successfully")
    } catch (e: Exception) {
        logger.info("Failed to load computers\n Exception:$e")
    }
}

fun createComputerObject(computerJSON: ComputerJSON) {
    val computer = Computer(
        config.getInt("computer_logic.max_number_of_launchers"),
        computerJSON.id,
        Location(
            Bukkit.getWorld(computerJSON.world),
            computerJSON.location.x,
            computerJSON.location.y,
            computerJSON.location.z
        )
    )
    computerList.add(computer)
}

fun saveLaunchers() {
    try {
        var jsonString = "["
        var i = 0
        val gson = Gson()
        val file = File(plugin.dataFolder.absolutePath + "/launchers.json")
        file.createNewFile()
        file.writeText("[")
        launcherList.forEach {
            i++
            val launcherJSON = LauncherJSON(it.computerID, it.type, it.relativeLocation)
            if (launcherList.size != i) file.appendText(gson.toJson(launcherJSON) + ",\n")
            else file.appendText(gson.toJson(launcherJSON))
        }
        file.appendText("]")
        logger.info("Launchers saved successfully")
    } catch (e: Exception) {
        logger.info("Failed to save launchers\n Exception:$e")
    }
}

fun loadLaunchers() {
    try {
        val gson = Gson()
        val file = File(plugin.dataFolder.absolutePath + "/launchers.json")
        val fileContent = file.readText()
        val launchers = gson.fromJson(fileContent, Array<LauncherJSON>::class.java)
        launchers.forEach {
            createLauncherObject(it)
        }
        logger.info("Launchers loaded successfully")
    } catch (e: Exception) {
        logger.warning("Failed to load launchers\n Exception:$e")
    }
}

fun createLauncherObject(launcherJSON: LauncherJSON) {
    val launcherTypes = plugin.config.getConfigurationSection("launchers")!!.getKeys(false)
    if (!launcherTypes.contains(launcherJSON.launcherType)) {
        logger.warning("Launcher type ${launcherJSON.launcherType} not found in config")
    }

    val reloadTime = plugin.config.getLong("launchers.${launcherJSON.launcherType}.reload_time")
    val homing = plugin.config.getBoolean("launchers.${launcherJSON.launcherType}.is_homing")
    val ammo = getItemStack("launchers.${launcherJSON.launcherType}.ammo_type")
    launcherList.add(
        Launcher(
            launcherJSON.computerId,
            launcherJSON.relativeLocation,
            launcherJSON.launcherType,
            reloadTime,
            ammo
        )
    )
}

