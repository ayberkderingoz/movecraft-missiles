package dev.peopo.movecraftmissiles.util

import dev.peopo.movecraftmissiles.util.config.messages.Messages
import org.bukkit.entity.Player

enum class Permissions(val permission: String) {
    EXAMPLE("example.example."),
    CREATE_COMPUTER("movecraftmissiles.createcomputer"),
    CREATE_LAUNCHER("movecraftmissiles.createlauncher"),
    SHOOT_MISSILE("movecraftmissiles.shootmissile"),
    CONTROL_COMPUTER("movecraftmissiles.controlcomputer"),
    BREAK_COMPUTER("movecraftmissiles.breakcomputer"),
    BREAK_LAUNCHER("movecraftmissiles.breaklauncher"),
}

fun Player.checkPermission(permission: Permissions): Boolean? {
    if (player?.hasPermission(permission.permission) != true) {
        player?.sendMessage(messages.getColorized(Messages.NO_PERMISSION))
        return null
    }
    return true
}