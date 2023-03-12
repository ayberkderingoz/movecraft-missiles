@file:Suppress("DEPRECATION")

package dev.peopo.movecraftmissiles.missile

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import dev.peopo.movecraftmissiles.computer.Computer
import dev.peopo.movecraftmissiles.event.MissileHitEvent
import dev.peopo.movecraftmissiles.util.config
import dev.peopo.movecraftmissiles.util.config.getCustomItem
import dev.peopo.movecraftmissiles.util.config.getItemStack
import dev.peopo.movecraftmissiles.util.config.getParticle
import dev.peopo.movecraftmissiles.util.config.getPotionEffectFromConfig
import dev.peopo.movecraftmissiles.util.faction.isLocationInFaction
import dev.peopo.movecraftmissiles.util.logger
import dev.peopo.movecraftmissiles.util.math.Velocity
import dev.peopo.movecraftmissiles.util.math.getBlocksInsideOfSphere
import dev.peopo.movecraftmissiles.util.math.hasBlocksNearby
import dev.peopo.movecraftmissiles.util.plugin
import dev.peopo.movecraftmissiles.util.pluginManager
import dev.peopo.movecraftmissiles.worldguard.isLocationInCustomFlag
import getShipsByBlockList
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.entity.ArmorStand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.EulerAngle
import java.util.*
import kotlin.math.asin
import kotlin.math.atan2

abstract class Missile(
    val computer: Computer,
    direction: Velocity,
    type: String,
    val armorStand: ArmorStand,
) : ArmorStand by armorStand {

    protected val missileVelocity = Velocity(0.0, 0.0, 0.0)
    protected val speed = config.getDouble("launchers.$type.projectile_speed")
    private val lifeSpan = config.getLong("launchers.$type.projectile_lifetime")
    private val particle = getParticle("launchers.$type.particle_trail")
    private val item = getCustomItem("launchers.$type.material_fired")
    private val potionEffect = getPotionEffectFromConfig("launchers.$type.potion_effect")
    private val explosionYield = config.getInt("launchers.$type.explosive_yield")
    private var firedFrom = computer.craft!!.movecraftCraft!!
    private val shieldDamage = config.getInt("launchers.$type.shield_damage")

    private val bukkitTask: BukkitTask

    init {
        this.isVisible = false
        this.setHelmet(item)
        this.missileVelocity.x = direction.x * speed
        this.missileVelocity.y = direction.y * speed
        this.missileVelocity.z = direction.z * speed
        bukkitTask = Bukkit.getScheduler().runTaskLater(plugin, Runnable { explode() }, lifeSpan)
        armorStand.headPose = EulerAngle(0.0, 0.0, 0.0)
        val pitch = asin(-direction.y)
        val yaw = Math.toRadians(Math.toDegrees(atan2(direction.z, direction.x)) - 90)
        armorStand.headPose = EulerAngle(pitch, yaw, 0.0)
        this.setRotation(yaw.toFloat(), 0f)
    }

    fun onTick() {
        armorStand.isMarker = false
        this.velocity = missileVelocity.toBukkitVector3() //x not finite

        if (particle != null) {
            this.world.spawnParticle(particle, this.location.add(0.0,2.0,0.0), 1)
        }
    }

    private fun explode() {
        Bukkit.getScheduler().runTask(plugin, Runnable {
            this.world.spawnParticle(Particle.EXPLOSION_HUGE, this.location, 1)
            missiles.remove(this)
            bukkitTask.cancel()

            if (this is HomingMissile) {
                this.companion.homingMissiles.remove(this)
            }

            potionEffect?.let { pe ->
                this.world.getNearbyLivingEntities(
                    this.location,
                    explosionYield.toDouble(),
                    explosionYield.toDouble(),
                    explosionYield.toDouble()
                ).forEach { it.addPotionEffect(pe) }
            }
            this.armorStand.remove()
            this.remove()

            val explodedBlocks = getBlocksInsideOfSphere(this.location, explosionYield, false)
            val effectedShips = getShipsByBlockList(explodedBlocks)
            if (effectedShips.contains(firedFrom)) { return@Runnable }
            effectedShips.forEach { ship ->
                val hitEvent = MissileHitEvent(shieldDamage, this.location, ship, this.firedFrom)
                hitEvent.callEvent()
            }

            if (isLocationInFaction(this.location, explodedBlocks)) return@Runnable
            if (isLocationInCustomFlag(this.location, explodedBlocks)) return@Runnable
            val explodeEvent = BlockExplodeEvent(this.location.block, explodedBlocks, 0.0f)
            explodeEvent.callEvent()
            if (!explodeEvent.isCancelled)
                Bukkit.getScheduler().runTask(plugin, Runnable {
                    explodeEvent.blockList().forEach { it.type = org.bukkit.Material.AIR }
                })
        })
    }

    fun onCollision() {
        explode()
    }

    companion object : Listener {
        init {
            pluginManager.registerEvents(this, plugin)
            Bukkit.getScheduler().runTaskTimer(plugin, Runnable { missiles.toSet().forEach { it.onTick() } }, 1, 1)

            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, Runnable {
                missiles.toSet().forEach {
                    if (it.location.hasBlocksNearby(1)) {
                        it.onCollision()
                    }
                }
            }, config.getLong("settings.collision_check"), config.getLong("settings.collision_check"))

            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, Runnable {
                for (missile in missiles.toSet()) {
                    (missile as? HomingMissile)?.turnMissile()
                }
            }, config.getLong("settings.turn_calculation"), config.getLong("settings.turn_calculation"))
        }

        val missiles: MutableSet<Missile> = Collections.synchronizedSet(mutableSetOf<Missile>())

        @EventHandler
        fun onEntityRemove(event: EntityRemoveFromWorldEvent) {
            val missile = event.entity as? Missile ?: return
            missiles.remove(missile)
        }
    }
}