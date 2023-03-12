package dev.peopo.movecraftmissiles.event

import com.github.secretx33.movecraftshields.event.DummyCannonEvent
import net.countercraft.movecraft.craft.Craft
import org.bukkit.Location
import org.bukkit.event.HandlerList

class MissileHitEvent(damage: Int, impactLocation: Location, damagedCraft: Craft, damagerCraft: Craft) :
    DummyCannonEvent(
        damage, impactLocation, damagedCraft, damagerCraft
    ) {

    companion object {
        @JvmStatic
        private val HANDLERS = HandlerList()


    }

    override fun getHandlers(): HandlerList = HANDLERS
}