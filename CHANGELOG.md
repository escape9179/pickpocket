#v1.1
//TODO Add steal log for each player (Suggested by zainmz).
//TODO Add reputation factor (Suggested by 0ct0ber).
//TODO Overhaul pickpocket items chances, values, etc.

#v1.0
* Chance for an item depends on how many times you stole it instead of every items chance being affected.
* Replaced experience with times stolen.
* Changed equation for determining 'Your chance'.
* Removed unnecessary configuration sections.
* Added command '/pickpocket giverandom <amount>' to give yourself a random pickpocket item.
    * Permission: pickpocket.developer
* Decreased automatic saving from every 30 seconds to every 5 seconds.
* Pickpocket item inventory always opens at the first page.
* Changed command '/pickpocket xp' to '/pickpocket steals'.
* Removed command '/pickpocket givexp'.
* Added command '/pickpocket bypasscooldown <true/false> <optional name>' to toggle cooldown bypassing.
    * Permission: pickpocket.bypass.cooldown
* Added command '/pickpocket exempt <true/false> <optional name>' to toggle exemption from being stolen from.
    * Permission: pickpocket.exempt
* Added command '/pickpocket admin <true/false>' to toggle admin notifications.
    * Permission: pickpocket.admin

####Bugs
* Some pickpocket items aren't visible in '/pickpocket items' once you've received it.

#v0.9.9
* Fixed multiple NullPointerExceptions
