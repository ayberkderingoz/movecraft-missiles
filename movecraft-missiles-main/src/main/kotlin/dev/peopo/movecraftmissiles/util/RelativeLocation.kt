package dev.peopo.movecraftmissiles.util

import dev.peopo.movecraftmissiles.util.math.Vector3
import net.countercraft.movecraft.Rotation
import net.countercraft.movecraft.events.CraftRotateEvent
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.BlockFace

fun getRelativeLocation(launcherBlock: Block, tipBlock: Block, center: Location): Vector3<Double> {
    val locationLauncher = launcherBlock.location
    val x = locationLauncher.x - center.x
    val y = locationLauncher.y - center.y
    val z = locationLauncher.z - center.z
    return when (getBlockFace(launcherBlock, tipBlock)) {
        BlockFace.NORTH -> Vector3(z, y, -x)
        BlockFace.SOUTH -> Vector3(z, y, x)
        BlockFace.EAST -> Vector3(x, y, z)
        BlockFace.WEST -> Vector3(-x, y, z)
        else -> Vector3(x, y, z)
    }
}

private fun getBlockFace(block: Block, tipBlock: Block): BlockFace {
    return when {
        block.getRelative(BlockFace.NORTH).location == tipBlock.location -> BlockFace.NORTH
        block.getRelative(BlockFace.SOUTH).location == tipBlock.location -> BlockFace.SOUTH
        block.getRelative(BlockFace.EAST).location == tipBlock.location -> BlockFace.EAST
        block.getRelative(BlockFace.WEST).location == tipBlock.location -> BlockFace.WEST
        else -> BlockFace.SELF
    }
}

fun getRotatedLocation(event: CraftRotateEvent, location: Location): Location {
    val relativeLocationX = location.x - event.originPoint.x
    val relativeLocationY = location.y - event.originPoint.y
    val relativeLocationZ = location.z - event.originPoint.z
    return when (event.rotation) {
        Rotation.CLOCKWISE -> {
            Location(
                event.craft.w,
                event.originPoint.x - relativeLocationZ,
                event.originPoint.y + relativeLocationY,
                event.originPoint.z + relativeLocationX
            )
        }

        Rotation.ANTICLOCKWISE -> {
            Location(
                event.craft.w,
                event.originPoint.x + relativeLocationZ,
                event.originPoint.y + relativeLocationY,
                event.originPoint.z - relativeLocationX
            )
        }

        else -> {
            location
        }
    }

}

fun getRotatedRelativeVector3(event: CraftRotateEvent, relativeLocation: Vector3<Double>): Vector3<Double> {
    return when (event.rotation) {
        Rotation.CLOCKWISE -> Vector3(-relativeLocation.z, relativeLocation.y, relativeLocation.x)
        Rotation.ANTICLOCKWISE -> Vector3(relativeLocation.z, relativeLocation.y, -relativeLocation.x)
        else -> relativeLocation
    }

}
