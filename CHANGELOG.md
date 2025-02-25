# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Dynamic Crosshair can now show what shears can be used on and what they can mine.
- Dynamic Crosshair can now show what knives can be used on and what they can mine.
- Dynamic Crosshair can now show what axes can be used on.
- Wormy Dirt texture by raspmary.

### Fixed
- Further fixes for Thrashers.
- Yak Pants should no longer cause a game crash with newer versions of Environmental.
- Kinetic damage should no longer cause a server crash.

## [0.1.4] - 2024-04-18

### Added
- Ash Ovens, crafted from Ash Bricks (Supplementaries).
- Silt Ovens, crafted from Silt Bricks (Twigs).
- Lead Grate, crafted from Lead Blocks (Oreganized)
- Copper Armour (Copperized) will grant the new Aftershock effect when struck by lightning. This effect increases attack speed and movement speed.
- Copper Armour (Copperized) will grant Lightning Resistance (CoFH Core) effect.
- Stoves (Farmer's Delight) start unlit.
- Nether Stoves (My Nether's Delight) start unlit.
- Campfires start unlit.
- Braziers (Caverns and Chasms) start unlit.
- World version is hidden.
- Glow Goop (Naturalist) has its tooltip hidden.
- Gliders (Gliders) no longer attract lightning.
- Thief Hoods (Environmental) no longer generate on skeletons.
    - Disable by overwriting `data/raspberry/tags/items/disabled.json`
- Healer Pouches (Environmental) no longer generate.
    - Disable by overwriting `data/environmental/tags/worldgen/structure/has_healer_pouch.json`
- Fishing Rods (Aquaculture 2) show their bait as a tooltip, and can hold up to a stack. Bait items can also be moved freely in and out of rods.
- Bait (Aquaculture 2) decreases with each successful fishing reel, instead of relying on durability.
- Bait (Aquaculture 2) now uses three different item tags, instead of being relegated to instances of BaitItem.
- Flowing Honey fluid (Create) makes Ochrum with Lava instead of Limestone.
- Yak Pants (Environmental) now uses Forge's step height modifier instead of its own.
- Agility (Neapolitan) now uses Forge's step height modifier as well as its climbing boost.
- Knives (Farmers Delight) now mine Bamboo instantly like in 1.20+.
- Torch Arrows (Quark) can now be fired out of Dispensers like in 1.20+.
- Electrum Weapons (Oreganized) now deal kinetic damage like in 1.20+.
- Gliders (Gliders) make a noise when they are equipped.
- Twigs (Twigs) can now light Stoves (Farmer's Delight).
- Cabinets (Farmer's Delight), Drawers (Another Furniture), Sacks and Safes (Supplementaries) can be used as Mounted Storage (Create).
- Golden Tools will show their harvest level correctly in Dynamic Crosshair when it has been changed by Quark.
- Gliders (Gliders) can be right-clicked in the inventory to equip them (Quark).
- Gliders (Gliders) can be right-clicked to swap them with armour (Survivality).


### Fixed
- Jerky (Brewin and Chewin) and other items without a fluid no longer cause a ticking block entity crash.
- Pikes, Thrashers, and Great Thrashers (Upgrade Aquatic) should no longer cause index out of bound crashes when grabbing players.
- Braziers (Caverns and Chasms) no longer produce fire sounds when unlit.
- Grindstones no longer display missing item textures when the last item in a stack is polished (Sully's Mod).
- Pigs no longer follow players with an empty hand.
- Items with empty NBT will now combine with items without NBT.
- Greatblades (Clash!) now take one durability damage on any successful charged attack.
- Dimensional Tears (Spelunkery) will now stack correctly.