package logan.pickpocket.main

enum class ProfileType {
    THIEF, VICTIM;

    val friendlyName = name.lowercase()
}