package dev.peopo.movecraftmissiles.computer

import dev.peopo.movecraftmissiles.craft.MissileCraft
import dev.peopo.movecraftmissiles.launcher.Launcher
import dev.peopo.movecraftmissiles.util.Permissions
import dev.peopo.movecraftmissiles.util.checkPermission
import dev.peopo.movecraftmissiles.util.config.getLaunchSound
import dev.peopo.movecraftmissiles.util.config.messages.Messages
import dev.peopo.movecraftmissiles.util.sendColorizedMessage
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import java.util.*


class Computer(
    val maxLauncher: Int,
    val id: String,
    var location: Location,

    ) {
    var craft: MissileCraft? = null
    var active: Boolean = false
    var player: Player? = null
    val linkedLaunchers: MutableSet<Launcher> = mutableSetOf()

    init {
        computerList.add(this)
    }


    fun launchLaunchers() {
        player?.checkPermission(Permissions.SHOOT_MISSILE) ?: return


        if (!isComputerAlive(this)) return
        linkedLaunchers.forEach {
            val locCopy = location.clone()
            val launchSound = getLaunchSound("launchers.${it.type}.launch_sound")
            val players = locCopy.getNearbyPlayers(launchSound?.second ?: 0.0)
            players.forEach{player->
                if (launchSound != null) {
                    player.playSound(locCopy, launchSound.first.sound, launchSound.first.volume, launchSound.first.pitch)
                }
            }
            it.fire(locCopy.add(it.relativeLocation.x, it.relativeLocation.y, it.relativeLocation.z))
        }
    }

    fun isComputerAlive(computer: Computer): Boolean {
        if (computer.location.block.state is Sign) {
            return true
        }
        computerList.remove(computer)
        computer.player?.sendColorizedMessage(Messages.COMPUTER_IS_DEAD)
        return false
    }

    companion object {
        val computerList: MutableSet<Computer> = Collections.synchronizedSet(mutableSetOf<Computer>())
    }
}
