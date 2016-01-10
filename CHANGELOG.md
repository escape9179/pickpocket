#V1.1
//TODO Add steal log for each player.
//TODO Add reputation factor (Suggested by 0ct0ber)

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

####Bugs
* Some pickpocket items aren't visible in '/pickpocket items' once you've received it.

#v0.9.9
* Fixed multiple NullPointerExceptions
