package network.warzone.pgm.ranks.models

import network.warzone.pgm.feature.named.NamedResource
import network.warzone.pgm.feature.resource.Resource
import java.util.*

data class Rank (
    override val _id: UUID,
    override var name: String,
    var displayName: String?,
    var prefix: String?,
    var priority: Int,
    var createdAt: Date,
    var staff: Boolean,
    var applyOnJoin: Boolean,
    var permissions: MutableList<String>
) : NamedResource {

    val nameLower: String get() = name.toLowerCase(Locale.ROOT)

    override fun generate(): Resource {
        // no relations?
        return this
    }

}