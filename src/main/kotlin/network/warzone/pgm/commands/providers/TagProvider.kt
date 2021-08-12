package network.warzone.pgm.commands.providers

import app.ashcon.intake.argument.ArgumentParseException
import app.ashcon.intake.argument.CommandArgs
import app.ashcon.intake.argument.Namespace
import app.ashcon.intake.bukkit.parametric.provider.BukkitProvider
import network.warzone.pgm.tags.TagFeature
import network.warzone.pgm.tags.exceptions.TagMissingException
import network.warzone.pgm.tags.models.Tag
import org.bukkit.command.CommandSender

class TagProvider : BukkitProvider<Tag> {

    override fun getName(): String {
        return "tag"
    }

    override fun get(sender: CommandSender?, args: CommandArgs, annotations: MutableList<out Annotation>?): Tag {
        val tagName = args.next()

        val tag: Tag? = TagFeature.getCached(tagName)
        tag ?: throw ArgumentParseException(TagMissingException(tagName).asTextComponent().content())

        return tag
    }

    override fun getSuggestions(
        prefix: String,
        sender: CommandSender,
        namespace: Namespace,
        mods: MutableList<out Annotation>
    ): List<String> {
        val query = prefix.toLowerCase()

        return TagFeature.cache.values
            .map { it.nameLower }
            .filter { it.startsWith(query) }
    }

}