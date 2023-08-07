package logan.pickpocket.main

class Profile(val name: String) {
    val type = ProfileType.THIEF
    val properties = mutableMapOf(
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
        return (other as? Profile)?.name.equals(this.name)
    }

    fun save(): Boolean {
        PickpocketPlugin.profileConfiguration.saveProfile(this)
        return true
    }
}