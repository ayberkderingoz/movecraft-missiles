package dev.peopo.movecraftmissiles.util.map

import java.util.concurrent.ConcurrentHashMap

open class ConcurrentSingleLazyMap<K : Any, V : Any>(
    private val lazy: suspend (K) -> V?,
    private val valueCache: ConcurrentHashMap<K, V> = ConcurrentHashMap(),
) : Map<K, V> by valueCache {
    fun invalidate(key: K) = valueCache.remove(key)

    suspend fun getOrFetch(key: K): V? = valueCache[key] ?: let { lazy.invoke(key)?.let { valueCache[key] = it; it } }
}