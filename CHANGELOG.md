# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Storage block for Ash (Supplementaries).

### Changed
- Moved Blueprint to an optional dependency.

### Fixed
- Backported fix for players deploying gliders in boats (Gliders).  
- Foxes no longer spawn with Rabbit's Feet.
- Launching no longer shows as an option for axes (RF/EMI).
- Tangle roots show that they're shearable. (Dynamic Crosshair)
- Deployers not being able to use modded shears. (Create) (SarahIsWeird)

## [1.4] - 2024-05-07

### Fixed
- Issue with Useful Spyglass's keybind.

## [1.3] - 2024-05-07

### Added
- Snow layer block that obeys gravity.
- Hardcoded EMI integration for Better Beacons, as running it through JEMI causes visual errors, especially on dedicated servers.
- Spyglasses can now be used from a bundle when Spyglass Improvements is installed.
- Backported trowel blacklist from Quark 1.20.
- Saddled horses no longer wander.
- Horses can now step up path blocks.

### Changed
- Tweaked Ash Stove Side texture (Kayla_the_Bee)
- Tweaked Moody Wildflower texture (ProbablyEkho)

### Fixed
- Replaced redirect in Spyglass Improvements to fix compatibility with Controllable.
- Rich Soil Farmland no longer converts to Dirt when trampled. (@SarahIsWeird)
- Rich Soil Farmland no longer prevents fall damage. (@SarahIsWeird)

### Removed
- Overlay functionality, as it is now part of Immersive Overlays.

## [1.2] - 2024-04-04

### Fixed
- Magnetic and Recovery compasses can now display a coordinate overlay.
- Lit Ash and Silt stoves now have correct textures by Kayla_the_Bee and ProbablyEkho.
- Tether warp now shows the correct tablet in EMI.

## [1.1] - 2024-04-03

### Fixed
- Crash when viewing flowerbeds.
- Swift Sneak being applied to boots instead of leggings.

## [1.0] - 2024-04-03

### Added
- Added Cheery and Moody Wildflowers, new flowerbed blocks textured and modelled by ProbablyEkho and inspired by the Spring to Life drop.
- Added Pink Petals, textured by ProbablyEkho and inspired by the Trails and Tales update.
- Added Clovers, a new natural block textured and modelled by ProbablyEkho.
- Compasses now show a coordinate overlay (X and Z) when in a player's inventory.
- Depth Gauges now show a coordinate overlay (Y) when in a player's inventory.
  - This applies to the items from Spelunkery and Caverns and Chasms. When neither mod is present, a compass can be used instead.
- Clocks now show a time overlay when in a player's inventory.
- Added EMI integration to enchanting tablets from Raspberry Flavoured.
- Added EMI integration to runes from Quark.
- Goggles (Create) can be right-clicked in the inventory to equip them (Quark).
- Goggles (Create) can be right-clicked to swap them with armour (Survivality).

### Changed
- Snakes (Naturalist) can no longer drop a Rabbit's Foot.
- Coconut Slices (Ecologics) clear one effect, instead of all of them.

### Fixed
- Screenshot Viewer no longer causes a crash when paused via F3-Esc.
- Swapping gliders with armour functions again.

## [0.1.13] - 2024-03-26

### Added
- Pointed Dripstone stalactites can now convert Dripstone blocks below into Rock Salt blocks.

### Fixed
- Alloyed encased blocks now tick correctly.
- Potential fix for dying when gliding causing a crash and softlock.
- Farmer's Delight EMI integration is now translated correctly.
- Raspberry Core Stoves are now valid workstations in EMI.

## [0.1.12] - 2024-03-20

### Added
- Direct compatibility between Farmer's Delight and EMI based off [the work of MerchantCalico](https://github.com/vectorwing/FarmersDelight/pull/971).

### Changed
- Bomb (Supplementaries) rarity has been changed - only blue bombs are considered rare.

### Fixed
- Additional null safety for unlit blocks.
- Further safety fixes for Create blastproofing.
- Further fixes for disabled items.

## [0.1.11] - 2024-02-27

### Fixed
- Crashes from making item entities blastproof.

## [0.1.10] - 2024-02-27

### Added
- Dynamic Crosshair can now show what shears can be used on and what they can mine.
- Dynamic Crosshair can now show what knives can be used on and what they can mine.
- Dynamic Crosshair can now show what axes can be used on.
- Wormy Dirt texture by raspmary.
- Deepslate Gravel and Raked Deepslate Gravel (textures by raspmary)
- Blackstone Gravel and Raked Blackstone Gravel (textures by raspmary)
- Copper Doors (Copper and Tuff Backport) can now be opened as double doors (Quark)
- Common seeds found in Raspberry Flavoured (Farmer's Delight, Cultural Delights, etc.) can now be used to breed chickens.
- Configurable tweak - blasting an item with lava that has no blasting recipe will no longer destroy the item. (Create)

### Fixed
- Further fixes for Thrashers.
- Yak Pants should no longer cause a game crash with newer versions of Environmental.
- Kinetic damage should no longer cause a server crash.
- Ender Torches (Endergetic Expansion) now give the correct particles.

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