package network.warzone.mars.player.commands

import app.ashcon.intake.Command
import app.ashcon.intake.CommandException
import app.ashcon.intake.bukkit.parametric.annotation.Sender
import network.warzone.mars.Mars
import network.warzone.mars.commands.providers.PlayerName
import network.warzone.mars.match.tracker.KillstreakTracker
import network.warzone.mars.player.feature.LevelColorService
import network.warzone.mars.player.feature.PlayerFeature
import network.warzone.mars.player.models.PlayerProfile
import network.warzone.mars.utils.matchPlayer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import tc.oc.pgm.api.player.MatchPlayer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.*
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import javax.annotation.Nullable


class StatCommands {
    @Command(aliases = ["killstreak", "ks"], desc = "View your current killstreak", usage = "[player]")
    fun onKillstreakView(@Sender sender: Player, @Nullable @PlayerName playerName: String?) {
        val player = playerName?.let { Bukkit.getPlayer(it) } ?: sender
        val killstreak = KillstreakTracker.getKillstreak(player.uniqueId ?: sender.uniqueId)
            ?: return sender.sendMessage("${ChatColor.RED}Killstreaks are not being tracked")
        val (_, color) = KillstreakTracker.getNearestTrackedKillstreak(killstreak)
        if (killstreak > 0) {
            val perspective = if (player == sender) "You're" else "${player.name} is"
            sender.matchPlayer.sendMessage(
                text("$perspective on a killstreak of ", NamedTextColor.GREEN)
                    .append(text(killstreak, color, TextDecoration.BOLD))
                    .append(text(" kills.", NamedTextColor.GREEN))
            )
        } else {
            val perspective = if (player == sender) "You don't" else "${player.name} doesn't"
            sender.sendMessage("${ChatColor.RED}$perspective have a killstreak yet.")
        }
    }

    @Command(aliases = ["stats", "statistics"], desc = "View a player's stats", usage = "[player]")
    fun onStatsView(@Sender sender: Player, @Nullable @PlayerName playerName: String?) {
        Mars.async {
            if (playerName == null) {
                viewStats(sender.matchPlayer, PlayerFeature.fetch(sender.name) ?: throw CommandException("Cant find profile for player."))
            } else {
                val profile = PlayerFeature.fetch(playerName)
                    ?: PlayerFeature.fetch(sender.name)
                    ?: throw CommandException("Cant find profile for player.")
                viewStats(sender.matchPlayer, profile)
            }
        }
    }

    private fun viewStats(sender: MatchPlayer, profile: PlayerProfile) {
        val stats = profile.stats
        val outline = text("-------------------------------", NamedTextColor.BLUE, TextDecoration.STRIKETHROUGH)
        sender.sendMessage(outline)
        val component =
            text()
                .append(
                    space(),
                    space(),
                    text("Viewing stats for ", NamedTextColor.DARK_AQUA).append(
                        text(
                            profile.name,
                            NamedTextColor.AQUA
                        )
                    ).append(newline())
                )
                .append { newline() }
                .append {
                    createCustomStat(
                        "Level",
                        stats.level,
                        LevelColorService.chatColorFromLevel(stats.level)
                    )
                }
                .append { createLabelledStat("XP", stats.xp, StatType.NEUTRAL) }
                .append { newline() }
                .append { createLabelledStat("Kills", stats.kills, StatType.POSITIVE) }
                .append { createLabelledStat("First Bloods", stats.firstBloods, StatType.POSITIVE) }
                .append { createLabelledStat("Deaths", stats.deaths, StatType.NEGATIVE) }
                .append { createLabelledStat("K/D", stats.kdr, StatType.NEUTRAL) }
                .append { newline() }
                .append { createLabelledStat("Wins", stats.wins, StatType.POSITIVE) }
                .append { createLabelledStat("Losses", stats.losses, StatType.NEGATIVE) }
                .append { createLabelledStat("Win %", stats.winPercentage, StatType.NEUTRAL) }
                .append { outline }
        sender.sendMessage(component)
    }

    enum class StatType(val color: NamedTextColor) {
        POSITIVE(NamedTextColor.GREEN),
        NEGATIVE(NamedTextColor.RED),
        NEUTRAL(NamedTextColor.AQUA)
    }

    private fun createLabelledStat(label: String, value: Any, type: StatType): Component {
        return text()
            .append { space() }
            .append { space() }
            .append { text("$label: ", NamedTextColor.DARK_AQUA) }
            .append { text(value.toString(), type.color) }
            .append { newline() }
            .build()
    }

    private fun createCustomStat(label: String, value: Any, color: NamedTextColor): Component {
        return text()
            .append { space() }
            .append { space() }
            .append { text("$label: ", NamedTextColor.DARK_AQUA) }
            .append { text(value.toString(), color) }
            .append { newline() }
            .build()
    }
}