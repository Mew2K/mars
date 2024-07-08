package network.warzone.mars.player.achievements

import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import network.warzone.api.database.models.Achievement
import network.warzone.mars.Mars
import network.warzone.mars.api.ApiClient
import network.warzone.mars.api.socket.OutboundEvent
import network.warzone.mars.api.socket.models.PlayerAchievementData
import network.warzone.mars.api.socket.models.SimplePlayer
import network.warzone.mars.player.feature.PlayerFeature
import network.warzone.mars.player.models.PlayerProfile
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.*



// A class for adding completed achievements to a player's profile.
class AchievementEmitter(private val achievement: Achievement) {
    // Fetch a player profile as needed.
    fun emit(player: Player) {
        Mars.async {
            val profile = PlayerFeature.fetch(player.name)!!
            emit(profile)
        }
    }

    // Add an achievement to the specified profile.
    fun emit(profile: PlayerProfile) {
        if (profile.stats.achievements.containsKey(achievement._id.toString())) return // Player already has achievement.

        // Print achievement earn to player and console.
        val player = Bukkit.getPlayer(profile._id)
        if (player != null) {
            val achievementMessage = TextComponent("${ChatColor.GRAY}You've earned an achievement: ${ChatColor.AQUA}${achievement.name}${ChatColor.GRAY}!")
            val hoverText = ComponentBuilder("${ChatColor.GOLD}${achievement.description}").create()
            achievementMessage.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText)
            player.playSound(player.location, Sound.LEVEL_UP, 1.0f, 1.0f)
            player.spigot().sendMessage(achievementMessage)
            println("Achievement " + achievement.name + " earned by " + player.name)
        }

        val data = PlayerAchievementData(
            SimplePlayer(profile._id, profile.name),
            achievement._id,
            Date().time
        )

        if (achievement.firstCompletion == null) {
            achievement.firstCompletion = profile._id
            Bukkit.broadcastMessage("\n${ChatColor.GOLD}${player.name} ${ChatColor.GRAY}is the first to complete the achievement ${ChatColor.AQUA}\"${achievement.name}\" ${ChatColor.GRAY}!")
            Bukkit.broadcastMessage("")
        }

        // Emit achievement completion to the database.
        ApiClient.emit(OutboundEvent.PlayerAchievement, data)
    }
}