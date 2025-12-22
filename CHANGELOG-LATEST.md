### Added
- New config settings to:
  - Hide enchantments
  - Hide potion types
  - Hide tooltips
  - Change creative tab icons
- Better jukeboxes config option:
    - Fades out vanilla music when playing a jukebox (supports Etched).
    - Changes jukebox sound to be non-directional.
- Setting in music and sounds menu to control jukebox fade distance.
- Config option to disable Ecologics penguins dropping feathers.
- New item tag to hide items from the creative menu.
- Items dragged from EMI into the inventory now are added to the inventory (as long as the player is an operator).

### Changed
- Removed leash backport in favor of Vanilla Backport's implementation.
- Temporarily removed the ability to leash fences.

### Fixed
- Swap Arrows can no longer force Withers into boats.
- Vertical leashes now render properly.
- Leads now connect to the front of boats.
- Boat leash connections persist on world reload.
- Crash on dedicated server.
- Music and sound settings being broken on 1.20.
- Tablet recipes now work on 1.20.
