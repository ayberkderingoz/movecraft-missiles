package dev.peopo.movecraftmissiles.util.faction

import com.massivecraft.factions.entity.BoardColl
import com.massivecraft.massivecore.ps.PS
import org.bukkit.Location
import org.bukkit.block.Block

fun isLocationInFaction(location: Location, blockList: List<Block>): Boolean {
    blockList.forEach { block ->
        val faction =
            BoardColl.get().getFactionAt(PS.valueOf(block.location)) ?: return false
        if (faction.name != "§2Wilderness") {
            return true
        }
    }

    return false

}