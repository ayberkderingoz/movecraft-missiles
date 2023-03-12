package dev.peopo.movecraftmissiles.launcher

import dev.peopo.movecraftmissiles.computer.Computer
import dev.peopo.movecraftmissiles.missile.HomingMissile
import dev.peopo.movecraftmissiles.util.config.getStructure
import dev.peopo.movecraftmissiles.util.config.messages.Messages
import dev.peopo.movecraftmissiles.util.getDropper
import dev.peopo.movecraftmissiles.util.getMissileSpawnLocation
import dev.peopo.movecraftmissiles.util.isLauncher
import dev.peopo.movecraftmissiles.util.math.Vector3
import dev.peopo.movecraftmissiles.util.sendColorizedMessage
import org.bukkit.Location
import org.bukkit.block.Sign
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import java.util.*


class Launcher(
    val computerID: String,
    var relativeLocation: Vector3<Double>,
    val type: String,
    private val reloadTime: Long,
    private val ammoType: ItemStack?,
) {
    lateinit var computer: Computer

    init {
        launcherList.add(this)
    }

    private var lastFire = 0L
    fun fire(loc: Location): Boolean {
        if (!isLauncherAlive(this)) return false.also { computer.player?.sendColorizedMessage(Messages.LAUNCHER_IS_DEAD) }
        if (System.currentTimeMillis() - lastFire < reloadTime) computer.player?.sendColorizedMessage(Messages.LAUNCHER_COOLDOWN)
            .also { return false }
        lastFire = System.currentTimeMillis()

        ammoType?.let {
            if (!useAmmo(loc)) {
                computer.player?.sendColorizedMessage(Messages.NOT_ENOUGH_AMMO)
                return false
            }
        }
        val structureSize = getStructure("launchers.${type}.structure").size
        val (missileSpawnLocation, direction) = getMissileSpawnLocation(loc.block, structureSize + 2) ?: return false
        val armorStand: ArmorStand =
            (missileSpawnLocation.world?.spawnEntity(missileSpawnLocation, EntityType.ARMOR_STAND) as ArmorStand)
        armorStand.isVisible = false
        val missile = HomingMissile(computer, direction, type, armorStand)
        computer.player!!.sendColorizedMessage(Messages.MISSILE_LAUNCH)
        missile.spawnAt(missileSpawnLocation)
        return true
    }

    private fun isLauncherAlive(launcher: Launcher): Boolean {
        if (launcher.getLauncherLocation().block.state is Sign) {
            if (isLauncher(launcher.getLauncherLocation().block.state as Sign)) {
                return true
            }
            return false
        }
        return false
    }

    private fun useAmmo(launcherLoc: Location): Boolean {
        getDropper(launcherLoc.block)?.let {
            if (it.inventory.containsAtLeast(ammoType, ammoType!!.amount)) {
                it.inventory.removeItem(ammoType)
                return true
            }
        }
        return false
    }

    fun getLauncherLocation(): Location {
        return computer.location.clone().add(this.relativeLocation.x, this.relativeLocation.y, this.relativeLocation.z)
    }

    companion object {
        val launcherList: MutableSet<Launcher> = Collections.synchronizedSet(mutableSetOf<Launcher>())
    }
}