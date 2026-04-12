v3.0

# v2.8
* **Fishing rod pickpocketing has been removed.**
  Pickpocketing can no longer be initiated by fishing hook hits and now only starts through the standard interaction flow.
* **Added profile configurations for thieves.**
  Before this there was one general configuration where you could adjust cooldowns, the number of items when rummaging, and so forth.. Now you can create several of these configurations and assign these profiles to players via permissions or by using `/pickpocket profile assign thief <profile> <player>`.
* **Added comments to the configuration file.**
  There were comments a few updates ago, but now they're back and more helpful than before.
* **Can use `*` and `-` for the disabled items list.**
  Specify an asterisk to add all items to the disabled items list, and prefix a disabled item with a hyphen to enable it instead.
  This can be used to change the disabled item list into an enabled items list instead.