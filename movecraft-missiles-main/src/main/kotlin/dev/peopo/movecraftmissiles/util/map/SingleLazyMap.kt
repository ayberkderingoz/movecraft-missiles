package dev.peopo.movecraftmissiles.util.map

class SingleLazyMap<K : Any, V : Any>(
    private val lazy: (K) -> V?,
    private val valueCache: MutableMap<K, V> = mutableMapOf(),
) : Map<K, V> by valueCache {
    fun invalidate(key: K) = valueCache.remove(key)

    override operator fun get(key: K): V? =
        valueCache[key] ?: let { lazy.invoke(key)?.let { valueCache[key] = it; it } }
}
