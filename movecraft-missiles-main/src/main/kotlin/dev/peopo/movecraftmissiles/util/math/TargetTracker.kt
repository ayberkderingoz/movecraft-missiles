package dev.peopo.movecraftmissiles.util.math

import net.countercraft.movecraft.craft.Craft
import org.bukkit.Location
import kotlin.math.sqrt

fun Velocity.normalize(): Velocity {
    val magnitute = sqrt((x * x) + (y * y) + (z * z))
    return Velocity(x / magnitute, y / magnitute, z / magnitute)
}

fun getShipCenter(targetShip: Craft): Location {
    val hitbox = targetShip.hitBox
    return Location(
        targetShip.world,
        ((hitbox.maxX + hitbox.minX) / 2).toDouble(),
        ((hitbox.maxY + hitbox.minY) / 2).toDouble(),
        ((hitbox.maxZ + hitbox.minZ) / 2).toDouble()
    )
}