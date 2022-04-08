package network.warzone.mars.match

import network.warzone.mars.match.tracker.DestroyableDamageEvent
import network.warzone.mars.match.tracker.WoolTracker
import network.warzone.mars.utils.hasMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import tc.oc.pgm.api.map.Gamemode
import tc.oc.pgm.api.party.Competitor
import tc.oc.pgm.api.party.Party
import tc.oc.pgm.api.player.event.MatchPlayerDeathEvent
import tc.oc.pgm.flag.event.FlagStateChangeEvent
import tc.oc.pgm.flag.state.Carried
import tc.oc.pgm.flag.state.Dropped
import tc.oc.pgm.goals.events.GoalTouchEvent
import tc.oc.pgm.lib.net.kyori.adventure.text.Component
import tc.oc.pgm.lib.net.kyori.adventure.text.Component.*
import tc.oc.pgm.lib.net.kyori.adventure.text.JoinConfiguration
import tc.oc.pgm.lib.net.kyori.adventure.text.format.NamedTextColor
import tc.oc.pgm.wool.MonumentWool

class ObjectiveAnnouncer : Listener {
    /**
     * Broadcast wool pickup to enemies
     */
    @EventHandler(priority = EventPriority.LOW)
    fun onWoolPickup(event: GoalTouchEvent) {
        if (!event.match.hasMode(Gamemode.CAPTURE_THE_WOOL, Gamemode.RACE_FOR_WOOL)) return
        val wool = event.goal as? MonumentWool ?: return
        val player = event.player ?: return

        val message = wool.getTouchMessage(player, false)
        sendToEnemies(player.party, message, false)
    }

    /**
     * Broadcast wool drop to entire match
     */
    @EventHandler(priority = EventPriority.LOW)
    fun onWoolDrop(event: MatchPlayerDeathEvent) {
        if (!event.match.hasMode(Gamemode.CAPTURE_THE_WOOL, Gamemode.RACE_FOR_WOOL)) return

        val player = event.victim
        val wools = WoolTracker.holdingCache.filterValues { it.contains(player.id) }.keys

        for (wool in wools) {
            val message = translatable(
                "events.wool.drop",
                NamedTextColor.WHITE,
                wool.componentName,
                player.name
            )
            player.match.sendMessage(message)
        }
    }

    /**
     * Broadcast destroyable damage to the entire match
     */
    @EventHandler(priority = EventPriority.LOW)
    fun onDestroyableDamage(event: DestroyableDamageEvent) {
        if (event.destroyable.isDestroyed || !event.destroyable.isVisible) return

        val message =
            if (event.damage <= 1)
                translatable("events.monument.damage.first", NamedTextColor.WHITE,
                    event.destroyable.owner.name,
                    event.destroyable.componentName,
                    event.player.name
                )
            else
                translatable("events.monument.damage.subsequent", NamedTextColor.WHITE,
                    event.destroyable.owner.name,
                    event.destroyable.componentName,
                    event.player.name,
                    translatable("misc.percentage", NamedTextColor.GRAY,
                        text(event.damage.toDouble() / event.destroyable.breaksRequired * 100f, NamedTextColor.GRAY)
                    )
                )

        event.player.match.sendMessage(message)
    }

    private fun sendToEnemies(party: Party, message: Component, includeObservers: Boolean) {
        val parties = party.match.parties.filterNot { it.nameLegacy == party.nameLegacy }
            .filter { if (!includeObservers) it is Competitor else true }
        parties.forEach { it.sendMessage(message) }
    }
}