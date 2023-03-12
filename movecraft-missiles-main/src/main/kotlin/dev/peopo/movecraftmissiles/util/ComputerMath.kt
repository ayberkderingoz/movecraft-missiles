package dev.peopo.movecraftmissiles.util

import dev.peopo.movecraftmissiles.computer.Computer
import dev.peopo.movecraftmissiles.computer.Computer.Companion.computerList
import dev.peopo.movecraftmissiles.craft.MissileCraft
import dev.peopo.movecraftmissiles.launcher.Launcher
import dev.peopo.movecraftmissiles.util.math.isLocationInShip
import net.countercraft.movecraft.CruiseDirection
import net.countercraft.movecraft.craft.Craft
import net.countercraft.movecraft.events.CraftPilotEvent
import net.countercraft.movecraft.utils.HitBox
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.Sign

val limitCache = mutableMapOf<String, MutableMap<String, Int>>()

fun fillLimitCache() {
    config.getConfigurationSection("launcher_limits")?.getValues(false)?.forEach {
        val map = mutableMapOf<String, Int>()
        config.getConfigurationSection("launcher_limits.${it.key}")?.getValues(true)?.forEach {
            map[it.key] = it.value as Int
        }
        limitCache[it.key] = map
    }
}

fun getLimitsCache(): Map<String, MutableMap<String, Int>> {
    if (limitCache.isEmpty()) {
        fillLimitCache()
    }
    return limitCache.toMap()
}

fun getLauncherCountOfCraft(craft: MissileCraft): Map<String, Int> {
    val map = mutableMapOf<String, Int>()
    craft.computers.forEach { computer ->
        computer.linkedLaunchers.forEach { launcher ->
            if (map.containsKey(launcher.type)) {
                map[launcher.type] = map[launcher.type]!! + 1
            } else {
                map[launcher.type] = 1
            }
        }
    }
    return map.toMap()
}

fun hasRoom(limits: Map<String, Int>, counts: Map<String, Int>, launcherType: String, craftType: String): Boolean {
    val limit = limits[launcherType] ?: run {
        logger.warning("No limit found for launcher type $launcherType in craft $craftType")
        return false
    }

    val count = counts[launcherType] ?: 0
    return count < limit
}

fun updateComputerLocation(
    location: Location,
    direction: CruiseDirection,
    newHitBox: HitBox,
    oldHitBox: HitBox,
): Location {
    val newLocation = location.clone()
    when (direction) {
        CruiseDirection.NORTH -> {
            newLocation.z -= newHitBox.maxZ - oldHitBox.maxZ
        }

        CruiseDirection.SOUTH -> {
            newLocation.z += newHitBox.maxZ - oldHitBox.maxZ
        }

        CruiseDirection.EAST -> {
            newLocation.x += newHitBox.maxX - oldHitBox.maxX
        }

        CruiseDirection.WEST -> {
            newLocation.x -= newHitBox.maxX - oldHitBox.maxX
        }

        CruiseDirection.UP -> {
            newLocation.y += newHitBox.maxY - oldHitBox.maxY
        }

        CruiseDirection.DOWN -> {
            newLocation.y -= newHitBox.maxY - oldHitBox.maxY
        }

        CruiseDirection.NONE -> {
            return location
        }
    }
    return newLocation
}

fun updateComputerLocation(location: Location, newHitBox: HitBox, oldHitBox: HitBox): Location {
    val newLocation = location.clone()
    newLocation.x += newHitBox.maxX - oldHitBox.maxX
    newLocation.y += newHitBox.maxY - oldHitBox.maxY
    newLocation.z += newHitBox.maxZ - oldHitBox.maxZ
    return newLocation
}

fun isComputerInList(sign: Sign): Boolean {
    computerList.forEach {
        if (it.id == sign.getLine(3)) {
            return true
        }
    }
    return false
}

fun addLauncherToComputer(computer: Computer, launcher: Launcher): Boolean {
    val craftType = computer.craft?.movecraftCraft?.type?.craftName?.lowercase()
    val launcherLimits = getLimitsCache()[craftType] ?: mutableMapOf()
    val launcherCount = getLauncherCountOfCraft(computer.craft!!)

    return if (hasRoom(launcherLimits, launcherCount, launcher.type, craftType!!)) {
        computer.linkedLaunchers.add(launcher)
        launcher.computer = computer
        true
    } else {
        false
    }
}

fun waitHitboxThenInitComputersCraft(e: CraftPilotEvent) {
    var craft: Craft = e.craft
    /*getShipList().forEach{
        if(it == e.craft){
            craft = it
        }
    }*/
    if (craft.hitBox.isEmpty) {
        Bukkit.getScheduler().runTaskLater(plugin, Runnable { waitHitboxThenInitComputersCraft(e) }, 1)
    } else if (!craft.hitBox.isEmpty) {
        computerList.forEach {
            val missileCraft = MissileCraft(craft, craft.type.toString())
            if (isLocationInShip(it.location, craft.hitBox)) {
                it.craft = missileCraft
            }
        }
    }
}
