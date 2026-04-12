# Unreleased (post-v3.0)
* **Replaced the old minigame with staged rummage stealing.**
  Pickpocketing now progresses through a staged rummage flow rather than the previous minigame implementation.
* **Improved rummage pacing and feedback.**
  Rummage expansion now ramps and uses shuffled audio cues to make repeated attempts feel less repetitive.
* **Fishing-rod triggered pickpocketing has been removed.**
  Pickpocketing can no longer be initiated by fishing hook hits and now only starts through the standard interaction flow.
* **Refactored session, skill, and message systems.**
  Runtime internals were reorganized around the session manager, skill module, and `MessageConfig` to simplify ongoing development.
* **Maintenance and documentation updates.**
  Added Javadocs across runtime classes and applied small repository housekeeping updates.

# v3.0
* **Major v3.0 release baseline.**
  Introduced the v3.0 codebase and release packaging as the foundation for subsequent updates.

# v2.8
* **Added profile configurations for thieves.**
  Before this there was one general configuration where you could adjust cooldowns, the number of items when rummaging, and so forth.. Now you can create several of these configurations and assign these profiles to players via permissions or by using `/pickpocket profile assign thief <profile> <player>`.
* **Added comments to the configuration file.**
  There were comments a few updates ago, but now they're back and more helpful than before.
* **Can use `*` and `-` for the disabled items list.**
  Specify an asterisk to add all items to the disabled items list, and prefix a disabled item with a hyphen to enable it instead.
  This can be used to change the disabled item list into an enabled items list instead.