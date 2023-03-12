package dev.peopo.movecraftmissiles.craft

import dev.peopo.movecraftmissiles.computer.Computer
import dev.peopo.movecraftmissiles.util.config
import net.countercraft.movecraft.craft.Craft
import java.util.concurrent.ConcurrentHashMap

class

MissileCraft(
    val movecraftCraft: Craft?,
    val type: String,
) {
    val computers: MutableSet<Computer> = mutableSetOf()
    private val computerLimit: Int

    init {
        movecraftCraft?.let { craftCache[movecraftCraft] = this }
        computerLimit = config.getInt("computer_limits.${type.lowercase()}")

    }

    fun checkLimit(): Boolean? {
        //if(computerLimit == -1){
            //return true
        //}
        if(computerLimit == 0){
            return null
        }
        else if (computers.size >= computerLimit) {
            return false
        }
        return true
    }

    companion object {
        private val craftCache = ConcurrentHashMap<Craft, MissileCraft>()

        fun getCraft(craft: Craft): MissileCraft? {
            return craftCache[craft]
        }
    }
}