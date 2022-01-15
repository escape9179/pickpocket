# v2.8
* **Added a configuration option to enable fishing rod pickpocketing.**
  This allows a player to initiate pickpocketing by using a fishing rod instead of sneak right-clicking.
* **Added profile configurations for thieves.**
  Before this there was one general configuration where you could adjust cooldowns, the number of items when rummaging, and so forth.. Now you can create several of these configurations and assign these profiles to players via permissions or by using `/pickpocket profile assign thief <profile> <player>`.
* **Added comments to the configuration file**.
  There were comments a few updates ago, but now they're back and more helpful than before.
* **Can use `*` and `-` for the disabled items list, similar to how you can use them when defining user permissions.**
  Specify an asterisk to add all items to the disabled items list, and prefix a disabled item with a hyphen to enable it instead.
  This can be used to change the disabled item list into an enabled items list instead.
