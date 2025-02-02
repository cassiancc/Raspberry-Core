# Raspberry Core

<a href='https://files.minecraftforge.net'><img alt="forge" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/forge_vector.svg"></a>

This is a collection of minor tweaks for the [Raspberry Flavoured](https://www.curseforge.com/minecraft/modpacks/raspberry-flavoured) modpack, focusing on compatibility and fixes for issues in older mods.

## Installation

Raspberry Core is a client and server mod for Forge 1.19.2. While it was created for use with the [Raspberry Flavoured](https://www.curseforge.com/minecraft/modpacks/raspberry-flavoured) modpack, dependencies have been kept to a minimum to allow it to be used elsewhere.

#### Required Dependencies
- [Farmer's Delight](https://modrinth.com/mod/farmers-delight) is required.
- [Copper and Tuff Backport](https://www.curseforge.com/minecraft/mc-mods/copper-tuff-backport-fabric) is required.

#### Optional Dependencies
- [Environmental](https://modrinth.com/mod/environmental) is recommended.
- [Twigs](https://modrinth.com/mod/twigs) is recommended.
- [Supplementaries](https://modrinth.com/mod/supplementaries) is recommended.
- [Oreganized](https://modrinth.com/mod/oreganized) is recommended.
- [Caverns and Chasms](https://modrinth.com/mod/caverns-and-chasms) is optional.
- [Aquaculture 2](https://modrinth.com/mod/aquaculture) is optional.
- [Brewing and Chewin](https://www.curseforge.com/minecraft/mc-mods/brewin-and-chewin) is optional.
- [Sully's Mod](https://modrinth.com/mod/sullysmod) is optional.
- [Create](https://modrinth.com/mod/create) is optional.
- [Upgrade Aquatic](https://modrinth.com/mod/upgrade-aquatic) is optional.
- [Neapolitan](https://modrinth.com/mod/neapolitan) is optional.
- [Another Furniture](https://modrinth.com/mod/another-furniture) is optional.
- [Cloth Config](https://modrinth.com/mod/cloth-config) is recommended to configure the mod.


## Additions

### Blocks
- Ash Ovens, crafted from Ash Bricks (Supplementaries).
- Silt Ovens, crafted from Silt Bricks (Twigs).
- Lead Grate, crafted from Lead Blocks (Oreganized)

### Fixes
- Jerky (Brewin and Chewin) and other items without a fluid no longer cause a ticking block entity crash.
- Pikes, Thrashers, and Great Thrashers (Upgrade Aquatic) should no longer cause index out of bound crashes when grabbing players.
- Braziers (Caverns and Chasms) no longer produce fire sounds when unlit.
- Grindstones no longer display missing item textures when the last item in a stack is polished (Sully's Mod).
- Pigs no longer follow players with an empty hand.
- Items with empty NBT will now combine with items without NBT.
- Dimensional Tears (Spelunkery) will now stack correctly.

### Configurable Tweaks
- Stoves (Farmer's Delight) start unlit.
- Campfires start unlit.
- Braziers (Caverns and Chasms) start unlit.
- World version is hidden.
- Glow Goop (Naturalist) has its tooltip hidden.

### Datapackable Tweaks
- Thief Hoods (Environmental) no longer generate on skeletons.
  - Disable by overwriting `data/raspberry/tags/items/disabled.json`
- Healer Pouches (Environmental) no longer generate.
    - Disable by overwriting `data/environmental/tags/worldgen/structure/has_healer_pouch.json`

### Tweaks
- Fishing Rods (Aquaculture 2) show their bait as a tooltip, and can hold up to a stack. Bait items can also be moved freely in and out of rods.
- Bait (Aquaculture 2) decreases with each successful fishing reel, instead of relying on durability.
- Flowing Honey fluid (Create) makes Ochrum with Lava instead of Limestone.
- Yak Pants (Environmental) now uses Forge's step height modifier instead of its own.
- Agility (Neapolitan) now uses Forge's step height modifier as well as its climbing boost.

### Compatibility Changes
- Twigs (Twigs) can now light Stoves (Farmer's Delight).
- Cabinets (Farmer's Delight), Drawers (Another Furniture), Sacks and Safes (Supplementaries) can be used as Mounted Storage (Create).

## License
[![Asset license (Unlicensed)](https://img.shields.io/badge/assets%20license-All%20Rights%20Reserved-red.svg?style=flat-square)](https://en.wikipedia.org/wiki/All_rights_reserved)
[![Code license (MIT)](https://img.shields.io/badge/code%20license-MIT-green.svg?style=flat-square)](https://github.com/cassiancc/Raspberry-Core/blob/main/LICENSE.txt)



If you are thinking about using the code or assets from Raspberry Core, please note the mod's licensing. **All assets of Raspberry Core are unlicensed and all rights are reserved to them by their respective authors.** The source code of Raspberry Core mod for Minecraft 1.19.2 is under the MIT license.
