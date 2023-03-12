package dev.peopo.movecraftmissiles.util.math

import org.bukkit.util.Vector

class Velocity(x: Double, y: Double, z: Double) {
    @Volatile
    var x: Double = 0.0
        @Synchronized get
        @Synchronized set
    @Volatile
    var y: Double = 0.0
        @Synchronized get
        @Synchronized set
    @Volatile
    var z: Double = 0.0
        @Synchronized get
        @Synchronized set

    init {
        this.x = x
        this.y = y
        this.z = z
    }

    fun toBukkitVector3() = Vector(x, y, z)

}

fun Vector.toVelocity() = Velocity(x, y, z)