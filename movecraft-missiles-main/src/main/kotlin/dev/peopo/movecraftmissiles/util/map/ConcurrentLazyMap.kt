package dev.peopo.movecraftmissiles.util.map

import java.util.concurrent.ConcurrentHashMap

class ConcurrentLazyMap<K : Any, V : Any>(private val valueCache: ConcurrentHashMap<K, V> = ConcurrentHashMap()) :
    Map<K, V> by valueCache {
    private val lazyValues = ConcurrentHashMap<K, () -> V>()

    fun invalidate(key: K) = valueCache.remove(key)

    operator fun set(key: K, value: () -> V) = lazyValues.put(key, value)
    override operator fun get(key: K): V? =
        valueCache[key] ?: lazyValues[key]?.let { valueCache[key] = it(); valueCache[key] }
}