@file:Suppress("DEPRECATION")

package dev.peopo.movecraftmissiles

import dev.peopo.movecraftmissiles.computer.Computer
import dev.peopo.movecraftmissiles.computer.Computer.Companion.computerList
import dev.peopo.movecraftmissiles.craft.MissileCraft
import dev.peopo.movecraftmissiles.launcher.Launcher
import dev.peopo.movecraftmissiles.launcher.Launcher.Companion.launcherList
import dev.peopo.movecraftmissiles.util.player.isPlayerUsingComputer
import dev.peopo.movecraftmissiles.util.*
import dev.peopo.movecraftmissiles.util.config.*
import dev.peopo.movecraftmissiles.util.config.messages.Messages
import dev.peopo.movecraftmissiles.util.math.isLocationInShip
import dev.peopo.movecraftmissiles.worldguard.CustomFlag
import getShipByBlockLocation
import loadComputers
import loadLaunchers
import net.countercraft.movecraft.events.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import saveComputers
import saveLaunchers

class MovecraftMissiles : JavaPlugin() {

    override fun onEnable() {
        CustomFlag().register()
        loadLaunchers()
        Bukkit.getScheduler().runTaskLater(this, Runnable {
            loadComputers()
        }, config.getLong("settings.world_load_time"))
        if (!plugin.hasDependencies()) this.disable()
        val launcherTypes = this.config.getConfigurationSection("launchers")!!.getKeys(false)

        pluginManager.registerEvents(object : Listener {
            @EventHandler
            fun onPlayerInteract(event: PlayerInteractEvent) {
                val p = event.player
                if (event.action == Action.LEFT_CLICK_AIR) {
                    val item = event.item ?: return
                    if (item.type == getMaterial("settings.player_control_item")) {
                        val comp = computerList.find { it.player == p } ?: return
                        comp.launchLaunchers()
                    }
                } else if (event.action == Action.RIGHT_CLICK_BLOCK) {
                    val block = event.clickedBlock ?: return

                    when (block.type) {
                        Material.OAK_WALL_SIGN, Material.ACACIA_WALL_SIGN, Material.BIRCH_WALL_SIGN,
                        Material.CRIMSON_WALL_SIGN, Material.DARK_OAK_WALL_SIGN, Material.JUNGLE_WALL_SIGN,
                        Material.SPRUCE_WALL_SIGN, Material.WARPED_WALL_SIGN,
                        -> {

                            val sign = block.state as? Sign ?: return
                            if (sign.getLine(1) == "§c§lOFFLINE") {
                                event.player.checkPermission(Permissions.CONTROL_COMPUTER) ?: return
                                if (isPlayerUsingComputer(event.player)) return
                                val craft = getShipByBlockLocation(sign.location)
                                if (craft == null) {
                                    p.sendColorizedMessage(Messages.SHIP_NOT_FOUND)
                                    return
                                }
                                computerList.forEach {
                                    if (it.id == sign.getLine(3) && it.craft != null) {
                                        sign.setLine(1, ChatColor.GREEN.toString() + ChatColor.BOLD + "ACTIVE")
                                        sign.update()
                                        it.player = p
                                        it.linkedLaunchers.clear()
                                        launcherList.forEach { launcher ->
                                            if (launcher.computerID == it.id) {
                                                if (!addLauncherToComputer(it, launcher)) {
                                                    val locCopy = it.location.clone()
                                                    val loc = locCopy.add(
                                                        Location(
                                                            locCopy.world,
                                                            launcher.relativeLocation.x,
                                                            launcher.relativeLocation.y,
                                                            launcher.relativeLocation.z
                                                        )
                                                    )
                                                    locCopy.world.getBlockAt(loc).breakNaturally()
                                                    launcherList.remove(launcher)
                                                    it.linkedLaunchers.remove(launcher)
                                                    p.sendColorizedMessage(Messages.LAUNCHER_CAPACITY_REACHED)
                                                }
                                            }
                                        }
                                        return
                                    }
                                }
                                /*sign.setLine(1, ChatColor.GREEN.toString() + ChatColor.BOLD + "ACTIVE")
                                sign.update()
                                val missileCraft = MissileCraft(craft, craft.type.craftName)
                                val computer = Computer(
                                    config.getInt("computer_logic.max_number_of_launchers"),
                                    sign.getLine(3), sign.location
                                )
                                computer.craft = missileCraft
                                computer.player = p
                                computer.linkedLaunchers.clear()
                                launcherList.forEach { launcher ->
                                    if (launcher.computerID == computer.id) {
                                        addLauncherToComputer(computer, launcher)
                                    }

                                }*/
                            } else if (sign.getLine(1) == "§a§lACTIVE") {
                                val computer = computerList.find { it.id == sign.getLine(3) } ?: return
                                if (computer.player == null) {
                                    return
                                }
                                if (event.player != computer.player) return
                                computer.player = null
                                sign.setLine(1, ChatColor.RED.toString() + ChatColor.BOLD + "OFFLINE")
                                sign.update()
                                computer.linkedLaunchers.clear()
                            } else return
                        }

                        else -> {
                            return
                        }
                    }
                }
            }

            /**
             * When player puts a sign down it checks if it is a launcher or computer sign then creates the related object
             * */
            @EventHandler
            fun onSignChangeEvent(e: SignChangeEvent) {
                if ((e.getLine(0) == "Fire Control" || e.getLine(0) == "Launcher") && e.getLine(3) != "") {

                    when (e.getLine(0)) {
                        "Fire Control" -> {
                            e.player.checkPermission(Permissions.CREATE_COMPUTER) ?: return
                            computerList.forEach {
                                if (it.id == e.getLine(3)) {
                                    e.player.sendColorizedMessage(Messages.COMPUTER_ID_IN_USE)
                                    e.isCancelled = true
                                    e.block.breakNaturally()
                                    return
                                }
                            }
                            val computer = Computer(
                                config.getInt("computer_logic.max_number_of_launchers"),
                                e.getLine(3).toString(), e.block.location
                            )
                            computer.player = null
                            e.setLine(0, ChatColor.GOLD.toString() + ChatColor.BOLD + "Fire Control")
                            e.setLine(1, "§c§lOFFLINE")
                            e.player.sendColorizedMessage(Messages.COMPUTER_CREATED) {
                                it.replace("{id}", computer.id)
                            }
                        }

                        "Launcher" -> {
                            e.player.checkPermission(Permissions.CREATE_LAUNCHER) ?: return
                            if (!isLauncher(e)) {
                                e.player.sendColorizedMessage(Messages.LAUNCHER_WRONG_STRUCTURE)
                                e.isCancelled = true
                                e.block.breakNaturally()

                                return
                            }
                            val structureSize = getStructure("launchers.${e.getLine(1)}.structure").size
                            val dispenserBlock = getDispenserOnTip(e.block, structureSize + 2) ?: return
                            e.player.sendMessage("Dispenser: ${dispenserBlock.location}")
                            val launcherType = e.getLine(1) ?: return
                            e.player.sendMessage("Launcher type: $launcherType")
                            if (!launcherTypes.contains(launcherType)) return
                            e.player.sendMessage("Launcher type is valid")
                            val reloadTime = config.getLong("launchers.${launcherType}.reload_time")
                            val ammo = getItemStack("launchers.${launcherType}.ammo_type")
                            computerList.forEach {
                                if (it.id == e.getLine(3)) {
                                    if (it.maxLauncher <= it.linkedLaunchers.size) {
                                        e.player.sendColorizedMessage(Messages.COMPUTER_CAPACITY_REACHED)
                                        return
                                    }

                                    val launcher = Launcher(
                                        it.id, getRelativeLocation(e.block, dispenserBlock, it.location), launcherType,
                                        reloadTime, ammo
                                    )

                                    e.player.sendColorizedMessage(Messages.LAUNCHER_ADDED)
                                    return
                                }
                            }
                            e.player.sendColorizedMessage(Messages.COMPUTER_NOT_FOUND)
                            e.isCancelled = true
                            e.block.breakNaturally()
                        }
                    }
                }
            }


            @EventHandler
            fun onSinkEvent(e: CraftSinkEvent) {
                val missileCraft = MissileCraft.getCraft(e.craft) ?: return
                computerList.forEach { computer ->
                    if (computer.craft == missileCraft) {

                        computer.location.block.breakNaturally()
                        computer.craft = null
                        computer.player = null
                        computer.linkedLaunchers.forEach { launcher ->
                            val loc = computer.location.clone()
                            loc.add(
                                launcher.relativeLocation.x,
                                launcher.relativeLocation.y,
                                launcher.relativeLocation.z
                            ).block.breakNaturally()
                            launcherList.remove(launcher)
                        }
                        computer.linkedLaunchers.clear()
                        computerList.remove(computer)
                    }
                }
            }

            @EventHandler
            fun onCraftDetectEvent(e: CraftDetectEvent) {
                if (MissileCraft.getCraft(e.craft) == null) {
                    val missileCraft = MissileCraft(e.craft, e.craft.type.craftName)
                    computerList.forEach {
                        if(missileCraft.checkLimit() == null){
                            return
                        }
                        else if (!missileCraft.checkLimit()!!) {
                            e.craft.notificationPlayer?.sendColorizedMessage(Messages.COMPUTER_LIMIT_REACHED)
                            //e.isCancelled = true
                            return
                        }
                        else if (isLocationInShip(it.location, e.craft.hitBox)) {
                            it.craft = missileCraft
                            it.craft!!.computers.add(it)
                        }
                    }
                } else {
                    val missileCraft = MissileCraft.getCraft(e.craft)!!
                    computerList.forEach {
                        if(missileCraft.checkLimit() == null){
                            return
                        }
                        else if (!missileCraft.checkLimit()!!) {
                            e.craft.notificationPlayer?.sendColorizedMessage(Messages.COMPUTER_LIMIT_REACHED)
                            //e.isCancelled = true
                            return
                        }
                        else if (isLocationInShip(it.location, e.craft.hitBox)) {
                            it.craft = missileCraft
                            it.craft!!.computers.add(it)
                        }
                    }
                }
            }

            /*@EventHandler
            fun onPlayerPilotEvent(e: CraftPilotEvent){
                waitHitboxThenInitComputersCraft(e)
            }*/
            @EventHandler
            fun onCraftReleaseEvent(e: CraftReleaseEvent) {
                computerList.forEach { computer ->
                    if (computer.craft?.movecraftCraft == e.craft) {
                        computer.craft?.computers?.remove(computer)
                        computer.craft = null
                        val comp = computer.location.block.state as? Sign
                        if (comp != null) {
                            comp.setLine(1, ChatColor.RED.toString() + ChatColor.BOLD + "OFFLINE")
                            comp.update()
                            computer.player = null
                            return
                        }
                        launcherList.forEach() { launcher ->
                            if (launcher.computer == computer) {
                                launcherList.remove(launcher)
                                launcher.getLauncherLocation().block.breakNaturally()
                            }
                        }
                        return
                    }
                }

            }

            @EventHandler
            fun onPlayerLogOutEvent(e: PlayerQuitEvent) {
                val computer = computerList.find { e.player == it.player } ?: return
                val sign = computer.location.block as? Sign ?: return
                sign.setLine(1, "§c§lOFFLINE")
                computerList.remove(computer)
            }

            @EventHandler
            fun onSignBreakEvent(e: BlockBreakEvent) {

                if(e.block.state is Sign) {
                    val sign = e.block.state as Sign
                    if ((sign.getLine(0) == ChatColor.GOLD.toString() + ChatColor.BOLD + "Fire Control" || sign.getLine(
                            0
                        ) == "Launcher") && sign.getLine(
                            3
                        ) != ""
                    ) {
                        when (sign.getLine(0)) {
                            ChatColor.GOLD.toString() + ChatColor.BOLD + "Fire Control" -> {
                                if (e.player.checkPermission(Permissions.BREAK_COMPUTER) == null) {
                                    e.isCancelled = true
                                    return
                                }
                                computerList.forEach {     //IF NEEDED TO ADD A CHECK FOR THE PLAYER it.player == e.player
                                    if (sign.getLine(3) == it.id) {
                                        computerList.remove(it)
                                        it.linkedLaunchers.clear()
                                        return
                                    }
                                }
                            }

                            "Launcher" -> {
                                if (!launcherTypes.contains(sign.getLine(1)) || sign.getLine(2) != "") {
                                    return
                                }
                                if (e.player.checkPermission(Permissions.BREAK_LAUNCHER) == null) {
                                    e.isCancelled = true
                                    return
                                }
                                launcherList.forEach { launcher ->
                                    if (sign.getLine(3) == launcher.computerID) {
                                        computerList.forEach { computer ->
                                            if (computer.id == launcher.computerID) {
                                                computer.linkedLaunchers.remove(launcher)
                                                launcherList.remove(launcher)
                                                return
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
                else if(isBlockAttachedToLauncherOrComputerSign(e.block)){
                    e.isCancelled = true
                    e.player.sendColorizedMessage(Messages.SIGN_BLOCK_BREAK)
                    return
                }
            }

            @EventHandler
            fun onCraftTranslateEvent(e: CraftTranslateEvent) {
                computerList.forEach {
                    if (isLocationInShip(it.location, e.craft.hitBox)) {
                        it.location = updateComputerLocation(it.location, e.newHitBox, e.oldHitBox)
                    }
                }
            }

            /*@EventHandler
            fun onMissileFireEvent(e: MissileExplodeLandEvent){
                dev.peopo.movecraftmissiles.util.logger.info("Explode land event")
                if(isLocationInCustomFlag(e.location)){
                    dev.peopo.movecraftmissiles.util.logger.info("Location is in custom flag")
                    e.isCancelled = true

                }
                if(isLocationInFaction(e.location)){
                    dev.peopo.movecraftmissiles.util.logger.info("Location is in faction")
                    e.isCancelled = true
                }
            }*/
            @EventHandler
            fun onCraftRotateEvent(e: CraftRotateEvent) {

                var computerId: String = ""
                computerList.forEach { computer ->
                    if (!isLocationInShip(computer.location, e.craft.hitBox)) return@forEach
                    computer.location = getRotatedLocation(e, computer.location)
                    computerId = computer.id
                }
                launcherList.forEach {
                    if (it.computerID == computerId) {
                        it.relativeLocation = getRotatedRelativeVector3(e, it.relativeLocation)
                    }
                }
            }
        }, plugin)
    }


    /**
     * Saves launchers to json 😎🤙
     * Changes the state of the computers to OFFLINE
     * !!!IF THE SERVER CRASHES SIGNS NEED TO BE REPLACED*/
    override fun onDisable() {
        computerList.forEach {
            val sign = it.location.block.state as? Sign ?: return@forEach
            sign.setLine(1, "§c§lOFFLINE")
            sign.update()
        }
        saveLaunchers()
        saveComputers()
    }
}
