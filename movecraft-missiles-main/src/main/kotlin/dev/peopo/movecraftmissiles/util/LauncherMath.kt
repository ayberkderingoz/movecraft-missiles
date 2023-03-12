package dev.peopo.movecraftmissiles.util

import dev.peopo.movecraftmissiles.util.config.getStructure
import dev.peopo.movecraftmissiles.util.math.Velocity
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.Dropper
import org.bukkit.block.Sign
import org.bukkit.event.block.SignChangeEvent


fun getBlockBehind(world: World, block: Block, blockFace: BlockFace, distance: Double): Block {
    return when (blockFace) {
        BlockFace.NORTH -> {
            world.getBlockAt(block.location.add(0.0, 0.0, -distance))
        }

        BlockFace.SOUTH -> {
            world.getBlockAt(block.location.add(0.0, 0.0, distance))
        }

        BlockFace.EAST -> {
            world.getBlockAt(block.location.add(distance, 0.0, 0.0))
        }

        BlockFace.WEST -> {
            world.getBlockAt(block.location.add(-distance, 0.0, 0.0))
        }

        else -> block
    }
}

/**
 * Gets the tip of the launcher
 * Using for missile spawn
 * */
fun getDispenserOnTip(block: Block, distance: Int): Block? {
    return when {
        block.getRelative(BlockFace.NORTH, distance).state.type == Material.DISPENSER -> {
            block.getRelative(BlockFace.NORTH, distance)
        }

        block.getRelative(BlockFace.SOUTH, distance).state.type == Material.DISPENSER -> {
            block.getRelative(BlockFace.SOUTH, distance)
        }

        block.getRelative(BlockFace.EAST, distance).state.type == Material.DISPENSER -> {
            block.getRelative(BlockFace.EAST, distance)
        }

        block.getRelative(BlockFace.WEST, distance).state.type == Material.DISPENSER -> {
            block.getRelative(BlockFace.WEST, distance)
        }

        else -> null
    }
}

fun getDropper(block: Block): Dropper? {
    return when {
        block.getRelative(BlockFace.NORTH, 1).state.type == Material.DROPPER -> {
            block.getRelative(BlockFace.NORTH, 1).state as Dropper
        }

        block.getRelative(BlockFace.SOUTH, 1).state.type == Material.DROPPER -> {
            block.getRelative(BlockFace.SOUTH, 1).state as Dropper
        }

        block.getRelative(BlockFace.EAST, 1).state.type == Material.DROPPER -> {
            block.getRelative(BlockFace.EAST, 1).state as Dropper
        }

        block.getRelative(BlockFace.WEST, 1).state.type == Material.DROPPER -> {
            block.getRelative(BlockFace.WEST, 1).state as Dropper
        }

        else -> null
    }
}

/**
 * Checks if the launcher is available in the config file
 * If not returns false
 * */
fun isLauncher(event: SignChangeEvent): Boolean {
    val materialList: List<Material>
    try {
        materialList = getStructure("launchers.${event.getLine(1)}.structure")!!
    } catch (e: NullPointerException) {

        return false
    }
    return when {
        event.block.getRelative(BlockFace.NORTH, 1).state.type == Material.DROPPER &&
                event.block.getRelative(BlockFace.NORTH, materialList.size + 2).state.type == Material.DISPENSER -> {
            for (i in 2..materialList.size + 1) {
                if (event.block.getRelative(BlockFace.NORTH, i).state.type != materialList[i - 2]) {

                    return false
                }
            }
            return true
        }

        event.block.getRelative(BlockFace.SOUTH, 1).state.type == Material.DROPPER &&
                event.block.getRelative(BlockFace.SOUTH, materialList.size + 2).state.type == Material.DISPENSER -> {
            for (i in 2..materialList.size + 1) {
                if (event.block.getRelative(BlockFace.SOUTH, i).state.type != materialList[i - 2]) {
                    return false
                }
            }
            return true
        }

        event.block.getRelative(BlockFace.EAST, 1).state.type == Material.DROPPER &&
                event.block.getRelative(BlockFace.EAST, materialList.size + 2).state.type == Material.DISPENSER -> {
            for (i in 2..materialList.size + 1) {
                if (event.block.getRelative(BlockFace.EAST, i).state.type != materialList[i - 2]) {

                    return false
                }
            }
            return true
        }

        event.block.getRelative(BlockFace.WEST, 1).state.type == Material.DROPPER &&
                event.block.getRelative(BlockFace.WEST, materialList.size + 2).state.type == Material.DISPENSER -> {
            for (i in 2..materialList.size + 1) {
                if (event.block.getRelative(BlockFace.WEST, i).state.type != materialList[i - 2]) {

                    return false
                }
            }
            return true
        }

        else -> false
    }
}

fun isLauncher(sign: Sign): Boolean {
    val materialList: List<Material>
    try {
        materialList = getStructure("launchers.${sign.getLine(1)}.structure")!!
    } catch (e: NullPointerException) {
        println("MovecraftMissiles: Structure not found check for misspelling in config\n" + e.message + sign.getLine(1))
        return false
    }
    return when {
        sign.block.getRelative(BlockFace.NORTH, 1).state.type == Material.DROPPER &&
                sign.block.getRelative(BlockFace.NORTH, materialList.size + 2).state.type == Material.DISPENSER -> {
            for (i in 2..materialList.size + 1) {
                if (sign.block.getRelative(BlockFace.NORTH, i).state.type != materialList[i - 2]) {

                    return false
                }
            }
            return true
        }

        sign.block.getRelative(BlockFace.SOUTH, 1).state.type == Material.DROPPER &&
                sign.block.getRelative(BlockFace.SOUTH, materialList.size + 2).state.type == Material.DISPENSER -> {
            for (i in 2..materialList.size + 1) {
                if (sign.block.getRelative(BlockFace.SOUTH, i).state.type != materialList[i - 2]) {
                    return false
                }
            }
            return true
        }

        sign.block.getRelative(BlockFace.EAST, 1).state.type == Material.DROPPER &&
                sign.block.getRelative(BlockFace.EAST, materialList.size + 2).state.type == Material.DISPENSER -> {
            for (i in 2..materialList.size + 1) {
                if (sign.block.getRelative(BlockFace.EAST, i).state.type != materialList[i - 2]) {

                    return false
                }
            }
            return true
        }

        sign.block.getRelative(BlockFace.WEST, 1).state.type == Material.DROPPER &&
                sign.block.getRelative(BlockFace.WEST, materialList.size + 2).state.type == Material.DISPENSER -> {
            for (i in 2..materialList.size + 1) {
                if (sign.block.getRelative(BlockFace.WEST, i).state.type != materialList[i - 2]) {

                    return false
                }
            }
            return true
        }

        else -> false
    }
}

fun getMissileSpawnLocation(block: Block, distance: Int): Pair<Location, Velocity>? {
    return when {
        block.getRelative(BlockFace.NORTH, distance).state.type == Material.DISPENSER -> {
            Pair(
                block.getRelative(BlockFace.NORTH, distance + 2).location,
                Velocity(0.0, 0.0, -1.0)
            )
        }

        block.getRelative(BlockFace.SOUTH, distance).state.type == Material.DISPENSER -> {
            Pair(
                block.getRelative(BlockFace.SOUTH, distance + 2).location,
                Velocity(0.0, 0.0, 1.0)
            )
        }

        block.getRelative(BlockFace.EAST, distance).state.type == Material.DISPENSER -> {
            Pair(
                block.getRelative(BlockFace.EAST, distance + 2).location,
                Velocity(1.0, 0.0, 0.0)
            )
        }

        block.getRelative(BlockFace.WEST, distance).state.type == Material.DISPENSER -> {
            Pair(
                block.getRelative(BlockFace.WEST, distance + 2).location,
                Velocity(-1.0, 0.0, 0.0)
            )
        }

        else -> null
    }
}


fun isBlockAttachedToLauncherOrComputerSign(block: Block): Boolean{
    if (block.getRelative(BlockFace.WEST,1).state is Sign){
        val sign = block.getRelative(BlockFace.WEST,1).state as Sign
        if (sign.getLine(0) == "Launcher" || sign.getLine(0) == ChatColor.GOLD.toString() + ChatColor.BOLD +"Fire Control"){
            return true
        }
    }
    if (block.getRelative(BlockFace.EAST,1).state is Sign){
        val sign = block.getRelative(BlockFace.EAST,1).state as Sign
        if (sign.getLine(0) == "Launcher" || sign.getLine(0) == ChatColor.GOLD.toString() + ChatColor.BOLD +"Fire Control"){
            return true
        }
    }
    if (block.getRelative(BlockFace.SOUTH,1).state is Sign){
        val sign = block.getRelative(BlockFace.SOUTH,1).state as Sign
        if (sign.getLine(0) == "Launcher" || sign.getLine(0) == ChatColor.GOLD.toString() + ChatColor.BOLD +"Fire Control"){
            return true
        }
    }
    if (block.getRelative(BlockFace.NORTH,1).state is Sign){
        val sign = block.getRelative(BlockFace.NORTH,1).state as Sign
        if (sign.getLine(0) == "Launcher" || sign.getLine(0) == ChatColor.GOLD.toString() + ChatColor.BOLD +"Fire Control"){
            return true
        }
    }
    return false
}



