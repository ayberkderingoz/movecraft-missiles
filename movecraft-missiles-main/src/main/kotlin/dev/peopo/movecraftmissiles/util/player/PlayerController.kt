package dev.peopo.movecraftmissiles.util.player

import dev.peopo.movecraftmissiles.computer.Computer
import dev.peopo.movecraftmissiles.util.config.messages.Messages
import dev.peopo.movecraftmissiles.util.sendColorizedMessage
import org.bukkit.entity.Player

fun isPlayerUsingComputer(player: Player): Boolean {
    Computer.computerList.forEach {
        if (it.player == player) {
            player.sendColorizedMessage(Messages.ALREADY_USING_COMPUTER)
            return true
        }
    }
    return false
}