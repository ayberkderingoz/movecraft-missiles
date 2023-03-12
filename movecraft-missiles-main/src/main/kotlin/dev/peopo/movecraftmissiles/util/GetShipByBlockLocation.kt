import dev.peopo.movecraftmissiles.movecraft.getShipList
import dev.peopo.movecraftmissiles.util.math.isLocationInShip
import net.countercraft.movecraft.craft.Craft
import org.bukkit.Location
import org.bukkit.block.Block

fun getShipByBlockLocation(loc: Location?): Craft? {
    if (loc == null) return null
    getShipList().forEach {
        if (isLocationInShip(loc, it.hitBox)) return it
    }
    return null
}

fun getShipsByBlockList(blockList: List<Block>): Set<Craft> {
    val shipList = mutableSetOf<Craft>()
    blockList.forEach {
        if (!it.isEmpty) {
            val ship = getShipByBlockLocation(it.location)
            ship?.let { shipList.add(it) }
        }
    }
    return shipList.toSet()
}