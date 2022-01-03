package logan.pickpocket.main

class ThiefProfile(override val name: String) : Profile {
    override val type = ProfileType.THIEF
    override val properties = mutableMapOf(
        "cooldown" to 10,
        "canUseFishingRod" to false,
        "minigameRollRate" to 20,
        "maxRummageCount" to 5,
        "numberOfRummageItems" to 4,
        "rummageDuration" to 3,
    )

    val cooldown
        get() = properties["cooldown"] as Int
    val canUseFishingRod
        get() = properties["canUseFishingRod"] as Boolean
    val maxRummageCount
        get() = properties["maxRummageCount"] as Int
    val minigameRollRate
        get() = properties["minigameRollRate"] as Long
    val numberOfRummageItems
        get() = properties["numberOfRummageItems"] as Int
    val rummageDuration
        get() = properties["rummageDuration"] as Int
}