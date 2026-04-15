# Unreleased (post-v3.0)
* **Victim inventory open now catches active rummaging thieves.**
  If a victim opens an inventory while the thief is in the rummage UI, both players hear a caught cue at the thief's location, the thief's rummage inventory closes immediately, and the session ends.
* **Pickpocket attempt now uses delay-matched bone meal audio feedback.**
  Starting a delayed pickpocket no longer sends the attempt chat line; it now plays `minecraft:item.bone_meal.use`, with pitch scaling across the computed pre-rummage delay window (3s to configured minimum), from default pitch at longer waits to higher pitch at shorter waits.
* **Memory-based forgetting now scales by chance.**
  On rummage expansion, each revealed item in previous rows is now forgotten using a linear Memory-based chance (level 0 => 100%, level 100 => 0%), while level 0 still guarantees all eligible previous-row reveals are forgotten.
* **Fixed Memory level 0 retention during early expansions.**
  Expanding rummage now clears all previously revealed slots from earlier rows when Memory is level 0, preventing old-row items from lingering after the first and second expansions.
* **Added a skills menu command.**
  Players can now run `/pickpocket skills` to open an inventory menu showing their Speed, Reveal, and Memory skill levels.
* **Gradle: `deploy` replaces `deployToSpigot`.**
  The distribution task that builds and copies the plugin JAR to `spigotPluginsDir` is now named `deploy`.
* **Replaced the old minigame with staged rummage stealing.**
  Pickpocketing now progresses through a staged rummage flow rather than the previous minigame implementation.
* **Improved rummage pacing and feedback.**
  Rummage expansion now ramps and uses shuffled audio cues to make repeated attempts feel less repetitive.
* **Rummage expansion grid rules and slot visuals.**
  After expanding, earlier menu rows no longer receive new victim-item reveals; only slots in newly added rows can. Each former expand-chest cell becomes a dead gray pane. Slots forgotten by memory or steals use blue panes and never accept a new mapping for that session. Mappings cleared because the victim slot is empty stay white panes only.
* **Rummage reveals are now row-local per expansion.**
  Reveals are now added only for the newly active row (including the initial row), so old rows are never backfilled after memory forgets or steals.
* **Reveal skill now scales 1:1 by level (capped at 8 per row).**
  Reveal level now directly sets how many slots are revealed per active row (level 1 => 1, level 2 => 2, ...), capped at 8 revealable slots per row.
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