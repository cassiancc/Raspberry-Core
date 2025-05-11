package cc.cassian.raspberry.registry;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import static cc.cassian.raspberry.RaspberryMod.locate;

public class RaspberryTags {
    public static final TagKey<Item> DISABLED = createItemTag("disabled");
    public static final TagKey<Item> BAIT = createItemTag("bait");
    public static final TagKey<Item> BAD_BAIT = createItemTag("bad_bait");
    public static final TagKey<Item> MID_BAIT = createItemTag("mid_bait");
    public static final TagKey<Item> GOOD_BAIT = createItemTag("good_bait");

    public static final TagKey<Item> HAS_KINETIC_DAMAGE = createItemTag( "has_kinetic_damage");

    public static final TagKey<Item> WORM_SEEKER_ITEMS = createItemTag( "worm_seeker_items");
    public static final TagKey<EntityType<?>> WORM_SEEKERS = createEntityTypeTag( "worm_seekers");

    public static final TagKey<Block> SHEARS_SHOULD_MINE = createBlockTag("mineable/shears");
    public static final TagKey<Block> SHEARS_SHOULD_USE = createBlockTag("useable/shears");
    public static final TagKey<Block> KNIVES_SHOULD_USE = createBlockTag("useable/knives");
    public static final TagKey<Block> AXES_SHOULD_USE = createBlockTag("useable/axes");
    public static final TagKey<Block> HOES_SHOULD_USE = createBlockTag("useable/hoes");

    public static final TagKey<Block> INTERACTABLE_METAL_DOORS = createBlockTag("interactable_metal_doors");

    public static final TagKey<Block> CONVERTS_TO_SALT = createBlockTag("converts_to_salt");

    public static final TagKey<Item> HORSE_FOOD = createItemTag("horse_food");
    public static final TagKey<Item> CONTAINERS = createItemTag("container");
    public static final TagKey<Item> TROWEL_BLACKLIST = createItemTag("trowel_blacklist");

    private static TagKey<Block> createBlockTag(String id) {
        return TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(), locate(id));
    }

    private static TagKey<Item> createItemTag(String id) {
        return TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), locate(id));
    }

    private static TagKey<EntityType<?>> createEntityTypeTag(String id) {
        return TagKey.create(ForgeRegistries.ENTITY_TYPES.getRegistryKey(), locate(id));
    }
}
