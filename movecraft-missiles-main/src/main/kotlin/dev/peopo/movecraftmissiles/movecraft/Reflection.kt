package dev.peopo.movecraftmissiles.movecraft

import dev.peopo.movecraftmissiles.util.disable
import dev.peopo.movecraftmissiles.util.plugin
import net.countercraft.movecraft.craft.Craft
import net.countercraft.movecraft.craft.CraftManager

private val movecraftAPI = CraftManager.getInstance() ?: run { plugin.disable() }

private val craftListField by lazy {
    val field = CraftManager::class.java.getDeclaredField("craftList") ?: run { plugin.disable(); return@lazy null }
    field.isAccessible = true
    return@lazy field
}

fun getShipList() = craftListField?.get(movecraftAPI) as? Set<Craft> ?: emptySet()
