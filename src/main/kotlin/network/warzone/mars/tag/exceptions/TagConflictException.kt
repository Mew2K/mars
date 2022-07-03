package network.warzone.mars.tag.exceptions

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import network.warzone.mars.utils.FeatureException

data class TagConflictException(val name: String) : FeatureException() {
    override fun asTextComponent(): TextComponent {
        return Component.text("A tag by the name $name already exists.", NamedTextColor.RED)
    }
}
