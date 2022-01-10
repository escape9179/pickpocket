package logan.pickpocket.main

class ThiefProfile(override val name: String) : Profile {
    override val type = ProfileType.THIEF
    override val properties = mutableMapOf(
        "cooldown" to "10",
        "canUseFishingRod" to "false",
        "minigameRollRate" to "20",
        "maxRummageCount" to "5",
        "numberOfRummageItems" to "4",
        "rummageDuration" to "3",
    )

    var cooldown
        get() = properties["cooldown"]!!.toInt()
        set(value) { properties["cooldown"] = value.toString() }
    var canUseFishingRod
        get() = properties["canUseFishingRod"]!!.toBoolean()
        set(value) { properties["canUseFishingRod"] = value.toString() }
    var maxRummageCount
        get() = properties["maxRummageCount"]!!.toInt()
        set(value) { properties["maxRummageCount"] = value.toString() }
    var minigameRollRate
        get() = properties["minigameRollRate"]!!.toLong()
        set(value) { properties["minigameRollRate"] = value.toString() }
    var maxRummageItems
        get() = properties["numberOfRummageItems"]!!.toInt()
        set(value) { properties["numberOfRummageItems"] = value.toString() }
    var rummageDuration
        get() = properties["rummageDuration"]!!.toInt()
        set(value) { properties["rummageDuration"] = value.toString() }

    override fun equals(other: Any?): Boolean {
        return (other as? ThiefProfile)?.name.equals(this.name)
    }
}