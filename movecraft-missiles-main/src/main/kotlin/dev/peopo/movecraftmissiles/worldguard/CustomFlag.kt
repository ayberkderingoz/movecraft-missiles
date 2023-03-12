package dev.peopo.movecraftmissiles.worldguard

import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.flags.StateFlag
import dev.peopo.movecraftmissiles.util.logger

class CustomFlag(

    val NO_EXPLOSION: StateFlag = StateFlag("no-explosion", false),
) {


    fun register() {
        try {
            val registry = WorldGuard.getInstance().flagRegistry
            registry.register(this.NO_EXPLOSION)
            logger.info("Flag no-explosion registered")

        } catch (var1: Exception) {
            logger.warning("Failed to register custom WorldGuard flags: ${var1.message}")

        }
    }


}