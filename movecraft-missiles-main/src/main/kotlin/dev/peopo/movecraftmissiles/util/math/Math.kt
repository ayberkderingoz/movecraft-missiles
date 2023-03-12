package dev.peopo.movecraftmissiles.util.math

import net.countercraft.movecraft.utils.HitBox
import org.bukkit.Location
import org.bukkit.block.Block


fun Location.toVector3() = Vector3(this.x, this.y, this.z)
fun Location.toBlockVector3() = Vector3(this.blockX, this.blockY, this.blockZ)
fun Location.toTargetVector(): ConcurrentBlockVector {
    val vector = ConcurrentBlockVector()
    vector.x = blockX
    vector.y = blockY
    vector.z = blockZ
    return vector
}

fun Location.hasBlocksNearby(range: Int): Boolean {
    for (x in blockX - range..blockX + range) {
        for (y in blockY - range..blockY + range + 1) {
            for (z in blockZ - range..blockZ + range) {
                if (world.getBlockAt(x, y, z).isEmpty) continue
                return true
            }
        }

    }
    return false
}

fun isLocationInShip(location: Location, hitBox: HitBox): Boolean {
    return location.blockX in hitBox.minX..hitBox.maxX && location.blockY in hitBox.minY..hitBox.maxY && location.blockZ in hitBox.minZ..hitBox.maxZ
}

fun getBlocksInsideOfSphere(centerBlock: Location?, radius: Int, hollow: Boolean): List<Block> {
    if (centerBlock == null) {
        return ArrayList()
    }
    val circleBlocks: MutableList<Block> = ArrayList()
    val bx = centerBlock.blockX
    val by = centerBlock.blockY
    val bz = centerBlock.blockZ
    for (x in bx - radius..bx + radius) {
        for (y in by - radius..by + radius) {
            for (z in bz - radius..bz + radius) {
                val distance = ((bx - x) * (bx - x) + (bz - z) * (bz - z) + (by - y) * (by - y)).toDouble()
                if (distance < radius * radius && !(hollow && distance < ((radius - 1) * (radius - 1)))) {
                    val l = Location(centerBlock.world, x.toDouble(), y.toDouble(), z.toDouble())
                    circleBlocks.add(l.block)
                }
            }
        }
    }
    return circleBlocks
}
