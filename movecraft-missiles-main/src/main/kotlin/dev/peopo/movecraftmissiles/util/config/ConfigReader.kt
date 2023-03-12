@file:Suppress("DEPRECATION")

package dev.peopo.movecraftmissiles.util.config

import com.github.secretx33.movecraftshields.config.ConfigKeys
import com.github.secretx33.movecraftshields.model.wrapper.SoundInfo
import dev.peopo.movecraftmissiles.util.config
import dev.peopo.movecraftmissiles.util.disable
import dev.peopo.movecraftmissiles.util.logger
import dev.peopo.movecraftmissiles.util.plugin
import dev.peopo.movecraftmissiles.util.sounddata.SoundData
import net.countercraft.movecraft.craft.Craft
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

fun getItemStack(path: String): ItemStack? {
    val itemString = config.getString(path) ?: run {
        logger.warning("No item found at path $path")
        return null
    }
    val itemSplit = itemString.split("#")
    val material = Material.matchMaterial(itemSplit[0]) ?: run {
        logger.warning("No material found with id ${itemSplit[0]}")
        return null
    }
    if (itemSplit.size > 1) {
        return ItemStack(material, itemSplit[1].toInt())
    }
    return ItemStack(material, 1)
}

fun getLaunchSound(path: String): Pair<SoundData,Double>? {
    val soundString = config.getString(path) ?: run {
        logger.warning("No sound found for path $path")
        return null
    }
    val soundList = soundString.split("#")
    val soundName = soundList[0]
    val soundVolume = soundList[1].toFloat()
    val soundPitch = soundList[2].toFloat()
    val soundDistance = soundList[3].toFloat()



    return Pair(SoundData(soundName, soundVolume, soundPitch),soundDistance.toDouble())
}


fun getCustomItem(path: String): ItemStack? {
    val itemString = config.getString(path) ?: run {
        logger.warning("No item found at path $path")
        return null
    }
    val itemSplit = itemString.split("#")
    val material = Material.matchMaterial(itemSplit[0]) ?: run {
        logger.warning("No material found with id ${itemSplit[0]}")
        return null
    }
    if (itemSplit.size > 1) {
        val itemId = itemSplit[1].toInt()
        val itemStack = ItemStack(material, 1)
        val meta = itemStack.itemMeta
        meta.setCustomModelData(itemId)
        itemStack.itemMeta = meta
        return itemStack
    }
    return ItemStack(material, 1)

}

fun getPotionEffectFromConfig(path: String): PotionEffect? {
    val potionString = config.getString(path) ?: return null
    val potionList = potionString.split("#")
    val potionType = PotionEffectType.getByName(potionList[0]) ?: run {
        logger.warning("No potion effect found with id ${potionList[0]}")
        return null
    }
    val potionDuration = potionList[1].toInt()
    val potionAmplifier = potionList[2].toInt()
    return PotionEffect(potionType, potionDuration, potionAmplifier, true, true, true)
}

fun getParticle(path: String): Particle? {
    val particleString = config.getString(path) ?: run {
        logger.warning("No particle found for path $path")
        return null
    }
    return Particle.valueOf(particleString)
}

fun getStructure(path: String): List<Material> {
    val structureString = config.getString(path) ?: run {
        logger.warning("No structure found for path $path")
        logger.warning("Disabling plugin")
        plugin.disable()
        return listOf()
    }

    val structureList = structureString.split("#")
    return List(structureList.size) { i -> Material.matchMaterial(structureList[i])!! }
}

fun getMaterial(path: String): Material? {
    val materialString = config.getString(path) ?: run {
        logger.warning("No control item found for path $path")
        return null
    }
    return Material.matchMaterial(materialString)
}

fun getSound(path: String): SoundData? {
    val soundString = config.getString(path) ?: run {
        logger.warning("No sound found for path $path")
        return null
    }
    val soundList = soundString.split("#")
    val soundName = soundList[0]
    val soundPitch = soundList[1].toFloat()
    val soundDistance = soundList[2].toFloat()


    return SoundData(soundName, soundPitch, soundDistance)
}

fun getCraftComputerLimit(craft: Craft): Int {
    return config.getInt("computer_limits.${craft.type}")

}