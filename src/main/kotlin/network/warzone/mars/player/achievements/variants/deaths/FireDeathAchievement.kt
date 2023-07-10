package network.warzone.mars.player.achievements.variants.deaths

import kotlinx.coroutines.runBlocking
import network.warzone.mars.Mars
import network.warzone.mars.player.PlayerManager
import network.warzone.mars.player.achievements.*
import network.warzone.mars.player.feature.PlayerFeature
import network.warzone.mars.utils.simple
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import tc.oc.pgm.api.match.Match
import tc.oc.pgm.api.player.event.MatchPlayerDeathEvent

object FireDeathAchievement {
    fun createAchievement(achievement: Achievement, titleName: String) : AchievementAgent =
        object : AchievementAgent, Listener {
            override var match: Match? = null
            override val title: String = titleName
            override val description: String = "Die to a source of fire during any match."
            override val gamemode: String = "NONE"
            override val id: String = achievement.name
            override val parent: AchievementParent = AchievementParent.NO_PARENT

            override fun load() {
                Mars.registerEvents(this)
            }

            @EventHandler
            fun onPlayerDeath(event: MatchPlayerDeathEvent) = runBlocking {
                AchievementManager.sendDebugMessage(achievement.name + ".onPlayerDeath called")
                val victim = event.victim ?: return@runBlocking
                val context = PlayerManager.getPlayer(victim.id) ?: return@runBlocking
                val profile = PlayerFeature.fetch(context.player.name) ?: return@runBlocking
                val cause = event.parent.entity.lastDamageCause ?: return@runBlocking

                AchievementManager.sendDebugMessage(achievement.name + "cause.eventName = " + cause.eventName)

                if (cause.cause == EntityDamageEvent.DamageCause.FIRE ||
                    cause.cause == EntityDamageEvent.DamageCause.FIRE_TICK ||
                    cause.cause == EntityDamageEvent.DamageCause.LAVA) {
                    AchievementEmitter.emit(profile, victim.simple, achievement)
                }
                AchievementManager.sendDebugMessage(achievement.name + ".onPlayerDeath finished")
            }

            override fun unload() {
                HandlerList.unregisterAll(this)
            }
    }
}