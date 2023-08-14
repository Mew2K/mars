package network.warzone.mars.player.achievements.variants

import network.warzone.api.database.models.AgentParams
import network.warzone.mars.api.socket.models.PlayerUpdateData
import network.warzone.mars.api.socket.models.PlayerUpdateEvent
import network.warzone.mars.api.socket.models.PlayerUpdateReason
import network.warzone.mars.match.models.DeathCause
import network.warzone.mars.player.achievements.AchievementAgent
import network.warzone.mars.player.achievements.AchievementEmitter
import org.bukkit.event.EventHandler

class FireDeathAchievement(
    val params: AgentParams.FireDeathAgentParams,
    override val emitter: AchievementEmitter) : AchievementAgent
{
    @EventHandler
    fun onProfileUpdate(event: PlayerUpdateEvent) {
        if (event.update.reason != PlayerUpdateReason.DEATH) return
        val deathData = event.update.data as PlayerUpdateData.KillUpdateData
        val victimProfile = event.update.updated
        if (deathData.data.cause == DeathCause.FIRE ||
            deathData.data.cause == DeathCause.LAVA) {
            emitter.emit(victimProfile)
        }
    }
}