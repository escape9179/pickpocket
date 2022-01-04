package logan.pickpocket.main

interface Profile {
    val name: String
    val type: ProfileType
    val properties: MutableMap<String, String>

    fun save(): Boolean {
        PickpocketPlugin.profileConfiguration.createThiefProfile(name)
        return true
    }
}