package logan.pickpocket.main

import logan.pickpocket.user.PickpocketUser
import org.bukkit.entity.Player

/**
 * Created by Tre on 12/17/2015.
 */
abstract class Profiles {
    companion object {
        fun get(player: Player): PickpocketUser {
            for (user in PickpocketPlugin.profiles) {
                if (user.uuid == player.uniqueId) {
                    return user
                }
            }
            val profile = PickpocketUser(player.uniqueId)
            PickpocketPlugin.addProfile(profile)
            return profile
        }
    }
}
