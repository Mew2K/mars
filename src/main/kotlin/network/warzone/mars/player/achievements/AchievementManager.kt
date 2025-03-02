package network.warzone.mars.player.achievements

import network.warzone.mars.Mars
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import tc.oc.pgm.api.match.event.MatchStartEvent

object AchievementManager : Listener {
    init {
        Mars.registerEvents(this)
        println("Achievement Manager HAS BEEN ENABLED THIS IS ENABLED.")
    }

    val debugPrefix: String = ChatColor.DARK_GRAY.toString() + "[@] " + ChatColor.GRAY.toString()
    val MewTwoKing: Player?
        get() = Bukkit.getPlayer("MewTwoKing")
    private var initializedAgents = false
    private var initializedParentAgents = false
    val achievementAgents: MutableList<AchievementAgent> = mutableListOf()
    val achievementParentAgents: MutableMap<AchievementParentAgent, MutableList<AchievementAgent>> = mutableMapOf()

    @EventHandler
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        println("MEWTWOKING HAS JOINED THE SERVER OMG OMG OMG")
    }

    // TODO: Achievement parents for the GUI are currently initialized once a player joins for the first time.
    //  This may need to be changed to a different event.
    @EventHandler
    private fun onMatchStart(event: MatchStartEvent) {
        println("A MATCH HAS STARTED OMG OMG OMG")
        sendDebugMessage("AchievementManager.onMatchStart called")
        if (!initializedAgents) {
            initializeAgents()
        }
        for (agent in achievementAgents) agent.match = event.match
        if (!initializedParentAgents) {
            initializeParentAgents()
        }
        sendDebugMessage("AchievementManager.onMatchStart finished")
    }

    //TODO: Instead of using Achievement.values(), we will be using whatever
    // is in the API database.
    private fun initializeAgents() {
        sendDebugMessage("AchievementManager.initializeAgents() called")
        for (achievement in Achievement.values()) {
            println("Enabling achievement: $achievement")
            val agent = achievement.agentProvider()
            //if (!isValidGamemode(agent.gamemode)) return;
            agent.load()
            achievementAgents += agent
        }
        initializedAgents = true
        sendDebugMessage("AchievementManager.initializeAgents() finished")
    }

    private fun initializeParentAgents() {
        sendDebugMessage("AchievementManager.initializeParentAgents() called")
        for (achievement in Achievement.values()) {
            val parent = achievement.agentProvider().parent
            achievementParentAgents.getOrPut(parent.agent) { mutableListOf() }.add(achievement.agentProvider())
        }
        initializedParentAgents = true
        sendDebugMessage("AchievementManager.initializeParentAgents() finished")
    }

    fun unload() {
        HandlerList.unregisterAll(this)
        for (agent in achievementAgents) agent.unload()
    }

    fun isValidGamemode(gamemode: String) : Boolean {
        try {
            GamemodeEnum.valueOf(gamemode.toUpperCase())
            return true
        }
        catch (e: IllegalArgumentException) {
            throw InvalidGamemodeException(gamemode)
        }
    }

    fun sendDebugMessage(message: String) {
        MewTwoKing?.sendMessage(debugPrefix + message)
    }

    fun sendDebugStartMessage(achievement: Achievement, functionName: String) {
        MewTwoKing?.sendMessage(debugPrefix + "\\u00A7e" + achievement.name + "." + functionName + " started.")
    }

    fun sendDebugFinishMessage(achievement: Achievement, functionName: String) {
        MewTwoKing?.sendMessage(debugPrefix + "\\u00A7e" + achievement.name + "." + functionName + " finished.")
    }
}