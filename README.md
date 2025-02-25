# Raspberry Core

<a href='https://files.minecraftforge.net'><img alt="forge" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/forge_vector.svg"></a>

This is a collection of minor tweaks for the [Raspberry Flavoured](https://www.curseforge.com/minecraft/modpacks/raspberry-flavoured) modpack, focusing on compatibility and fixes for issues in older mods, as well as a few minor features that work best in a full mod.

## Installation

Raspberry Core is a client and server mod for Forge 1.19.2. While it was created for use with the [Raspberry Flavoured](https://www.curseforge.com/minecraft/modpacks/raspberry-flavoured) modpack, dependencies have been kept to a minimum to allow it to be used elsewhere.

#### Required Dependencies
- [Farmer's Delight](https://modrinth.com/mod/farmers-delight) is required.
- [Blueprint](https://modrinth.com/mod/blueprint) is required.

#### Optional Dependencies
- [Copper and Tuff Backport](https://www.curseforge.com/minecraft/mc-mods/copper-tuff-backport-fabric) is recommended.
- [Copperized](https://www.curseforge.com/minecraft/mc-mods/copperized) is recommended.
- [CoFH Core](https://www.curseforge.com/minecraft/mc-mods/cofh-core) is recommended.
- [Environmental](https://modrinth.com/mod/environmental) is recommended.
- [Twigs](https://modrinth.com/mod/twigs) is recommended.
- [Supplementaries](https://modrinth.com/mod/supplementaries) is recommended.
- [Oreganized](https://modrinth.com/mod/oreganized) is recommended.
- [Aquaculture 2](https://modrinth.com/mod/aquaculture) is optional.
- [Another Furniture](https://modrinth.com/mod/another-furniture) is optional.
- [Brewin and Chewin](https://www.curseforge.com/minecraft/mc-mods/brewin-and-chewin) is optional.
- [Caverns and Chasms](https://modrinth.com/mod/caverns-and-chasms) is optional.
- [Clash](https://www.curseforge.com/minecraft/mc-mods/clash) is optional.
- [Create](https://modrinth.com/mod/create) is optional.
- [Dynamic Crosshair](https://modrinth.com/mod/dynamiccrosshair) is optional.
- [Gliders](https://modrinth.com/mod/gliders) is optional.
- [Neapolitan](https://modrinth.com/mod/neapolitan) is optional.
- [Quark](https://modrinth.com/mod/quark) is optional.
- [Sully's Mod](https://modrinth.com/mod/sullysmod) is optional.
- [Upgrade Aquatic](https://modrinth.com/mod/upgrade-aquatic) is optional.
- [Cloth Config](https://modrinth.com/mod/cloth-config) is recommended to configure the mod.


## Additions

### Blocks
- Ash Ovens, crafted from Ash Bricks (Supplementaries).
- Silt Ovens, crafted from Silt Bricks (Twigs).
- Lead Grate, crafted from Lead Blocks (Oreganized)

### Features
- Copper Armour (Copperized) will grant the new Aftershock effect when struck by lightning. This effect increases attack speed and movement speed.
- Copper Armour (Copperized) will grant Lightning Resistance (CoFH Core) effect.

### Fixes
- Jerky (Brewin and Chewin) and other items without a fluid no longer cause a ticking block entity crash.
- Pikes, Thrashers, and Great Thrashers (Upgrade Aquatic) should no longer cause index out of bound crashes when grabbing players.
- Braziers (Caverns and Chasms) no longer produce fire sounds when unlit.
- Grindstones no longer display missing item textures when the last item in a stack is polished (Sully's Mod).
- Pigs no longer follow players with an empty hand.
- Items with empty NBT will now combine with items without NBT.
- Greatblades (Clash!) now take one durability damage on any successful charged attack.
- Dimensional Tears (Spelunkery) will now stack correctly.

### Configurable Tweaks
- Stoves (Farmer's Delight) start unlit.
- Nether Stoves (My Nether's Delight) start unlit.
- Campfires start unlit.
- Braziers (Caverns and Chasms) start unlit.
- World version is hidden.
- Glow Goop (Naturalist) has its tooltip hidden.
- Gliders (Gliders) no longer attract lightning.

### Datapackable Tweaks
- Thief Hoods (Environmental) no longer generate on skeletons.
  - Disable by overwriting `data/raspberry/tags/items/disabled.json`
- Healer Pouches (Environmental) no longer generate.
  - Disable by overwriting `data/environmental/tags/worldgen/structure/has_healer_pouch.json`
- Dynamic Crosshair can now show what Shears should be able to mine.
  - Edit this list by changing `data/raspberry/tags/blocks/shears_should_mine`
- Dynamic Crosshair can now show what Shears should be able to be used on.
  - Edit this list by changing `data/raspberry/tags/blocks/shears_should_use`

### Tweaks
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

### Compatibility Changes
- Twigs (Twigs) can now light Stoves (Farmer's Delight).
- Cabinets (Farmer's Delight), Drawers (Another Furniture), Sacks and Safes (Supplementaries) can be used as Mounted Storage (Create).
- Golden Tools will show their harvest level correctly in Dynamic Crosshair when it has been changed by Quark.
- Gliders (Gliders) can be right-clicked in the inventory to equip them (Quark).
- Gliders (Gliders) can be right-clicked to swap them with armour (Survivality).

## License
[![Asset license (Unlicensed)](https://img.shields.io/badge/assets%20license-All%20Rights%20Reserved-red.svg?style=flat-square)](https://en.wikipedia.org/wiki/All_rights_reserved)
[![Code license (MIT)](https://img.shields.io/badge/code%20license-MIT-green.svg?style=flat-square)](https://github.com/cassiancc/Raspberry-Core/blob/main/LICENSE.txt)



If you are thinking about using the code or assets from Raspberry Core, please note the mod's licensing. **All assets of Raspberry Core are unlicensed and all rights are reserved to them by their respective authors.** The source code of Raspberry Core mod for Minecraft 1.19.2 is under the MIT license.
