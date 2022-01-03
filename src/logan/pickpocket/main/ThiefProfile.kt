package logan.pickpocket.main

class ThiefProfile(override val name: String) : Profile {
    override val type = ProfileType.THIEF
    override val properties = mutableMapOf<String, Any>(
        "cooldown" to 10,
        "canUseFishingRod" to false,
        "minigameRollRate" to 20,
        "maxRummageCount" to 5,
        "numberOfRummageItems" to 4,
        "rummageDuration" to 3,
    )

    var cooldown: Int by properties
    var canUseFishingRod: Boolean by properties
    var maxRummageCount: Int by properties
    var minigameRollRate: Long by properties
    var numberOfRummageItems: Int by properties
    var rummageDuration: Int by properties
}