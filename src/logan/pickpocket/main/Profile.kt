package logan.pickpocket.main

interface Profile {
    val name: String
    val type: ProfileType
    val properties: MutableMap<String, Any>

    fun save(): Boolean {
        PickpocketPlugin.profileConfiguration.createThiefProfile(name)
        return true
    }
}