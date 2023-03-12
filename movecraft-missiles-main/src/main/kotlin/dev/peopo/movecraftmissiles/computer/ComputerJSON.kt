package dev.peopo.movecraftmissiles.computer

import dev.peopo.movecraftmissiles.util.math.Vector3

data class ComputerJSON(
    val id: String,
    val world: String,
    val location: Vector3<Double>,
) {
}