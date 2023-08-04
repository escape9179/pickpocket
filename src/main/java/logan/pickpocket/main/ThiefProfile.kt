package logan.pickpocket.main

class ThiefProfile(override val name: String) : Profile {
    override val type = ProfileType.THIEF
    override val properties = mutableMapOf(
        "cooldown" to "10",
        "canUseFishingRod" to "false",
        "numberOfRummageItems" to "4",
    )

    var cooldown
        get() = properties["cooldown"]!!.toInt()
        set(value) { properties["cooldown"] = value.toString() }
    var canUseFishingRod
        get() = properties["canUseFishingRod"]!!.toBoolean()
        set(value) { properties["canUseFishingRod"] = value.toString() }
    var maxRummageItems
        get() = properties["numberOfRummageItems"]!!.toInt()
        set(value) { properties["numberOfRummageItems"] = value.toString() }

    override fun equals(other: Any?): Boolean {
        return (other as? ThiefProfile)?.name.equals(this.name)
    }
}