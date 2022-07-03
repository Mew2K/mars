package network.warzone.mars.player.feature.exceptions

import network.warzone.mars.utils.FeatureException
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor

data class PlayerNotOnlineException(val target: String) : FeatureException() {
    override fun asTextComponent(): TextComponent {
        return Component.text("The player $target is not online.", NamedTextColor.RED)
    }
}