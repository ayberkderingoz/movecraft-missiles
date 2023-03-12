package dev.peopo.movecraftmissiles.worldguard

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldguard.WorldGuard
import org.bukkit.Location
import org.bukkit.block.Block

fun isLocationInCustomFlag(location: Location, blockList: List<Block>): Boolean {
    val customFlag = CustomFlag()
    val container = WorldGuard.getInstance().platform.regionContainer
    val regionManager = container.get(BukkitAdapter.adapt(location.world))
    blockList.forEach { location ->
        val blockVector3: BlockVector3 = BlockVector3.at(location.x, location.y, location.z)
        regionManager?.getApplicableRegions(blockVector3)?.forEach {
            it.flags?.let { flag ->
                if (flag.keys.toString() == "[" + customFlag.NO_EXPLOSION.toString() + "]" && flag.values.toString() == "[ALLOW]") {
                    return true
                }
            }

        }
    }
    return false
}