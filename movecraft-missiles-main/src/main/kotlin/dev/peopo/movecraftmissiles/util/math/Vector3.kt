package dev.peopo.movecraftmissiles.util.math


data class Vector3<T : Number>(val x: T, val y: T, val z: T)


class ConcurrentBlockVector {
    @Volatile
    var x: Int = 0
        @Synchronized set
        @Synchronized get

    @Volatile
    var y: Int = 0
        @Synchronized set
        @Synchronized get

    @Volatile
    var z: Int = 0
        @Synchronized set
        @Synchronized get
}