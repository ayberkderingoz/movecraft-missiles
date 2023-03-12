package dev.peopo.movecraftmissiles.missile

import dev.peopo.movecraftmissiles.computer.Computer
import dev.peopo.movecraftmissiles.util.math.Velocity
import org.bukkit.entity.ArmorStand

class DumbMissile(computer: Computer, direction: Velocity, type: String, armorStand: ArmorStand) :
    Missile(computer, direction, type, armorStand) {
    init {
        missiles.add(this)
    }
}