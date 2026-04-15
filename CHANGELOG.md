# Unreleased (v3.0)

**Pickpocketing**
- Removed pickpocketing minigame and replaced it with a staged rummaging process.
  - The thief clicks an inventory menu button to expand the number of rows available to
  steal from. This is like searching difference pockets of the victim.

**Skills**
- Added skills systems
  - `Quickness`: the time it takes for an item to transfer from the victims inventory to the thieves.
  - `Reveal`: the number of items revealed in a single rummage inventory row.
  - `Memory`: affects the chance of an item remaining in the previous row after rummaging again.
  - `Speed`: the time it takes for the rummage inventory to open.

**Commands**
- `/pickpocket skills`: opens skills inventory menu
- `/pickpocket setskill <skill name> <level>`: sets the level of a skill
