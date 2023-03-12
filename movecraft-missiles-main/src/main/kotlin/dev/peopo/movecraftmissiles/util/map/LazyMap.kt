package dev.peopo.movecraftmissiles.util.map

class LazyMap<K, V : Any>(private val valueCache: MutableMap<K, V> = mutableMapOf()) : Map<K, V> by valueCache {
    private val lazyValues = mutableMapOf<K, () -> V>()

    fun invalidate(key: K) = valueCache.remove(key)

    operator fun set(key: K, value: () -> V) = lazyValues.put(key, value)
    override operator fun get(key: K): V? =
        valueCache[key] ?: lazyValues[key]?.let { valueCache[key] = it(); valueCache[key] }
}