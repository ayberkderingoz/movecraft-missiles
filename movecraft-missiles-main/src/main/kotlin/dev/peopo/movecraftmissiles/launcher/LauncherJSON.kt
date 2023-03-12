package dev.peopo.movecraftmissiles.launcher

import dev.peopo.movecraftmissiles.util.math.Vector3


data class LauncherJSON(
    val computerId: String,
    val launcherType: String,
    val relativeLocation: Vector3<Double>,
)