package logan.pickpocket.main

class VictimProfile(override val name: String) : Profile {
    override val type = ProfileType.VICTIM
    override val properties = mutableMapOf<String, Any>()
}