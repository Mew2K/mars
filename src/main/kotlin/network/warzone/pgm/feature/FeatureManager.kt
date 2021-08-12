package network.warzone.pgm.feature

import app.ashcon.intake.bukkit.graph.BasicBukkitCommandGraph
import kotlinx.coroutines.runBlocking
import network.warzone.pgm.WarzonePGM
import network.warzone.pgm.api.events.ApiConnectedEvent
import network.warzone.pgm.feature.resource.ResourceType
import network.warzone.pgm.map.MapFeature
import network.warzone.pgm.player.feature.PlayerFeature
import network.warzone.pgm.ranks.RankFeature
import network.warzone.pgm.tags.TagFeature
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object FeatureManager : Listener {

    private val features: MutableMap<ResourceType<*>, Feature<*, *>> = mutableMapOf(
        ResourceType.Player to PlayerFeature,
        ResourceType.Rank to RankFeature,
        ResourceType.Tag to TagFeature,
        ResourceType.Map to MapFeature
    )

    fun init() {
        WarzonePGM.registerEvents(this)
    }

    fun <T : Feature<*, *>> getFeature(type: ResourceType<T>): T {
        return type.cast(features[type]!!)
    }

    fun registerCommands(graph: BasicBukkitCommandGraph) {
        features.values.forEach {
            it.getSubcommands().forEach { entry ->
                val node = graph.rootDispatcherNode.registerNode(*entry.key.toTypedArray())
                node.registerCommands(entry.value)
            }

            it.getCommands().forEach { command ->
                graph.rootDispatcherNode.registerCommands(command)
            }
        }
    }

    @EventHandler
    fun onApiConnected(event: ApiConnectedEvent) = runBlocking {
        features.values.forEach {
            it.init()
        }
    }

}