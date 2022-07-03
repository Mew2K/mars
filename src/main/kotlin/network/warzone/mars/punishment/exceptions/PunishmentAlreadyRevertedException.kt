package network.warzone.mars.punishment.exceptions

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import network.warzone.mars.utils.FeatureException

data class PunishmentAlreadyRevertedException(val id: String) : FeatureException() {
    override fun asTextComponent(): TextComponent {
        return Component.text("The punishment $id is already reverted.", NamedTextColor.RED)
    }
}
