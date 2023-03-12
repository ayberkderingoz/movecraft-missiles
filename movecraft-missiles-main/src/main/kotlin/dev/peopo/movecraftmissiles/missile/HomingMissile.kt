@file:Suppress("DEPRECATION")

package dev.peopo.movecraftmissiles.missile


import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import dev.peopo.movecraftmissiles.computer.Computer
import dev.peopo.movecraftmissiles.util.config
import dev.peopo.movecraftmissiles.util.config.getMaterial
import dev.peopo.movecraftmissiles.util.config.getSound
import dev.peopo.movecraftmissiles.util.math.*
import dev.peopo.movecraftmissiles.util.plugin
import dev.peopo.movecraftmissiles.util.pluginManager
import getShipByBlockLocation
import net.countercraft.movecraft.craft.Craft
import org.bukkit.Bukkit
import org.bukkit.entity.ArmorStand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector
import kotlin.math.*

class HomingMissile(
    computer: Computer, direction: Velocity, type: String, armorStand: ArmorStand,
) : Missile(computer, direction, type, armorStand) {

    val companion = Companion
    private var targetShip: Craft? = null
    private var firedAt = System.currentTimeMillis()
    private var maxTurn = config.getDouble("launchers.$type.turn_radius")
    private var homing = config.getBoolean("launchers.$type.is_homing")
    private var soundData = getSound("launchers.$type.sound")
    private val aimDelay = config.getLong("launchers.$type.aim_delay")

    @Volatile
    var currentTarget: ConcurrentBlockVector? = null
        @Synchronized get() {
            targetShip?.let {
                return getShipCenter(it).toTargetVector()
            } ?: run {
                return field
            }
        }
        @Synchronized set

    init {
        val loc = armorStand.location
        loc.pitch = 0f
        loc.yaw = 0f
        targetShip?.let { craft ->
            if (isLocationInShip(computer.location, craft.hitBox)) targetShip = null
        }
        this.setRotation(0f, 0f)
        missiles.add(this)
        homingMissiles.add(this)
    }

    fun turnMissile() {
        if (!canTurn()) return
        val immutableTarget = currentTarget ?: return
        val target = Vector(immutableTarget.x, immutableTarget.y, immutableTarget.z)

        val direction = Vector(missileVelocity.x, missileVelocity.y, missileVelocity.z).normalize()
        val targetDirection =
            Vector(target.x - location.x, target.y - location.y, target.z - location.z).normalize()
        var angle = targetDirection.angle(direction)
        if (angle > maxTurn) angle = maxTurn.toFloat()
        else if (angle < -maxTurn.toFloat()) angle = -maxTurn.toFloat()
        val dTick = ((direction.getCrossProduct(targetDirection)).getCrossProduct(direction)).normalize()
        val cosAngle = cos(angle)
        val sinAngle = sin(angle)
        val result = Vector(
            (direction.x * cosAngle + dTick.x * sinAngle),
            (direction.y * cosAngle + dTick.y * sinAngle),
            (direction.z * cosAngle + dTick.z * sinAngle)
        ).normalize()


        missileVelocity.x = result.x * speed
        missileVelocity.y = result.y * speed
        missileVelocity.z = result.z * speed

        Bukkit.getScheduler().runTask(plugin, Runnable {
            armorStand.headPose = EulerAngle(0.0, 0.0, 0.0)
            val pitch = asin(-result.y)
            val yaw = Math.toRadians(Math.toDegrees(atan2(result.z, result.x)) - 90)
            armorStand.headPose = EulerAngle(pitch, yaw, 0.0)

        })
    }

    private fun recalculateTargetLocation() {
        if (targetShip != null) {
            val target = getShipCenter(targetShip!!)
            currentTarget = target.toTargetVector()
            soundData?.let {
                val targetPlayer = targetShip!!.notificationPlayer
                targetPlayer?.playSound(targetPlayer.location, it.sound, it.volume, it.pitch)
                plugin.config.getString("messages.target_locked")?.let { it1 -> targetPlayer?.sendActionBar(it1) }
            }
        }
        if (targetShip == null) {
            if (homing) {
                val controlMaterial = getMaterial("settings.player_control_item")
                if (computer.player?.itemInHand?.type != controlMaterial) return
                val lookedLocation = computer.player?.getTargetBlock(100)?.location ?: return
                val lookedCraft = getShipByBlockLocation(lookedLocation)

                if (lookedCraft == null || lookedCraft == computer.craft!!.movecraftCraft) {
                    currentTarget = lookedLocation.toTargetVector()
                    return
                }

                targetShip = lookedCraft
                recalculateTargetLocation()
            } else {
                val controlMaterial = getMaterial("settings.player_control_item")
                if (computer.player?.itemInHand?.type != controlMaterial) return
                val lookedLocation = computer.player?.getTargetBlock(100)?.location ?: return
                currentTarget = lookedLocation.toTargetVector()
                return
            }
        }
    }

    private fun canTurn(): Boolean {
        if (System.currentTimeMillis() - firedAt < aimDelay) return false
        return true
    }

    companion object : Listener {
        val homingMissiles = mutableSetOf<HomingMissile>()

        init {
            pluginManager.registerEvents(this, plugin)
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, Runnable {
                HashSet(homingMissiles).forEach {
                    it.recalculateTargetLocation()
                }
            }, config.getLong("settings.turn_calculation"), config.getLong("settings.turn_calculation"))

            @EventHandler
            fun onEntityRemove(event: EntityRemoveFromWorldEvent) {
                val missile = event.entity as? HomingMissile ?: return
                homingMissiles.remove(missile)
            }
        }
    }
}