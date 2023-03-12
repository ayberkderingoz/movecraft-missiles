package dev.peopo.movecraftmissiles.util

import dev.peopo.movecraftmissiles.util.config.messages.Messages
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.regex.Matcher
import java.util.regex.Pattern


fun String.colorize() = ChatColor.translateAlternateColorCodes('&', translateHexColorCodes(this))

private val hexPattern: Pattern = Pattern.compile("[§&]#([A-Fa-f\\d]{6})")
private fun translateHexColorCodes(message: String): String {
    val matcher: Matcher = hexPattern.matcher(message)
    val buffer = StringBuffer(message.length + 4 * 8)
    while (matcher.find()) {
        val group: String = matcher.group(1)
        matcher.appendReplacement(
            buffer, "§x§${group[0]}§${group[1]}§${group[2]}§${group[3]}§${group[4]}§${group[5]}"
        )
    }
    return matcher.appendTail(buffer).toString()
}


fun Player.sendColorizedMessage(message: Messages) = this.sendMessage(messages.getColorized(message))

fun Player.sendColorizedMessage(message: Messages, parser: (message: String) -> String) {
    val raw = messages.getMessage(message)
    this.sendMessage(parser.invoke(raw).colorize())
}